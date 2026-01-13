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
    private final Map<String, Map<Integer, List<Integer>>> invertedIndex = new HashMap<>();
    // forward index: docId → word → positions
    private final Map<Integer, Map<String, List<Integer>>> forwardIndex = new HashMap<>();

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
                String w = words.get(pos);

                invertedIndex
                    .computeIfAbsent(w, k -> new HashMap<>())
                    .computeIfAbsent(docId, k -> new ArrayList<>())
                    .add(pos);

                forwardIndex.get(docId)
                    .computeIfAbsent(w, k -> new ArrayList<>())
                    .add(pos);
            }
        }
    }

    // -----------------------------
    // Phrase query evaluator
    // -----------------------------
    public List<Integer> phraseQuery(String phrase) {
        String[] words = phrase.split("\\s+");
        if (words.length == 0) return List.of();

        // Step 1: get posting lists
        List<Map<Integer, List<Integer>>> postings = new ArrayList<>();
        for (String w : words) {
            Map<Integer, List<Integer>> p = invertedIndex.get(w);
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
        List<Integer> firstWordPositions = postings.get(0).get(docId);

        for (int startPos : firstWordPositions) {
            boolean ok = true;

            for (int i = 1; i < postings.size(); i++) {
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
                String w = words.get(pos);

                localInverted
                    .computeIfAbsent(w, k -> new HashMap<>())
                    .computeIfAbsent(docId, k -> new ArrayList<>())
                    .add(pos);

                localForward.get(docId)
                    .computeIfAbsent(w, k -> new ArrayList<>())
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

            Map<Integer, List<Integer>> globalPosting =
                    globalInverted.computeIfAbsent(word, k -> new HashMap<>());

            for (var e : partialPosting.entrySet()) {
                globalPosting
                    .computeIfAbsent(e.getKey(), k -> new ArrayList<>())
                    .addAll(e.getValue());
            }
        }

        // Merge forward
        globalForward.putAll(partial.forward());
    }

    // -----------------------------
    // Shared state (ConcurrentHashMap) builder
    // -----------------------------
    public void buildIndexConcurrent(List<Document> docs, int numThreads) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        for (Document doc : docs) {
            executor.submit(() -> {
                int docId = doc.id();
                List<String> words = doc.words();

                for (int pos = 0; pos < words.size(); pos++) {
                    String w = words.get(pos);

                    // Inner map must also be concurrent because multiple threads 
                    // process different docs but the same word.
                    invertedIndex
                        .computeIfAbsent(w, k -> new ConcurrentHashMap<>())
                        .computeIfAbsent(docId, k -> new ArrayList<>())
                        .add(pos);
                    // forward index entry 
                    forwardIndex
                        .computeIfAbsent(docId, k -> new ConcurrentHashMap<>())
                        .computeIfAbsent(w, k -> Collections.synchronizedList(new ArrayList<>()))
                        .add(pos);
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
    }

    public void addDocument(Document doc) {
        int docId = doc.id();
        List<String> words = doc.words();

        for (int pos = 0; pos < words.size(); pos++) {
            String w = words.get(pos);

            invertedIndex
                .computeIfAbsent(w, k -> new HashMap<>())
                .computeIfAbsent(docId, k -> new ArrayList<>())
                .add(pos);

            forwardIndex
                .computeIfAbsent(docId, k -> new HashMap<>())
                .computeIfAbsent(w, k -> new ArrayList<>())
                .add(pos);
        }
    }

    public void deleteDocument(int docId) {
        // Step 1: get all words for this doc
        Map<String, List<Integer>> words = forwardIndex.remove(docId);
        if (words == null) return; // doc not found

        // Step 2: remove docId from each word's posting list
        for (String w : words.keySet()) {
            Map<Integer, List<Integer>> posting = invertedIndex.get(w);
            if (posting != null) {
                posting.remove(docId);

                // optional: clean up empty words
                if (posting.isEmpty()) {
                    invertedIndex.remove(w, posting);
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
    }
}
