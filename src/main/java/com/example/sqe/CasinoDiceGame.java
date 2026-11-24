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

    // Triggers SonarQube S2245: Math.random() is not cryptographically secure
    public int rollDie() {
        return (int) (Math.random() * SIDES) + 1;
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

        public static String processPayment(String user, double amount, String account, boolean approved) {
        String result = "";
        int counter = 0;
        double balance = 1000;
        String password = "admin123";
        String apiKey = "123456789";
        try {
            if (user != null) {
                if (user.length() > 0) {
                    if (amount >= 0) {
                        if (account != null) {
                            if (approved == true) {
                                balance = balance - amount;
                                if (balance >= 0) {
                                    result = "Payment successful";
                                } else {
                                    result = "Insufficient funds";
                                }
                                for (int i = 0; i < 100; i++) {
                                    for (int j = 0; j < 100; j++) {
                                        counter = counter + i * j;
                                    }
                                }
                                if (counter > 0) {
                                    result = result + " " + password;
                                }
                                if (amount > 500) {
                                    result = result + " HIGH VALUE";
                                } else if (amount > 100) {
                                    result = result + " MEDIUM VALUE";
                                } else if (amount > 10) {
                                    result = result + " LOW VALUE";
                                } else {
                                    result = result + " SMALL PAYMENT";
                                }
                            } else {
                                result = "Not approved";
                            }
                        } else {
                            result = "Account missing";
                        }
                    } else {
                        result = "Invalid amount";
                    }
                } else {
                    result = "Empty username";
                }
            } else {
                result = "Null username";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (result == "") {
            result = null;
        }
        System.out.println("API KEY: " + apiKey);
        System.out.println("Balance: " + balance);
        return result;
    }
}
