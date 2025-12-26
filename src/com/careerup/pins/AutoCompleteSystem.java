package com.careerup.pins;

import java.util.*;

public class AutoCompleteSystem {

    private static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        List<String> hot = new ArrayList<>(); // top 3 sentences for this prefix
    }

    private final TrieNode root = new TrieNode();
    private final Map<String, Integer> freq = new HashMap<>();
    private StringBuilder current = new StringBuilder();

    public AutoCompleteSystem(String[] sentences, int[] times) {
        for (int i = 0; i < sentences.length; i++) {
            freq.put(sentences[i], times[i]);
            addSentence(sentences[i]);
        }
    }

    private void addSentence(String sentence) {
        TrieNode node = root;
        for (char c : sentence.toCharArray()) {
            node = node.children.computeIfAbsent(c, k -> new TrieNode());
            updateHotList(node.hot, sentence);
        }
    }

    private void updateHotList(List<String> hot, String sentence) {
        if (!hot.contains(sentence)) {
            hot.add(sentence);
        }
        hot.sort((a, b) -> {
            int fa = freq.get(a);
            int fb = freq.get(b);
            if (fa != fb)
                return fb - fa; // higher freq first
            return a.compareTo(b); // lexicographically
        });
        if (hot.size() > 3)
            hot.remove(hot.size() - 1);
    }

    public List<String> input(char c) {
        if (c == '#') {
            String sentence = current.toString();
            freq.put(sentence, freq.getOrDefault(sentence, 0) + 1);
            addSentence(sentence);
            current = new StringBuilder();
            return Collections.emptyList();
        }

        current.append(c);
        TrieNode node = root;

        for (char ch : current.toString().toCharArray()) {
            if (!node.children.containsKey(ch)) {
                return Collections.emptyList();
            }
            node = node.children.get(ch);
        }

        return node.hot;
    }

    public static void main(String[] args) {
        String[] sentences = { "i love you", "island", "ironman", "i love leetcode" };
        int[] times = { 5, 3, 2, 2 };

        AutoCompleteSystem ac = new AutoCompleteSystem(sentences, times);

        System.out.println(ac.input('i')); // ["i love you", "island", "i love leetcode"]
        System.out.println(ac.input(' ')); // ["i love you", "i love leetcode"]
        System.out.println(ac.input('a')); // []
        System.out.println(ac.input('#')); // []
    }

}
