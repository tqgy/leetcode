package com.careerup.carrot;

import java.util.ArrayList;
import java.util.List;

/**
 * Given an m x n board of characters and a list of strings words, return all
 * words on the board.
 * 
 * Each word must be constructed from letters of sequentially adjacent cells,
 * where adjacent cells are horizontally or vertically neighboring. The same
 * letter cell may not be used more than once in a word.
 */
public class WordSearchII {
    int[][] dir = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };

    // Use trie to find word
    // N = num of rows
    // M = num of columns
    // X = number of words in dictionary
    // Y = length of longest word in dictionary
    // Use Trie Time : O(4^(min(Y, NM))*NM)
    // Use Set Time : O(4^(NM)*NM)
    public List<String> findWords(char[][] board, String[] words) {
        List<String> res = new ArrayList<>();
        if (board == null || words == null)
            return res;
        TrieNode node = buildTrie(words);
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board[0].length; j++) {
                find(board, i, j, res, node);
            }
        return res;
    }

    private void find(char[][] board, int row, int col, List<String> res, TrieNode node) {
        // error
        if (row < 0 || col < 0 || row == board.length || col == board[0].length)
            return;
        char c = board[row][col];
        // already visited or don't have char in Trie
        if (c == '#' || node.next[c - 'a'] == null)
            return;
        // Move to the char in Trie
        node = node.next[c - 'a'];
        // find a result
        if (node.word != null) {
            res.add(node.word);
            // deduplicate
            node.word = null;
            // Important! Don't return! May miss some result!
            // return;
        }
        // visited
        board[row][col] = '#';
        // recursion
        for (int[] d : dir) {
            find(board, row + d[0], col + d[1], res, node);
        }
        // restore the original char
        board[row][col] = c;
    }

    private TrieNode buildTrie(String[] words) {
        TrieNode root = new TrieNode();
        for (String s : words) {
            // Important! Need set a temp root for each word, start from root
            TrieNode n = root;
            char[] word = s.toCharArray();
            for (int i = 0; i < word.length; i++) {
                char c = word[i];
                int index = c - 'a';
                if (n.next[index] == null) {
                    n.next[index] = new TrieNode();
                }
                n = n.next[index];
            }
            n.word = s;
        }
        return root;
    }

    class TrieNode {
        TrieNode[] next = new TrieNode[26];
        // use word instead of a isWord flag to make it easy to get the word
        String word;
    }

    public static void main(String[] args) {
        WordSearchII ws = new WordSearchII();
        char[][] board = { { 'o', 'a', 'a', 'n' }, { 'e', 't', 'a', 'e' }, { 'i', 'h', 'k', 'r' },
                { 'i', 'f', 'l', 'v' } };
        String[] words = { "oath", "pea", "eat", "rain" };
        System.out.println(ws.findWords(board, words));
    }
}
