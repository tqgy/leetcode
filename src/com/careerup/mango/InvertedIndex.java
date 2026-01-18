package com.careerup.mango;

import java.util.*;
import java.util.concurrent.*;

/**
 * Full positional inverted index with:
 *  - phrase query support
 *  - multithreaded index construction
 */
public class InvertedIndex {

    // word -> (docId -> list of positions)
    // Using ConcurrentHashMap for thread safety during concurrent builds
    private final Map<String, Map<Integer, List<Integer>>> invertedIndex = new ConcurrentHashMap<>();
    
    // forward index: docId → word → positions
    // This is primarily used for efficient document deletion (retrieving all words for a given docId).
    private final Map<Integer, Map<String, List<Integer>>> forwardIndex = new ConcurrentHashMap<>();

    // -----------------------------
    // Document model
    // -----------------------------
    public record Document(int id, List<String> words) {}

    // -----------------------------
    // Single-threaded index builder
    // -----------------------------
    public void buildIndex(List<Document> docs) {
        for (Document doc : docs) {
            int docId = doc.id();
            List<String> words = doc.words();
            forwardIndex.putIfAbsent(docId, new HashMap<>());

            for (int pos = 0; pos < words.size(); pos++) {
                String word = words.get(pos);

                invertedIndex
                    .computeIfAbsent(word, k -> new HashMap<>())
                    .computeIfAbsent(docId, k -> new ArrayList<>())
                    .add(pos);

                forwardIndex.get(docId)
                    .computeIfAbsent(word, k -> new ArrayList<>())
                    .add(pos);
            }
        }
    }

    // -----------------------------
    // Phrase query evaluator
    // -----------------------------
    /**
     * Searches for documents containing the specific phrase (words in order).
     * @param phrase the phrase to search for (e.g., "quick brown")
     * @return list of document IDs containing the phrase
     */
    public List<Integer> phraseQuery(String phrase) {
        String[] words = phrase.split("\\s+");
        if (words.length == 0) return List.of();

        // Step 1: get posting lists - List<Map<docId, List<positions>>>
        List<Map<Integer, List<Integer>>> postings = new ArrayList<>();
        for (String word : words) {
            Map<Integer, List<Integer>> p = invertedIndex.get(word);
            if (p == null) return List.of(); // word not found
            postings.add(p);
        }

        // Step 2: intersect docIds
        Set<Integer> candidateDocs = new HashSet<>(postings.get(0).keySet());
        for (int i = 1; i < postings.size(); i++) {
            candidateDocs.retainAll(postings.get(i).keySet());
            if (candidateDocs.isEmpty()) return List.of();
        }

        // Step 3: check positional alignment
        List<Integer> result = new ArrayList<>();
        for (int docId : candidateDocs) {
            if (matchesPhraseInDoc(docId, postings)) {
                result.add(docId);
            }
        }

        return result;
    }

    private boolean matchesPhraseInDoc(int docId, List<Map<Integer, List<Integer>>> postings) {
        // postings - List<Map<docId, List<positions>>>
        // Get positions of the first word
        List<Integer> firstWordPositions = postings.get(0).get(docId);
        
        // For each position of the first word, check if subsequent words align
        // e.g., for position p of word1, check if word2 has p+1, word3 has p+2, etc.
        // If any alignment works, return true
        for (int startPos : firstWordPositions) {
            boolean ok = true;

            for (int i = 1; i < postings.size(); i++) {
                // Check if the i-th word has position startPos + i
                List<Integer> positions = postings.get(i).get(docId);
                if (!positions.contains(startPos + i)) {
                    ok = false;
                    break;
                }
            }

            if (ok) return true;
        }

        return false;
    }

    // -----------------------------
    // Multithreaded index builder
    // -----------------------------
    public record IndexResult(
        Map<String, Map<Integer, List<Integer>>> inverted,
        Map<Integer, Map<String, List<Integer>>> forward) {}

