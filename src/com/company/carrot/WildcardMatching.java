package com.careerup.carrot;

/**
 * Given an input string (s) and a pattern (p), implement wildcard pattern
 * matching with support for '?' and '*' where:
 * 
 * '?' Matches any single character. '*' Matches any sequence of characters
 * (including the empty sequence). The matching should cover the entire input
 * string (not partial).
 */
public class WildcardMatching {
    public static boolean isMatch(String s, String p) {
        if (s == null || p == null)
            return false;
        int slen = s.length(), plen = p.length();
        // dp[i][j] means 1-ith in s match 1-jth in p
        boolean[][] dp = new boolean[slen + 1][plen + 1];
        dp[0][0] = true;
        // init: take care of "" for s and "*" for p
        for (int i = 1; i <= plen; i++) {
            if (p.charAt(i - 1) == '*')
                dp[0][i] = true;
            else
                break;
        }

        for (int i = 1; i <= slen; i++) {
            for (int j = 1; j <= plen; j++) {
                char sc = s.charAt(i - 1), pc = p.charAt(j - 1);
                if (sc == pc || pc == '?')
                    dp[i][j] = dp[i - 1][j - 1];
                else if (pc == '*') {
                    // dp[i][j-1] means p's * match 0 char in s
                    // dp[i-1][j] means p's * match multiple char in s
                    dp[i][j] = dp[i][j - 1] || dp[i - 1][j];
                } else
                    dp[i][j] = false;
            }
        }
        return dp[slen][plen];
    }

    public static void main(String[] args) {
        String s = "aa", p = "a";
        System.out.println(isMatch(s, p));
        s = "aa";
        p = "*";
        System.out.println(isMatch(s, p));
        s = "cb";
        p = "?a";
        System.out.println(isMatch(s, p));
        s = "adceb";
        p = "*a*b";
        System.out.println(isMatch(s, p));
        s = "acdcb";
        p = "a*c?b";
        System.out.println(isMatch(s, p));
    }
}
