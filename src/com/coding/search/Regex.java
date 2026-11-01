package com.careerup.tools;

import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

/**
 * Refined Regex class with constants and test cases based on provided patterns.
 * Common Regex Tokens:
 *   - .       Any character (typically except newline)
 *   - \d      Any digit [0-9]
 *   - \D      Any non-digit [^0-9]
 *   - \s      Any whitespace character [ \t\n\x0B\f\r]
 *   - \S      Any non-whitespace character
 *   - \w      Any word character [a-zA-Z_0-9]
 *   - \W      Any non-word character
 *   - \b      Word boundary
 *   - ^       Start of line/string
 *   - $       End of line/string
 *   - ?       Zero or one (optional)
 *   - *       Zero or more
 *   - +       One or more
 *   - {n}     Exactly n times
 *   - {n,}    At least n times
 *   - {n,m}   At least n but not more than m times
 *   - (...)   Capturing group
 *   - [...]   Available characters
 *   - |       Logical OR
 */
public class Regex {

    // Numbers
    public static final String INTEGER = "-?\\d+";
    public static final String DECIMAL = "-?\\d+(\\.\\d+)?";
    public static final String HEX = "0[xX][0-9A-Fa-f]+";
    public static final String BINARY = "0[bB][01]+";

    // Text / Characters
    public static final String LETTERS_ONLY = "[A-Za-z]+";
    public static final String ALPHANUMERIC_ONLY = "[A-Za-z0-9]+";
    public static final String WHITESPACE = "\\s+";

    // Emails / URLs
    public static final String EMAIL = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    public static final String URL = "https?://[A-Za-z0-9./_-]+";
    public static final String DOMAIN = "^[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";

    // Dates / Times
    public static final String DATE_YYYY_MM_DD = "\\d{4}-\\d{2}-\\d{2}";
    public static final String TIME_HH_MM = "([01]\\d|2[0-3]):[0-5]\\d";

    // Phone Numbers
    public static final String US_PHONE = "\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}";

    // Passwords
    public static final String PASSWORD_COMPLEX = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";

    // Programming / Logs
    public static final String JAVA_IDENTIFIER = "[A-Za-z_$][A-Za-z0-9_$]*";
    public static final String IPV4 = "((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)";
    public static final String MAC_ADDRESS = "([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}";

    // HTML / XML
    public static final String HTML_TAG = "<([A-Za-z][A-Za-z0-9]*)\\b[^>]*>(.*?)</\\1>";
    public static final String STRIP_HTML = "<[^>]+>";

    // Words / Boundaries
    public static final String WHOLE_WORD_APPLE = "\\bapple\\b";
    public static final String WORDS_ONLY = "\\b[A-Za-z]+\\b";

    // Lookaheads / Lookbehinds
    public static final String NUMBER_FOLLOWED_BY_KG = "\\d+(?=kg)";
    public static final String NUMBER_NOT_FOLLOWED_BY_KG = "\\d+(?!kg)";
    public static final String WORD_PRECEDED_BY_AT = "(?<=@)\\w+";

    // Misc
    public static final String UUID = "[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}";
    public static final String CREDIT_CARD_SIMPLE = "\\d{4}[- ]?\\d{4}[- ]?\\d{4}[- ]?\\d{4}";

    public static void test(String regex, String input) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        System.out.println("--------------------------------------------------");
        System.out.println("Regex: " + regex);
        System.out.println("Input: " + input);

        int count = 0;
        while (matcher.find()) {
            System.out.println("Match " + (++count) + ": \"" + matcher.group() + "\"");
            for (int i = 1; i <= matcher.groupCount(); i++) {
                System.out.println("  Group " + i + ": \"" + matcher.group(i) + "\"");
            }
        }

        if (count == 0) {
            System.out.println("No matches found.");
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Regex Test Suite ===\n");

        test(INTEGER, "123 -456 abc");
        test(DECIMAL, "123.45 -0.678 99");
        test(HEX, "0x1A 0Xff 123");
        test(BINARY, "0b1011 0B1100 0b12");

        test(LETTERS_ONLY, "abcDEF 123");
        test(ALPHANUMERIC_ONLY, "abc123DEF");
        test(WHITESPACE, "a   b\tc");

        test(EMAIL, "user@example.com");
        test(EMAIL, "invalid-email");
        test(URL, "https://www.google.com/search");
        test(DOMAIN, "google.com");

        test(DATE_YYYY_MM_DD, "2023-10-27");
        test(TIME_HH_MM, "23:59 12:00 25:00");

        test(US_PHONE, "(123) 456-7890");
        test(US_PHONE, "123-456-7890");

        test(PASSWORD_COMPLEX, "Pass1234");
        test(PASSWORD_COMPLEX, "weak");

        test(JAVA_IDENTIFIER, "validVar _private $money 1invalid");

        test(IPV4, "192.168.0.1 256.0.0.1");
        test(MAC_ADDRESS, "00:1A:2B:3C:4D:5E");

        test(HTML_TAG, "<div>Content</div>");
        test(HTML_TAG, "<p>Text</p>");
        test(STRIP_HTML, "<b>Bold</b>");

        test(WHOLE_WORD_APPLE, "apple apples pineapple");
        test(WORDS_ONLY, "hello world 123");

        test(NUMBER_FOLLOWED_BY_KG, "10kg 5lb");
        test(NUMBER_NOT_FOLLOWED_BY_KG, "10kg 5lb");

        test(WORD_PRECEDED_BY_AT, "user@domain @handle");

        test(UUID, "123e4567-e89b-12d3-a456-426614174000");
        test(CREDIT_CARD_SIMPLE, "1234-5678-9012-3456");
        String s = "[a,b,n,n,m]";
        s = s.replaceAll("[\\[\\]]", "");
        System.out.println(s);
        s = "[a,b+n-n*m]";
        String[] ss = s.split("[\\[\\]\\+\\-\\*\\,]");
        Arrays.stream(ss).forEach(System.out::println);

        Set<String> set1 = new HashSet<>();
        set1.add("a");
        set1.add("b");
        set1.add("n");
        set1.add("m");
        System.out.println(set1);

        Set<String> set2 = new HashSet<>(Arrays.asList("a", "b", "c", "d"));
        System.out.println("Set 2: " + set2);

        // 1. In-place intersection (modifies set1)
        Set<String> set1Copy = new HashSet<>(set1);
        set1Copy.retainAll(set2);
        System.out.println("Intersection (In-place): " + set1Copy);

        // 2. Stream-based intersection (returns a new set)
        Set<String> intersection = set1.stream()
                .filter(set2::contains)
                .collect(Collectors.toSet());
        System.out.println("Intersection (Stream): " + intersection);
    }
}
