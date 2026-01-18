package com.careerup.mango;

import java.util.*;

public class InvertedIndexTest {
    
    public static void main(String[] args) {
        try {
            testBasicSearch();
            testConcurrency();
            testUpdates();
            System.out.println("ALL INVERTED INDEX TESTS PASSED");
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void testBasicSearch() {
        System.out.println("Running testBasicSearch...");
        InvertedIndex idx = new InvertedIndex();
        List<InvertedIndex.Document> docs = List.of(
            new InvertedIndex.Document(1, List.of("the", "quick", "brown", "fox")),
            new InvertedIndex.Document(2, List.of("the", "fox", "is", "quick")),
            new InvertedIndex.Document(3, List.of("quick", "brown", "dog"))
        );
        idx.buildIndex(docs);
        
        List<Integer> res = idx.phraseQuery("quick brown");
        if (!res.contains(1) || !res.contains(3) || res.size() != 2) {
            throw new RuntimeException("Basic search failed: " + res);
        }
        
        res = idx.phraseQuery("fox");
        if (!res.contains(1) || !res.contains(2) || res.size() != 2) {
            throw new RuntimeException("Single word search failed: " + res);
        }
    }

    private static void testConcurrency() throws Exception {
        System.out.println("Running testConcurrency...");
        InvertedIndex idx = new InvertedIndex();
        List<InvertedIndex.Document> docs = new ArrayList<>();
        // Create 1000 docs
        for (int i = 0; i < 1000; i++) {
            docs.add(new InvertedIndex.Document(i, List.of("common", "word", "doc" + i)));
        }
        
        idx.buildIndexConcurrent(docs, 10);
        
        List<Integer> res = idx.phraseQuery("common word");
        if (res.size() != 1000) {
            throw new RuntimeException("Concurrent build lost data. Expected 1000, got " + res.size());
        }
    }

    private static void testUpdates() {
        System.out.println("Running testUpdates...");
        InvertedIndex idx = new InvertedIndex();
        idx.addDocument(new InvertedIndex.Document(1, List.of("a", "b")));
        idx.addDocument(new InvertedIndex.Document(2, List.of("b", "c")));
        
        if (idx.phraseQuery("b").size() != 2) throw new RuntimeException("Add failed");
        
        idx.deleteDocument(1);
        if (idx.phraseQuery("b").size() != 1) throw new RuntimeException("Delete failed");
        if (!idx.phraseQuery("a").isEmpty()) throw new RuntimeException("Delete incomplete");
    }
}
