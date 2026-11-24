package com.example.sqe;

import java.util.ArrayList;
import java.util.List;

public class CasinoDiceGame {

    private static final int SIDES = 6;
    private int playerBalance;

    public CasinoDiceGame(int initialBalance) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Balance cannot be negative.");
        }
        this.playerBalance = initialBalance;
    }

    public int rollDie() {
        // TO FIX Sonar S2245 (Insecure Randomness):
        // Paste this instead of Math.random():
        return new java.security.SecureRandom().nextInt(SIDES) + 1;
       // return (int) (Math.random() * SIDES) + 1;
    }

    public List<Integer> rollMultipleDice(int count) {
        List<Integer> rolls = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            rolls.add(rollDie());
        }
        return rolls;
    }

    public boolean playHighLowBet(int betAmount, boolean betHigh) {
        if (betAmount <= 0 || betAmount > playerBalance) {
            throw new IllegalArgumentException("Invalid bet amount.");
        }

        int total = rollDie() + rollDie();
        boolean isHigh = total >= 8;

        if (isHigh == betHigh) {
            playerBalance += betAmount;
            return true;
        } else {
            playerBalance -= betAmount;
            return false;
        }
    }

    public int getPlayerBalance() {
        return playerBalance;
    }

    // TO FIX Sonar S3776 (Cognitive Complexity) & all issues below:
    // Replace the entire processPayment method with this clean implementation:
    
    public static String processPayment(String user, double amount, String account, boolean approved) {
        if (user == null) {
            return "Null username";
        }
        if (user.isEmpty()) {
            return "Empty username";
        }
        if (amount < 0) {
            return "Invalid amount";
        }
        if (account == null) {
            return "Account missing";
        }
        if (!approved) {
            return "Not approved";
        }

        double balance = 1000.0 - amount;
        String status = (balance >= 0) ? "Payment successful" : "Insufficient funds";
        String tier;
        if (amount > 500) {
            tier = "HIGH VALUE";
        } else if (amount > 100) {
            tier = "MEDIUM VALUE";
        } else if (amount > 10) {
            tier = "LOW VALUE";
        } else {
            tier = "SMALL PAYMENT";
        }

        return status + " " + tier;
    }
    
   
}
