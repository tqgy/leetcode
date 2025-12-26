package com.careerup.carrot;

import java.util.*;

public class ExpressionEvaluator {

    public static Integer solve(String target, List<String> equations) {
        Map<String, Integer> values = new HashMap<>();
        Map<String, List<String>> map = new HashMap<>();
        Set<String> seen = new HashSet<>();

        // Parse equations
        for (String e : equations) {
            String[] parts = e.split("=");
            String left = parts[0].trim();
            String right = parts[1].trim();

            try {
                int num = Integer.parseInt(right);
                values.put(left, num);
            } catch (NumberFormatException ex) {
                // Split expression into tokens
                List<String> tokens = new ArrayList<>(Arrays.asList(right.split(" ")));
                map.put(left, tokens);
            }
        }

        // Recursive compute
        return compute(target, values, map, seen);
    }

    private static Integer compute(String variable, Map<String, Integer> values, Map<String, List<String>> map,
            Set<String> seen) {
        // If variable is a number
        try {
            return Integer.parseInt(variable);
        } catch (NumberFormatException ignored) {
        }

        if (values.containsKey(variable)) {
            return values.get(variable);
        }
        if (!map.containsKey(variable)) {
            return null;
        }
        if (seen.contains(variable)) {
            return null; // cycle detected
        }
        seen.add(variable);

        List<String> eq = map.get(variable);
        int val = 0;
        for (int i = 0; i < eq.size(); i += 2) {
            String signToken = (i - 1 >= 0) ? eq.get(i - 1) : "+";
            int sign = signToken.equals("+") ? 1 : -1;

            Integer subVal = compute(eq.get(i), values, map, seen);
            if (subVal == null)
                return null;
            val += sign * subVal;
        }

        values.put(variable, val);
        return val;
    }

    public static void main(String[] args) {
        List<List<String>> inputs = Arrays.asList(Arrays.asList("T1 = 1", "T2 = T3", "T3 = T1"),
                Arrays.asList("T1 = 1", "T2 = 2 + T4", "T3 = T1 - 4", "T4 = T1 + T3"),
                Arrays.asList("T1 = 1", "T2 = 2 + T4", "T3 = T1 - 4", "T4 = T1 + T3"),
                Arrays.asList("T1 = 1", "T2 = 2 + T4", "T3 = T1 - 4", "T4 = T1 + T3"),
                Arrays.asList("T1 = T2", "T2 = T1"));

        String[] targets = { "T2", "T2", "T3", "T4", "T2" };

        for (int i = 0; i < inputs.size(); i++) {
            Integer result = solve(targets[i], inputs.get(i));
            System.out.println(targets[i] + " = " + result);
        }
    }
}
