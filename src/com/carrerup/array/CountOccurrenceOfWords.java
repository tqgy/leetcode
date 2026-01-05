package com.carrerup.array;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Counts the occurrence of each word in a text file.
 * Uses a HashMap to store count frequencies and a TreeMap to sort the output alphabetically.
 */
public class CountOccurrenceOfWords {

    public static void main(String[] args) {
        String filename = "Test.txt";
        Map<String, Integer> wordCounts = new HashMap<>();

        // Use try-with-resources to ensure the file reader is closed automatically
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Split the line into words using non-word characters as delimiters
                String[] words = line.split("\\W+");
                
                for (String word : words) {
                    if (word.isEmpty()) continue;
                    
                    String key = word.toLowerCase(); // Case-insensitive counting
                    wordCounts.put(key, wordCounts.getOrDefault(key, 0) + 1);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return;
        }

        // Use a TreeMap to sort counts by word (key)
        Map<String, Integer> sortedCounts = new TreeMap<>(wordCounts);

        System.out.println("Word Counts (Alphabetical Order):");
        for (Map.Entry<String, Integer> entry : sortedCounts.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
