package com.careerup.pins;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Expression Add Operators (LeetCode 282)
 *
 * Given a string `num` containing only digits and an integer `target`, return
 * all possible ways to insert binary operators '+', '-', and/or '*' between the
 * digits such that the resulting expression evaluates to `target`.
 *
 * Notes:
 *  - Operands in the expressions must not contain leading zeros (e.g., "05").
 *  - Use DFS/backtracking to try all possible splits and operator placements.
 */
public class ExpressionAddOperators {

    /**
     * Entry point for generating all expressions that evaluate to `target`.
     *
     * @param num    digits-only string
     * @param target integer target value
     * @return list of valid expressions (order not guaranteed)
     */
    public List<String> addOperators(String num, int target) {
        List<String> res = new ArrayList<>();
        // Start DFS with empty expression. Parameters for dfs explained below.
        dfs("", 0L, 0L, 0, num, target, res);
        return res;
    }

    /**
     * DFS/backtracking helper.
     *
     * Parameters:
     *  - path : current expression string built so far
     *  - sum  : current evaluated total of the expression
     *  - pre  : the value of the last operand (including its sign when it
     *           has been applied to `sum`). This is required to correctly
     *           handle '*' (multiplication) because multiplication has higher
     *           precedence than previous '+'/'-'. For example, when adding
     *           "a*b" after expression "X + a", we must remove `a` (pre)
     *           then add `a*b`.
     *  - pos  : next index in `num` to consume
     */
    public void dfs(String path, long sum, long pre, int pos, String num, int target, List<String> res) {
        // If we've consumed all digits, check if expression evaluates to target
        if (pos == num.length()) {
            if (sum == target) {
                res.add(path);
            }
            return;
        }

        // Try all possible next operands by extending the substring [pos..i]
        for (int i = pos; i < num.length(); i++) {
            // Skip numbers with leading zeros (e.g. "05")
            if (i != pos && num.charAt(pos) == '0') {
                break;
            }

            long cur = Long.parseLong(num.substring(pos, i + 1));

            // If this is the very first operand, we don't add an operator before it
            if (pos == 0) {
                // path = "cur"; sum = cur; pre = cur
                dfs(path + cur, cur, cur, i + 1, num, target, res);
            } else {
                // Add '+' operator: sum increases by cur, pre = +cur
                dfs(path + "+" + cur, sum + cur, cur, i + 1, num, target, res);

                // Add '-' operator: sum decreases by cur, pre = -cur
                dfs(path + "-" + cur, sum - cur, -cur, i + 1, num, target, res);

                // Add '*' operator: need to remove previous `pre` contribution and
                // add `pre * cur` instead. pre becomes pre * cur for subsequent
                // multiplications.
                dfs(path + "*" + cur, sum - pre + pre * cur, pre * cur, i + 1, num, target, res);
            }
        }
    }

    // ------------------------ Simple in-file tests ---------------------------
    private static void runTest(String num, int target, Set<String> expected, String name) {
        ExpressionAddOperators solver = new ExpressionAddOperators();
        List<String> res = solver.addOperators(num, target);
        Set<String> resSet = new HashSet<>(res);
        if (resSet.equals(expected)) {
            System.out.println("PASS: " + name + " -> " + resSet);
        } else {
            System.err.println("FAIL: " + name + " -> expected " + expected + ", got " + resSet);
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        // Example cases from the problem
        runTest("123", 6, Set.of("1+2+3", "1*2*3"), "Example1");
        runTest("232", 8, Set.of("2*3+2", "2+3*2"), "Example2");
        runTest("105", 5, Set.of("1*0+5", "10-5"), "Example3");
        runTest("00", 0, Set.of("0+0", "0-0", "0*0"), "LeadingZeros");

        // No valid combinations
        runTest("3456237490", 9191, Set.of(), "NoSolution");

        System.out.println("All tests passed ✅");
    }
}
