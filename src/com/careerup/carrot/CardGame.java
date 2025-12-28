package com.careerup.carrot;

import java.util.*;

/**
 * Given an input of cards that have suits { +, -, = }, values { A, B, C }, and
 * different counts of values [1 - 3]. Find a valid hand. A valid hand consists
 * of 3 cards. Where all the suits are different or the same, all the values are
 * different or the same, and all counts are different or the same.
 */
public class CardGame {

    private static final char[] idxToSuite = new char[] { '+', '-', '=' };
    private static final String[] idxToValue = new String[] { "A", "B", "C" };
    private static final int[] idxToCount = new int[] { 1, 2, 3 };

    private static String idxToCard(int s, int v, int c) {
        return String.valueOf(idxToSuite[s]) + idxToValue[v].repeat(idxToCount[c]);
    }

    private static int[] cardToIdx(char[] card) {
        int s = card[0] == '+' ? 0 : (card[0] == '-' ? 1 : 2);
        int v = card[1] == 'A' ? 0 : (card[1] == 'B' ? 1 : 2);
        int c = card.length - 2;
        return new int[] { s, v, c, s * 9 + v * 3 + c };
    }

    private static int calculateIdx(int i, int j) {
        return i == j ? i : 3 - i - j;
    }

    public static List<List<String>> getCards(String[] cards) {
        if (cards.length < 3) {
            return Collections.emptyList();
        }
        List<List<String>> res = new ArrayList<>();
        int[] cnts = new int[27];
        for (String card : cards) {
            ++cnts[cardToIdx(card.toCharArray())[3]];
        }
        for (int t1 = 0; t1 < 27; ++t1) {
            int s1 = t1 / 9;
            int v1 = (t1 / 3) % 3;
            int c1 = t1 % 3;
            if (cnts[t1] <= 0) {
                continue;
            }
            for (int t2 = t1; t2 < 27; ++t2) {
                int s2 = t2 / 9;
                int v2 = (t2 / 3) % 3;
                int c2 = t2 % 3;
                if (t1 == t2 && cnts[t2] < 2 || t1 != t2 && cnts[t2] <= 0) {
                    continue;
                }
                int s3 = calculateIdx(s1, s2);
                int v3 = calculateIdx(v1, v2);
                int c3 = calculateIdx(c1, c2);
                int t3 = s3 * 9 + v3 * 3 + c3;
                if (t3 >= t2 && (t1 == t2 && cnts[t1] >= 3 || t1 != t2 && cnts[t3] > 0)) {
                    res.add(Arrays.asList(idxToCard(s1, v1, c1), idxToCard(s2, v2, c2), idxToCard(s3, v3, c3)));
                }
            }
        }
        return res;
    }

    public static void main(String[] args) {
        String[] cards = new String[] { "+AA", "-AA", "+AA", "-C", "-B", "+AA", "-AAA", "-A", "=AA" };
        System.out.println(getCards(cards));
    }
}
