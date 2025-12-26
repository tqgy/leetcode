package com.careerup.pins;

import java.util.*;

public class OptimalAccountBalance {
    /**
     * Solutions for the "optimal account balance" problem.
     *
     * Given a list of transfers transactions where each transaction is
     * {@code [from, to, amount]}, compute ways to settle net debts between
     * people. This class provides:
     * - {@link #minTransfers(int[][])}: compute the minimum number of transactions
     *   required to settle all debts using a backtracking search.
     * - {@link #oneSettlement(int[][])}: produce one valid (greedy) sequence of
     *   transactions that settles all debts.
     *
     * Constraints (typical LeetCode limits): small number of transactions (<= 8)
     * so exponential backtracking is acceptable.
     */
    public static int minTransfers(int[][] transactions) {
        Map<Integer, Integer> balanceMap = getDebts(transactions);
        return settle(new ArrayList<>(balanceMap.values()), 0);
    }

    /**
     * Compute each person's net balance from the list of transactions.
     * Positive value means the person is owed money; negative means they owe.
     * Entries with zero balance are removed from the returned map.
     *
     * @param transactions array of {@code [from, to, amount]} transfers
     * @return map personId -> net balance (non-zero only)
     */
    public static Map<Integer, Integer> getDebts(int[][] transactions) {
        Map<Integer, Integer> balanceMap = new HashMap<>();
        for (int[] transaction : transactions) {
            int from = transaction[0];
            int to = transaction[1];
            int amount = transaction[2];
            // payer loses amount, receiver gains amount
            balanceMap.put(from, balanceMap.getOrDefault(from, 0) - amount);
            balanceMap.put(to, balanceMap.getOrDefault(to, 0) + amount);
        }
        // remove entries with zero net balance for cleaner downstream processing
        balanceMap.values().removeIf(balance -> balance == 0);
        return balanceMap;
    }

    /**
     * Backtracking routine that tries to settle all debts starting at index {@code start}.
     * The {@code debts} list contains net balances (positive = creditor, negative = debtor).
     * This method tries to pair {@code debts[start]} with later balances of opposite sign,
     * exploring all combinations to find the minimum number of transactions.
     *
     * Complexity: factorial in the number of non-zero balances in the worst case (small inputs).
     *
     * @param debts list of net balances (modified in-place during recursion)
     * @param start index of the current debt to settle
     * @return minimum number of transactions needed to settle all debts from {@code start}
     */
    public static int settle(List<Integer> debts, int start) {
        // all debts processed
        if (start == debts.size()) {
            return 0;
        }
        // if current balance already settled, move on
        if (debts.get(start) == 0) {
            return settle(debts, start + 1);
        }

        int minTransfers = Integer.MAX_VALUE;
        // try to settle debts[start] with any opposite-signed balance later in the list
        for (int i = start + 1; i < debts.size(); i++) {
            if (debts.get(start) * debts.get(i) < 0) {
                int originalStart = debts.get(start);
                int originalI = debts.get(i);
                int delta = Math.min(Math.abs(originalI), Math.abs(originalStart));

                // apply one transaction between start and i
                debts.set(start, originalStart + (originalStart > 0 ? -delta : delta));
                debts.set(i, originalI + (originalI > 0 ? -delta : delta));

                // recurse to settle the remainder
                int transfers = 1 + settle(debts, start + 1);
                minTransfers = Math.min(minTransfers, transfers);

                // undo (backtrack)
                debts.set(start, originalStart);
                debts.set(i, originalI);
            }
        }
        return minTransfers;
    }

    /**
     * Produce one valid sequence of transactions that settles all debts.
     * This is a greedy routine that pairs debtors with creditors until all
     * amounts are settled. It does not attempt to minimize the number of
     * transactions, but runs in linear time in the number of non-zero balances.
     *
     * @param transactions input transactions as {@code [from, to, amount]} entries
     * @return list of transactions {@code {fromId, toId, amount}} that settle all debts,
     *         or an empty list if a complete settlement is impossible (non-zero total)
     */
    public static List<int[]> oneSettlement(int[][] transactions) {
        Map<Integer, Integer> balanceMap = getDebts(transactions);
        // if the total is not zero, debts cannot be fully settled
        int total = balanceMap.values().stream().mapToInt(Integer::intValue).sum();
        if (total != 0) {
            return Collections.emptyList();
        }

        // queues of debtors (owes money) and creditors (is owed money)
        Deque<int[]> debtors = new ArrayDeque<>(); // each element: {id, remainingOwe}
        Deque<int[]> creditors = new ArrayDeque<>(); // each element: {id, remainingCredit}
        for (Map.Entry<Integer, Integer> e : balanceMap.entrySet()) {
            int id = e.getKey();
            int bal = e.getValue();
            if (bal < 0) debtors.addLast(new int[] { id, -bal });
            else if (bal > 0) creditors.addLast(new int[] { id, bal });
        }

        List<int[]> result = new ArrayList<>();
        while (!debtors.isEmpty() && !creditors.isEmpty()) {
            int[] d = debtors.peekFirst();
            int[] c = creditors.peekFirst();
            int give = Math.min(d[1], c[1]);
            // debtor pays creditor
            result.add(new int[] { d[0], c[0], give });
            d[1] -= give;
            c[1] -= give;
            if (d[1] == 0) debtors.removeFirst();
            if (c[1] == 0) creditors.removeFirst();
        }
        return result;
    }

    public static void main(String[] args) {
        int[][] transactions1 = {{0,1,10},{2,0,5}};
        System.out.println(minTransfers(transactions1)); // Expected output: 2
        System.out.println("One settlement: " + format(oneSettlement(transactions1)));

        int[][] transactions2 = {{0,1,10},{1,0,1},{1,2,5},{2,0,5}};
        System.out.println(minTransfers(transactions2)); // Expected output: 1
        System.out.println("One settlement: " + format(oneSettlement(transactions2)));
    }

    /**
     * Simple formatter for a list of int[] transactions used in examples.
     *
     * @param txs list of transactions where each transaction is {from, to, amount}
     * @return human readable representation like "(0->1:5), (2->1:5)"
     */
    private static String format(List<int[]> txs) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < txs.size(); i++) {
            int[] t = txs.get(i);
            sb.append(String.format("(%d->%d:%d)", t[0], t[1], t[2]));
            if (i < txs.size() - 1) sb.append(", ");
        }
        return sb.toString();
    }
}
