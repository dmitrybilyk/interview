package com.conduct.interview._3_spring._5_transactions;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TransactionsDemo {

    public static void main(String[] args) {
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "com/conduct/interview/_3_spring/_5_transactions/transactions-context.xml")) {

            TransferService transferService = context.getBean(TransferService.class);

            printBalances(transferService, "-- starting balances --");

            transferService.transfer("alice", "bob", 100);
            printBalances(transferService, "-- after a normal transfer of 100 --");

            try {
                transferService.transfer("alice", "bob", 5000);
            } catch (IllegalArgumentException e) {
                System.out.println("caught: " + e.getMessage());
            }
            printBalances(transferService, "-- after the failed @Transactional transfer (should match above: rolled back) --");

            try {
                transferService.transferWithoutTransaction("alice", "bob", 5000);
            } catch (IllegalArgumentException e) {
                System.out.println("caught: " + e.getMessage());
            }
            printBalances(transferService, "-- after the failed NON-transactional transfer (alice is now short 5000 with no rollback) --");
        }
    }

    private static void printBalances(TransferService transferService, String label) {
        System.out.println(label);
        System.out.println("  alice: " + transferService.balanceOf("alice"));
        System.out.println("  bob:   " + transferService.balanceOf("bob"));
    }
}
