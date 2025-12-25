package com.careerup.instacart;

import java.util.*;

public class CalculateEquationExpression {

    private Map<String, String> expr = new HashMap<>();
    private Map<String, Integer> memo = new HashMap<>();
    private Set<String> visiting = new HashSet<>(); // for cycle detection

    public int calculate(String[] arr) {
        String target = arr[0];

        for (int i = 1; i < arr.length; i++) {
            String[] parts = arr[i].split("=", 2);
            expr.put(parts[0].trim(), parts[1].trim());
        }

        return evalVar(target);
    }

    private int evalVar(String var) {
        if (memo.containsKey(var)) return memo.get(var);

        if (visiting.contains(var)) {
            throw new RuntimeException("Circular dependency detected at: " + var);
        }

        visiting.add(var);
        int val = evalExpr(expr.get(var));
        visiting.remove(var);

        memo.put(var, val);
        return val;
    }

    private int evalExpr(String s) {
        List<String> tokens = tokenize(s);
        List<String> replaced = new ArrayList<>();

        for (String t : tokens) {
            if (Character.isLetter(t.charAt(0))) {
                replaced.add(String.valueOf(evalVar(t)));
            } else {
                replaced.add(t);
            }
        }

        return evalTokens(replaced);
    }

    private List<String> tokenize(String s) {
        List<String> out = new ArrayList<>();
        int i = 0;

        while (i < s.length()) {
            char c = s.charAt(i);

            if (Character.isWhitespace(c)) {
                i++;
            } else if ("+-*/()".indexOf(c) >= 0) {
                out.add(String.valueOf(c));
                i++;
            } else if (Character.isDigit(c)) {
                int j = i;
                while (j < s.length() && Character.isDigit(s.charAt(j))) j++;
                out.add(s.substring(i, j));
                i = j;
            } else { // variable
                int j = i;
                while (j < s.length() && Character.isLetterOrDigit(s.charAt(j))) j++;
                out.add(s.substring(i, j));
                i = j;
            }
        }

        return out;
    }

    private int evalTokens(List<String> t) {
        Stack<Integer> nums = new Stack<>();
        Stack<Character> ops = new Stack<>();

        for (String s : t) {
            if (s.equals("(")) {
                ops.push('(');
            } else if (s.equals(")")) {
                while (ops.peek() != '(') apply(nums, ops.pop());
                ops.pop();
            } else if (s.length() == 1 && "+-*/".indexOf(s.charAt(0)) >= 0) {
                char op = s.charAt(0);
                while (!ops.isEmpty() && prec(ops.peek()) >= prec(op)) {
                    apply(nums, ops.pop());
                }
                ops.push(op);
            } else {
                nums.push(Integer.parseInt(s));
            }
        }

        while (!ops.isEmpty()) apply(nums, ops.pop());
        return nums.pop();
    }

    private int prec(char c) {
        return (c == '+' || c == '-') ? 1 : (c == '*' || c == '/') ? 2 : 0;
    }

    private void apply(Stack<Integer> nums, char op) {
        int b = nums.pop();
        int a = nums.pop();
        switch (op) {
            case '+': nums.push(a + b); break;
            case '-': nums.push(a - b); break;
            case '*': nums.push(a * b); break;
            case '/': nums.push(a / b); break;
        }
    }

    // Test cases
    public static void main(String[] args) {
        CalculateEquationExpression calc = new CalculateEquationExpression();

        String[] arr1 = {
            "total",
            "a = 5",
            "b = a * 2",
            "c = b + 3",
            "total = c - 4"
        };
        System.out.println(calc.calculate(arr1)); // 9

        String[] arr2 = {
            "result",
            "x = 10",
            "y = x / 2",
            "z = (y + 3) * 4",
            "result = z - x"
        };
        System.out.println(calc.calculate(arr2)); // 18

        // Circular dependency test
        String[] arr3 = {
            "final",
            "a = b + 1",
            "b = a + 1",
            "final = a"
        };
        try {
            System.out.println(calc.calculate(arr3));
        } catch (Exception e) {
            System.out.println(e.getMessage()); // Circular dependency detected
        }
    }
}
