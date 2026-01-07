package com.careerup.carrot;

import java.util.*;

/**
 * Evaluates a set of variable assignments and computes the value of a target variable.
 * Supports:
 * - Literals: T1=1
 * - References: T2=T1
 * - Simple Arithmetic (+, -): T3=T1+T2
 * - Cycle Detection: Returns "IMPOSSIBLE" if a dependency cycle is detected.
 */
public class ExpressionEvaluator {

    private static final String IMPOSSIBLE = "IMPOSSIBLE";

    /**
     * Computes the value of the target variable given a list of expressions.
     *
     * @param target      The variable to resolve (e.g., "T1")
     * @param expressions List of assignment strings (e.g., "T1=1", "T2=T1+5")
     * @return The computed integer value as a String, or "IMPOSSIBLE" if a cycle/error occurs.
     */
    public static String compute(String target, List<String> expressions) {
        Map<String, String> formulaMap = parseExpressions(expressions);
        Map<String, Integer> memo = new HashMap<>();
        Set<String> visiting = new HashSet<>();

        try {
            int result = evaluate(target, formulaMap, memo, visiting);
            return String.valueOf(result);
        } catch (CycleException | IllegalArgumentException e) {
            return IMPOSSIBLE;
        }
    }

    private static Map<String, String> parseExpressions(List<String> expressions) {
        Map<String, String> map = new HashMap<>();
        for (String exp : expressions) {
            String[] parts = exp.split("=");
            if (parts.length == 2) {
                map.put(parts[0].trim(), parts[1].trim());
            }
        }
        return map;
    }

    private static int evaluate(String var, Map<String, String> formulaMap, Map<String, Integer> memo, Set<String> visiting) {
        // Return cached value if available
        if (memo.containsKey(var)) {
            return memo.get(var);
        }
        // If it's a raw number, return it immediately
        if (isNumeric(var)) {
            return Integer.parseInt(var);
        }

        // Cycle detection
        if (visiting.contains(var)) {
            throw new CycleException();
        }
        if (!formulaMap.containsKey(var)) {
            throw new IllegalArgumentException("Unknown variable: " + var);
        }

        visiting.add(var);
        
        String expression = formulaMap.get(var);
        int val = evaluateExpression(expression, formulaMap, memo, visiting);

        visiting.remove(var);
        memo.put(var, val);
        return val;
    }

    private static int evaluateExpression(String expr, Map<String, String> map, Map<String, Integer> memo, Set<String> visiting) {
        // Find operator (+ or -)
        int opIndex = findOperator(expr);

        if (opIndex == -1) {
            // Case 1: Simple reference or number (e.g., "5" or "T1")
            return evaluate(expr, map, memo, visiting);
        }

        // Case 2: Binary operation (e.g., "T1+5")
        String left = expr.substring(0, opIndex).trim();
        String right = expr.substring(opIndex + 1).trim();
        char op = expr.charAt(opIndex);

        int leftVal = evaluate(left, map, memo, visiting);
        int rightVal = evaluate(right, map, memo, visiting);

        return (op == '+') ? (leftVal + rightVal) : (leftVal - rightVal);
    }

    private static int findOperator(String expr) {
        // Skip index 0 to handle negative numbers
        for (int i = 1; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (c == '+' || c == '-') {
                return i;
            }
        }
        return -1;
    }

    private static boolean isNumeric(String str) {
        return str.matches("-?\\d+");
    }

    static class CycleException extends RuntimeException {}

    // ==========================================
    // Test Harness
    // ==========================================

    public static void main(String[] args) {
        System.out.println("=== ExpressionEvaluator Test Suite ===\n");
        boolean allPassed = true;

        allPassed &= runTest("Simple Value", "5", Arrays.asList("T1=5"), "T1");
        allPassed &= runTest("Simple Reference", "5", Arrays.asList("T1=5", "T2=T1"), "T2");
        allPassed &= runTest("Addition", "15", Arrays.asList("T1=5", "T2=10", "T3=T1+T2"), "T3");
        allPassed &= runTest("Subtraction", "5", Arrays.asList("T1=10", "T2=5", "T3=T1-T2"), "T3");
        allPassed &= runTest("Negative Result", "-5", Arrays.asList("T1=5", "T2=10", "T3=T1-T2"), "T3");
        allPassed &= runTest("Transitive", "8", Arrays.asList("A=1", "B=A", "C=B", "D=C+7"), "D");
        allPassed &= runTest("Cycle Direct", "IMPOSSIBLE", Arrays.asList("T1=T1"), "T1");
        allPassed &= runTest("Cycle Indirect", "IMPOSSIBLE", Arrays.asList("A=B", "B=C", "C=A"), "A");
        allPassed &= runTest("Cycle Complex", "IMPOSSIBLE", Arrays.asList("T1=1", "T2=2", "T3=T4+T5", "T4=T5", "T5=T4"), "T3");
        allPassed &= runTest("Unknown Var", "IMPOSSIBLE", Arrays.asList("T1=T2+1"), "T1");

        System.out.println("\nOverall Status: " + (allPassed ? "✅ ALL PASSED" : "❌ SOME FAILED"));
    }

    private static boolean runTest(String testName, String expected, List<String> expressions, String target) {
        String result = compute(target, expressions);
        boolean passed = expected.equals(result);
        System.out.printf("[%s] %-15s | Expected: %-10s | Got: %-10s\n", 
            passed ? "PASS" : "FAIL", testName, expected, result);
        return passed;
    }
}