    // This approach partitions the documents, builds partial indexes in parallel,
    // and then merges the partial indexes into a global index.
    // The benefit is reduced contention since each thread works on its own local data.
    public IndexResult buildIndexParallel(
            List<Document> docs, int numThreads) throws InterruptedException {

        List<List<Document>> partitions = partition(docs, numThreads);

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<IndexResult>> futures = new ArrayList<>();

        for (List<Document> part : partitions) {
            futures.add(executor.submit(() -> buildPartialIndex(part)));
        }

        Map<String, Map<Integer, List<Integer>>> globalInverted = new HashMap<>();
        Map<Integer, Map<String, List<Integer>>> globalForward = new HashMap<>();

        for (Future<IndexResult> f : futures) {
            try {
                IndexResult res = f.get();
                merge(globalInverted, globalForward, res);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        executor.shutdown();
        return new IndexResult(globalInverted, globalForward);
    }

    private List<List<Document>> partition(List<Document> docs, int n) {
        List<List<Document>> parts = new ArrayList<>();
        int size = docs.size();
        int chunk = (size + n - 1) / n;

        for (int i = 0; i < size; i += chunk) {
            parts.add(docs.subList(i, Math.min(i + chunk, size)));
        }
        return parts;
    }

    private IndexResult buildPartialIndex(List<Document> docs) {
        Map<String, Map<Integer, List<Integer>>> localInverted = new HashMap<>();
        Map<Integer, Map<String, List<Integer>>> localForward = new HashMap<>();

        for (Document doc : docs) {
            int docId = doc.id();
            List<String> words = doc.words();
            localForward.putIfAbsent(docId, new HashMap<>());

            for (int pos = 0; pos < words.size(); pos++) {
                String word = words.get(pos);

                localInverted
                    .computeIfAbsent(word, k -> new HashMap<>())
                    .computeIfAbsent(docId, k -> new ArrayList<>())
                    .add(pos);

                localForward.get(docId)
                    .computeIfAbsent(word, k -> new ArrayList<>())
                    .add(pos);
            }
        }

        return new IndexResult(localInverted, localForward);
    }

    private void merge(Map<String, Map<Integer, List<Integer>>> globalInverted,
                       Map<Integer, Map<String, List<Integer>>> globalForward,
                       IndexResult partial) {

        // Merge inverted
        for (var entry : partial.inverted().entrySet()) {
            String word = entry.getKey();
            Map<Integer, List<Integer>> partialPosting = entry.getValue();

            for (var e : partialPosting.entrySet()) {
                int docId = e.getKey();
                List<Integer> positions = e.getValue();
                globalInverted.computeIfAbsent(word, k -> new HashMap<>())
                              .computeIfAbsent(docId, k -> new ArrayList<>())
                              .addAll(positions);
            }
        }

        // Merge forward
        globalForward.putAll(partial.forward());
    }

    // -----------------------------
    // Shared state (ConcurrentHashMap) builder
    // -----------------------------
    /**
     * Builds the index concurrently using a shared thread-safe map.
     * This method directly modifies the internal concurrent maps.
     * 
     * @param docs list of documents to index
     * @param numThreads number of threads to use
     * @throws InterruptedException if interrupted while waiting for completion
     */
    public void buildIndexConcurrent(List<Document> docs, int numThreads) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        for (Document doc : docs) {
            executor.submit(() -> {
                int docId = doc.id();
                List<String> words = doc.words();

                for (int pos = 0; pos < words.size(); pos++) {
                    String word = words.get(pos);

                    // Update inverted index: thread-safe computeIfAbsent
                    // The inner maps must also be concurrent or synchronized if strictly necessary, 
                    // but for simplified "ConcurrentHashMap" demo usually we rely on CHM's guarantees.
                    // However, standard CHM doesn't lock the VALUE (the inner map) for subsequent updates.
                    // Ideally, we use compute() for atomic updates or nested CHM.
                    invertedIndex
                        .computeIfAbsent(word, k -> new ConcurrentHashMap<>())
                        .computeIfAbsent(docId, k -> Collections.synchronizedList(new ArrayList<>()))
                        .add(pos);

                    // Update forward index
                    forwardIndex
                        .computeIfAbsent(docId, k -> new ConcurrentHashMap<>())
                        .computeIfAbsent(word, k -> Collections.synchronizedList(new ArrayList<>()))
                        .add(pos);
                }
            });
        }

        // Shutdown the executor, no more tasks will be submitted, 
        // but existing tasks will continue to run
        executor.shutdown();
        // Wait for all threads to finish
        executor.awaitTermination(1, TimeUnit.MINUTES);
    }

    public void addDocument(Document doc) {
        int docId = doc.id();
        List<String> words = doc.words();

        for (int pos = 0; pos < words.size(); pos++) {
            String word = words.get(pos);

            invertedIndex
                .computeIfAbsent(word, k -> new HashMap<>())
                .computeIfAbsent(docId, k -> new ArrayList<>())
                .add(pos);

            forwardIndex
                .computeIfAbsent(docId, k -> new HashMap<>())
                .computeIfAbsent(word, k -> new ArrayList<>())
                .add(pos);
        }
    }

    public void deleteDocument(int docId) {
        // Step 1: get all words for this doc
        Map<String, List<Integer>> words = forwardIndex.remove(docId);
        if (words == null) return; // doc not found

        // Step 2: remove docId from each word's posting list
        for (String word : words.keySet()) {
            Map<Integer, List<Integer>> posting = invertedIndex.get(word);
            if (posting != null) {
                posting.remove(docId);

                // optional: clean up empty words
                if (posting.isEmpty()) {
                    invertedIndex.remove(word, posting);
                }
            }
        }
    }

    // -----------------------------
    // Demo main method
    // -----------------------------
    public static void main(String[] args) throws Exception {
        InvertedIndex idx = new InvertedIndex();

        List<Document> docs = List.of(
            new Document(1, List.of("the", "quick", "brown", "fox")),
            new Document(2, List.of("the", "fox", "is", "quick")),
            new Document(3, List.of("quick", "brown", "dog"))
        );

        // Demo 1: Single-threaded
        System.out.println("--- Single-threaded ---");
        idx.buildIndex(docs);
        System.out.println(idx.phraseQuery("quick brown")); // [1, 3]

        // Demo 2: ConcurrentHashMap
        System.out.println("\n--- ConcurrentHashMap Parallel ---");
        InvertedIndex idxConcurrent = new InvertedIndex();
        idxConcurrent.buildIndexConcurrent(docs, 2);
        System.out.println(idxConcurrent.phraseQuery("quick brown")); // [1, 3]

        // Demo 3: Partition-Merge Parallel ---
        System.out.println("\n--- Partition-Merge Parallel ---");
        InvertedIndex idxPM = new InvertedIndex();
        IndexResult globalResult = idxPM.buildIndexParallel(docs, 2);
        // Set the global index manually for the demo
        idxPM.invertedIndex.putAll(globalResult.inverted());
        idxPM.forwardIndex.putAll(globalResult.forward());
        System.out.println(idxPM.phraseQuery("quick brown")); // [1, 3]

        // Demo 4: Delete document
        System.out.println("\n--- Delete document ---");
        idxPM.deleteDocument(3); // Deleted doc 3 to make output [1]
        System.out.println(idxPM.phraseQuery("quick brown")); // [1]

        // Demo 5: Add document back
        System.out.println("\n--- Add document back ---");
        idxPM.addDocument(new Document(3, List.of("quick", "brown", "dog")));
        System.out.println(idxPM.phraseQuery("quick brown")); // [1, 3]


        Map<String, Set<Integer>> invertedIndex = new HashMap<>();
        invertedIndex.put("quick", new HashSet<>(Set.of(1, 2)));
        invertedIndex.put("brown", new HashSet<>(Set.of(1, 3)));
        invertedIndex.put("fox",   new HashSet<>(Set.of(1, 2)));
        invertedIndex.put("dog",   new HashSet<>(Set.of(3)));

        System.out.println(invertedIndex);
        System.out.println(invertedIndex.get("quick"));
        System.out.println(invertedIndex.getOrDefault("quick", Set.of()));

        invertedIndex.values()
            .stream()
            .filter(s -> s.contains(3))
            .forEach(s -> s.remove(3));
        invertedIndex.values().removeIf(Set::isEmpty);
        System.out.println(invertedIndex);
        invertedIndex.get("quick").remove(2);
        System.out.println(invertedIndex);

        invertedIndex.entrySet().stream().max(Map.Entry.comparingByKey()).get().getKey();
        invertedIndex.entrySet().stream().max(Map.Entry.comparingByValue((a,b) -> a.size() - b.size())).get().getKey();

    }
}
