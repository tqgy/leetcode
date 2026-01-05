package com.careerup.tools;

import java.util.*;

/**
 * A simple Trie (Prefix Tree) implementation. 
 * Also support hash map implementation.
 * 
 * Supports: 
 *  - insert(word) 
 *  - search(word) 
 *  - startsWith(prefix)
 *  - autocomplete(prefix)
 */
class TrieTree {

    /** TrieNode represents each character node in the Trie */
    static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        // Map<Character, TrieNode> children = new HashMap<>();
        boolean isEndOfWord = false;
    }

    private final TrieNode root;

    public TrieTree() {
        root = new TrieNode();
    }

    /** Insert a word into the Trie */
    public void insert(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            int idx = c - 'a';
            if (node.children[idx] == null) {
                node.children[idx] = new TrieNode();
                // node.children.putIfAbsent(c, new TrieNode()); 
                // node = node.children.get(c);
            }
            node = node.children[idx];
        }
        node.isEndOfWord = true;
    }

    /** Search for an exact word */
    public boolean search(String word) {
        TrieNode node = findNode(word);
        return node != null && node.isEndOfWord;
    }

    /** Check if any word starts with the given prefix */
    public boolean startsWith(String prefix) {
        return findNode(prefix) != null;
    }

    /** Autocomplete: return all words starting with prefix */
    public List<String> autocomplete(String prefix) {
        List<String> results = new ArrayList<>();
        TrieNode node = findNode(prefix);

        if (node == null) return results; // no such prefix

        dfs(node, new StringBuilder(prefix), results);
        return results;
    }

    /** DFS to collect all words under a given TrieNode */
    private void dfs(TrieNode node, StringBuilder path, List<String> results) {
        if (node.isEndOfWord) {
            results.add(path.toString());
        }

        // for (var entry : node.children.entrySet()) { 
        //     path.append(entry.getKey()); 
        //     dfs(entry.getValue(), path, results); 
        //     path.deleteCharAt(path.length() - 1); // backtrack 
        // }

        for (int i = 0; i < 26; i++) {
            if (node.children[i] != null) {
                path.append((char) ('a' + i));
                dfs(node.children[i], path, results);
                path.deleteCharAt(path.length() - 1); // backtrack
            }
        }
    }

    /** Helper: find the node corresponding to the last char of a string */
    private TrieNode findNode(String s) {
        TrieNode node = root;

        for (char c : s.toCharArray()) {

            // if (!node.children.containsKey(c)) 
            //     return null; 
            // node = node.children.get(c); 
            
            int idx = c - 'a';
            if (node.children[idx] == null) return null;
            node = node.children[idx];
        }
        return node;
    }

    public static void main(String[] args) {
        TrieTree trie = new TrieTree();
        trie.insert("apple");
        trie.insert("app");
        trie.insert("bat");
        System.out.println(trie.search("apple")); // true
        System.out.println(trie.search("app")); // true
        System.out.println(trie.search("appl")); // false
        System.out.println(trie.search("bat")); // true
        System.out.println(trie.search("bath")); // false
        System.out.println(trie.startsWith("app")); // true
        System.out.println(trie.startsWith("ba")); // true
        System.out.println(trie.startsWith("cat")); // false
        System.out.println(trie.autocomplete("app")); // ["apple", "app"]
        System.out.println(trie.autocomplete("ba")); // ["bat"]
        System.out.println(trie.autocomplete("c")); // []
    }
}
