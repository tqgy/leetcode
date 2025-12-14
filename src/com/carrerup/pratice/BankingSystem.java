package com.carrerup.pratice;

import java.util.*;
import java.util.*;

class BankSystem {
    private Map<Integer, Account> accounts;
    private Map<Integer, Payment> paymentHistory;

    public BankSystem() {
        accounts = new HashMap<>();
        paymentHistory = new HashMap<>();
    }

    public void createAccount(int accountId) {
        if (!accounts.containsKey(accountId)) {
            accounts.put(accountId, new Account(accountId));
        }
    }

    public void deposit(int accountId, int amount) {
        if (accounts.containsKey(accountId)) {
            Account acc = accounts.get(accountId);
            acc.setBalance(acc.getBalance() + amount);
            acc.addTransaction(new Transaction("deposit", amount));
        }
    }

    public void transfer(int fromAccountId, int toAccountId, int amount) {
        if (accounts.containsKey(fromAccountId) &&
            accounts.containsKey(toAccountId) &&
            accounts.get(fromAccountId).getBalance() >= amount) {

            Account from = accounts.get(fromAccountId);
            Account to = accounts.get(toAccountId);

            from.setBalance(from.getBalance() - amount);
            to.setBalance(to.getBalance() + amount);

            from.addTransaction(new Transaction("transfer_out", amount));
            to.addTransaction(new Transaction("transfer_in", amount));
        }
    }

    public List<Integer> topSpenders(int k) {
        List<Spender> spenders = new ArrayList<>();

        for (Account acc : accounts.values()) {
            int totalSpent = acc.getTransactions().stream()
                    .filter(t -> !t.getType().equals("deposit"))
                    .mapToInt(Transaction::getAmount)
                    .sum();
            spenders.add(new Spender(totalSpent, acc.getId()));
        }

        spenders.sort((a, b) -> {
            if (b.totalSpent != a.totalSpent) {
                return Integer.compare(b.totalSpent, a.totalSpent);
            }
            return Integer.compare(a.accountId, b.accountId);
        });

        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < Math.min(k, spenders.size()); i++) {
            result.add(spenders.get(i).accountId);
        }
        return result;
    }

    public int pay(int accountId, int amount) {
        if (accounts.containsKey(accountId) &&
            accounts.get(accountId).getBalance() >= amount) {

            Account acc = accounts.get(accountId);
            acc.setBalance(acc.getBalance() - amount);

            int paymentId = paymentHistory.size() + 1;
            Payment payment = new Payment(accountId, "pending", amount);
            paymentHistory.put(paymentId, payment);

            // Simulate cashback immediately for demo
            processCashback(paymentId);

            return paymentId;
        }
        return -1;
    }

    private void processCashback(int paymentId) {
        Payment payment = paymentHistory.get(paymentId);
        if (payment != null && payment.getStatus().equals("pending")) {
            int cashback = (int) (payment.getAmount() * 0.1); // 10% cashback
            Account acc = accounts.get(payment.getAccountId());
            acc.setBalance(acc.getBalance() + cashback);
            acc.addTransaction(new Transaction("cashback", cashback));
            payment.setStatus("completed");
        }
    }

    public String getPaymentStatus(int paymentId) {
        Payment payment = paymentHistory.get(paymentId);
        return payment != null ? payment.getStatus() : "unknown";
    }

    // Inner helper classes
    private static class Account {
        private int id;
        private int balance;
        private List<Transaction> transactions;

        public Account(int id) {
            this.id = id;
            this.balance = 0;
            this.transactions = new ArrayList<>();
        }

        public int getId() { return id; }
        public int getBalance() { return balance; }
        public void setBalance(int balance) { this.balance = balance; }
        public List<Transaction> getTransactions() { return transactions; }
        public void addTransaction(Transaction t) { transactions.add(t); }

        @Override
        public String toString() {
            return "Account{id=" + id + ", balance=" + balance + ", transactions=" + transactions + "}";
        }
    }

    private static class Transaction {
        private String type;
        private int amount;

        public Transaction(String type, int amount) {
            this.type = type;
            this.amount = amount;
        }

        public String getType() { return type; }
        public int getAmount() { return amount; }

        @Override
        public String toString() {
            return "(" + type + ", " + amount + ")";
        }
    }

    private static class Payment {
        private int accountId;
        private String status;
        private int amount;

        public Payment(int accountId, String status, int amount) {
            this.accountId = accountId;
            this.status = status;
            this.amount = amount;
        }

        public int getAccountId() { return accountId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public int getAmount() { return amount; }
    }

    private static class Spender {
        private int totalSpent;
        private int accountId;

        public Spender(int totalSpent, int accountId) {
            this.totalSpent = totalSpent;
            this.accountId = accountId;
        }
    }

    // Demo main method
    public static void main(String[] args) {
        BankSystem bank = new BankSystem();
        bank.createAccount(1);
        bank.deposit(1, 1000);
        bank.createAccount(2);
        bank.deposit(2, 500);
        bank.transfer(1, 2, 200);

        System.out.println(bank.accounts); // Verify balances
        System.out.println(bank.topSpenders(1)); // Top spender
        int paymentId = bank.pay(1, 300);
        System.out.println("Payment status: " + bank.getPaymentStatus(paymentId));
        System.out.println(bank.accounts); // Check cashback applied
    }
}


