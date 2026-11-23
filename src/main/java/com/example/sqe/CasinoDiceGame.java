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
}
