package com.careerup.strip;

/**
 * -----------------------------------------------------------------------------
 * Accept-Language Parser (Parts 1–4)
 * -----------------------------------------------------------------------------
 *
 * The Accept-Language HTTP header lists languages the client prefers, in order.
 * Example:
 *     "en-US, fr-CA, fr-FR"
 *
 * The server supports a set of languages, e.g.:
 *     ["fr-FR", "en-US"]
 *
 * Our job:
 *   Given the header + supported languages, return the list of languages
 *   that the server can respond with, in correct preference order.
 *
 * PART 1:
 *   - Only exact matches (e.g., "en-US" matches "en-US")
 *
 * PART 2:
 *   - Support language-only tags (e.g., "fr" matches "fr-FR", "fr-CA")
 *
 * PART 3:
 *   - Support wildcard "*" meaning "all other languages"
 *
 * PART 4:
 *   - Support q-factors: "fr-FR;q=1", "fr;q=0.5", "fr-CA;q=0"
 *   - q=0 means "undesired" (lowest priority)
 *   - Higher q means higher preference
 *
 * Final ordering rules:
 *   1. Sort by q-factor (descending)
 *   2. For equal q, preserve the order in which the client listed them
 *   3. For language-only tags (e.g., "fr"), all matching variants appear
 *      before any more specific tag that appears later in the header.
 *
 * -----------------------------------------------------------------------------
 */

import java.util.*;

public class HTTPAcceptLanguage {

    /** Represents one parsed header entry */
    static class Entry {
        String tag;     // e.g. "fr-FR", "fr", "*"
        double q;       // q-factor
        int index;      // order in header

        Entry(String tag, double q, int index) {
            this.tag = tag;
            this.q = q;
            this.index = index;
        }
    }

    /**
     * Main function: parse Accept-Language header and return supported languages
     * in correct preference order.
     */
    public static List<String> parseAcceptLanguage(String header, List<String> supported) {
        List<Entry> entries = parseHeader(header);

        // Map each supported language to its best q-factor (default = -1 = not selected)
        Map<String, Double> bestQ = new HashMap<>();
        for (String s : supported) bestQ.put(s, -1.0);

        // Process entries in order
        for (Entry e : entries) {
            if (e.tag.equals("*")) {
                // wildcard: match all languages not yet assigned
                for (String lang : supported) {
                    if (bestQ.get(lang) < 0) {
                        bestQ.put(lang, e.q);
                    }
                }
            } else if (e.tag.contains("-")) {
                // exact match
                if (bestQ.containsKey(e.tag)) {
                    bestQ.put(e.tag, e.q);
                }
            } else {
                // language-only tag: match all variants
                String prefix = e.tag + "-";
                for (String lang : supported) {
                    if (lang.startsWith(prefix)) {
                        bestQ.put(lang, e.q);
                    }
                }
            }
        }

        // Filter out languages that never matched (q < 0)
        List<String> result = new ArrayList<>();
        for (String lang : supported) {
            if (bestQ.get(lang) >= 0) result.add(lang);
        }

        // Sort by q-factor descending, then by header index
        result.sort((a, b) -> {
            double qa = bestQ.get(a);
            double qb = bestQ.get(b);
            if (qa != qb) return Double.compare(qb, qa); // higher q first

            // tie-breaker: earlier header entry wins
            int ia = findEntryIndex(entries, a);
            int ib = findEntryIndex(entries, b);
            return Integer.compare(ia, ib);
        });

        return result;
    }

    /** Parse header into Entry objects */
    private static List<Entry> parseHeader(String header) {
        List<Entry> list = new ArrayList<>();
        String[] parts = header.split(",");

        for (int i = 0; i < parts.length; i++) {
            String p = parts[i].trim();
            String[] seg = p.split(";");

            String tag = seg[0].trim();
            double q = 1.0; // default q-factor

            if (seg.length > 1 && seg[1].startsWith("q=")) {
                q = Double.parseDouble(seg[1].substring(2));
            }

            list.add(new Entry(tag, q, i));
        }
        return list;
    }

    /** Find the index of the header entry that matched this supported language */
    private static int findEntryIndex(List<Entry> entries, String lang) {
        for (Entry e : entries) {
            if (e.tag.equals("*")) return e.index;
            if (e.tag.equals(lang)) return e.index;
            if (!e.tag.contains("-") && lang.startsWith(e.tag + "-")) return e.index;
        }
        return Integer.MAX_VALUE; // should not happen
    }

    // -------------------------------------------------------------------------
    // Test Cases
    // -------------------------------------------------------------------------
    public static void main(String[] args) {

        System.out.println("=== PART 1 ===");
        System.out.println(parseAcceptLanguage("en-US, fr-CA, fr-FR",
                List.of("fr-FR", "en-US"))); // [en-US, fr-FR]

        System.out.println(parseAcceptLanguage("fr-CA, fr-FR",
                List.of("en-US", "fr-FR"))); // [fr-FR]

        System.out.println(parseAcceptLanguage("en-US",
                List.of("en-US", "fr-CA"))); // [en-US]


        System.out.println("\n=== PART 2 ===");
        System.out.println(parseAcceptLanguage("en",
                List.of("en-US", "fr-CA", "fr-FR"))); // [en-US]

        System.out.println(parseAcceptLanguage("fr",
                List.of("en-US", "fr-CA", "fr-FR"))); // [fr-CA, fr-FR]

        System.out.println(parseAcceptLanguage("fr-FR, fr",
                List.of("en-US", "fr-CA", "fr-FR"))); // [fr-FR, fr-CA]


        System.out.println("\n=== PART 3 ===");
        System.out.println(parseAcceptLanguage("en-US, *",
                List.of("en-US", "fr-CA", "fr-FR"))); // [en-US, fr-CA, fr-FR]

        System.out.println(parseAcceptLanguage("fr-FR, fr, *",
                List.of("en-US", "fr-CA", "fr-FR"))); // [fr-FR, fr-CA, en-US]


        System.out.println("\n=== PART 4 ===");
        System.out.println(parseAcceptLanguage("fr-FR;q=1, fr-CA;q=0, fr;q=0.5",
                List.of("fr-FR", "fr-CA", "fr-BG"))); // [fr-FR, fr-BG, fr-CA]

        System.out.println(parseAcceptLanguage("fr-FR;q=1, fr-CA;q=0, *;q=0.5",
                List.of("fr-FR", "fr-CA", "fr-BG", "en-US"))); // [fr-FR, fr-BG, en-US, fr-CA]

        System.out.println(parseAcceptLanguage("fr-FR;q=1, fr-CA;q=0.8, *;q=0.5",
                List.of("fr-FR", "fr-CA", "fr-BG", "en-US")));
        // expected: [fr-FR, fr-CA, fr-BG, en-US]
    }
}