class BankingSystem {
    private Map<String, Integer> accounts;
    private List<ScheduledPayment> scheduledPayments;

    public BankingSystem() {
        accounts = new HashMap<>();
        scheduledPayments = new ArrayList<>();
    }

    public void createAccount(String accountId, int initialBalance) {
        accounts.put(accountId, initialBalance);
    }

    public String transfer(String fromAccount, String toAccount, int amount) {
        if (!accounts.containsKey(fromAccount) || !accounts.containsKey(toAccount)) {
            return "Account not found";
        }
        if (accounts.get(fromAccount) < amount) {
            return "Insufficient funds";
        }
        accounts.put(fromAccount, accounts.get(fromAccount) - amount);
        accounts.put(toAccount, accounts.get(toAccount) + amount);
        return "Transfer successful";
    }

    public String schedulePayment(String fromAccount, String toAccount, int amount, String date) {
        scheduledPayments.add(new ScheduledPayment(fromAccount, toAccount, amount, date));
        return "Scheduled payment placed";
    }

    public String cancelPayment(String fromAccount, String date) {
        scheduledPayments.removeIf(p -> p.getFromAccount().equals(fromAccount) && p.getDate().equals(date));
        return "Scheduled payment canceled";
    }

    public String checkBalance(String accountId) {
        return accounts.containsKey(accountId) ? String.valueOf(accounts.get(accountId)) : "Account not found";
    }

    // Inner class for scheduled payments
    private static class ScheduledPayment {
        private String fromAccount;
        private String toAccount;
        private int amount;
        private String date;

        public ScheduledPayment(String fromAccount, String toAccount, int amount, String date) {
            this.fromAccount = fromAccount;
            this.toAccount = toAccount;
            this.amount = amount;
            this.date = date;
        }

        public String getFromAccount() { return fromAccount; }
        public String getToAccount() { return toAccount; }
        public int getAmount() { return amount; }
        public String getDate() { return date; }
    }

    // Main method for testing
    public static void main(String[] args) {
        BankingSystem bs = new BankingSystem();
        bs.createAccount("A", 1000);
        bs.createAccount("B", 500);

        System.out.println(bs.checkBalance("A"));
        System.out.println(bs.checkBalance("B"));
        System.out.println(bs.transfer("A", "B", 200));
        System.out.println(bs.schedulePayment("A", "B", 300, "2023-10-25"));
        System.out.println(bs.cancelPayment("A", "2023-10-25"));
        System.out.println(bs.checkBalance("A"));
        System.out.println(bs.checkBalance("B"));
    }
}

