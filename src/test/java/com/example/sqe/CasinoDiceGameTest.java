// package com.example.sqe;

// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.params.ParameterizedTest;
// import org.junit.jupiter.params.provider.CsvSource;
// import org.junit.jupiter.params.provider.ValueSource;

// import java.util.List;

// import static org.junit.jupiter.api.Assertions.*;

// @DisplayName("Casino Dice Game Tests")
// class CasinoDiceGameTest {

//     /**
//      * Deterministic test subclass enabling precise testing of high-low dice outcomes
//      * and complete branch coverage on CasinoDiceGame without relying on random probabilities.
//      */
//     private static class DeterministicCasinoDiceGame extends CasinoDiceGame {
//         private final int[] rolls;
//         private int rollIndex = 0;

//         DeterministicCasinoDiceGame(int initialBalance, int... rolls) {
//             super(initialBalance);
//             this.rolls = rolls;
//         }

//         @Override
//         public int rollDie() {
//             if (rolls == null || rolls.length == 0) {
//                 return super.rollDie();
//             }
//             return rolls[rollIndex++ % rolls.length];
//         }
//     }

//     @Test
//     @DisplayName("Should initialize game with valid balance")
//     void testConstructorValidBalance() {
//         CasinoDiceGame game = new CasinoDiceGame(500);
//         assertEquals(500, game.getPlayerBalance());

//         CasinoDiceGame zeroBalanceGame = new CasinoDiceGame(0);
//         assertEquals(0, zeroBalanceGame.getPlayerBalance());
//     }

//     @ParameterizedTest
//     @ValueSource(ints = {-1, -50, -1000})
//     @DisplayName("Should throw IllegalArgumentException when initialized with negative balance")
//     void testConstructorNegativeBalance(int negativeBalance) {
//         IllegalArgumentException exception = assertThrows(
//             IllegalArgumentException.class,
//             () -> new CasinoDiceGame(negativeBalance)
//         );
//         assertEquals("Balance cannot be negative.", exception.getMessage());
//     }

//     @Test
//     @DisplayName("Should roll single die within valid range (1 to 6)")
//     void testRollDie() {
//         CasinoDiceGame game = new CasinoDiceGame(100);
//         for (int i = 0; i < 50; i++) {
//             int roll = game.rollDie();
//             assertTrue(roll >= 1 && roll <= 6, "Roll value should be between 1 and 6 inclusive");
//         }
//     }

//     @Test
//     @DisplayName("Should roll multiple dice correctly")
//     void testRollMultipleDice() {
//         CasinoDiceGame game = new CasinoDiceGame(100);

//         List<Integer> zeroRolls = game.rollMultipleDice(0);
//         assertNotNull(zeroRolls);
//         assertTrue(zeroRolls.isEmpty());

//         List<Integer> fiveRolls = game.rollMultipleDice(5);
//         assertEquals(5, fiveRolls.size());
//         for (int roll : fiveRolls) {
//             assertTrue(roll >= 1 && roll <= 6);
//         }
//     }

//     @ParameterizedTest
//     @ValueSource(ints = {0, -1, -50})
//     @DisplayName("Should reject non-positive bet amounts in playHighLowBet")
//     void testPlayHighLowBetNonPositive(int invalidBet) {
//         CasinoDiceGame game = new CasinoDiceGame(100);
//         IllegalArgumentException exception = assertThrows(
//             IllegalArgumentException.class,
//             () -> game.playHighLowBet(invalidBet, true)
//         );
//         assertEquals("Invalid bet amount.", exception.getMessage());
//     }

//     @Test
//     @DisplayName("Should reject bet amount greater than player balance")
//     void testPlayHighLowBetExceedingBalance() {
//         CasinoDiceGame game = new CasinoDiceGame(100);
//         IllegalArgumentException exception = assertThrows(
//             IllegalArgumentException.class,
//             () -> game.playHighLowBet(101, true)
//         );
//         assertEquals("Invalid bet amount.", exception.getMessage());
//     }

//     @Test
//     @DisplayName("Should win bet when roll is high (>= 8) and bet is high")
//     void testPlayHighLowBetHighWin() {
//         // Rolls: 4 + 4 = 8 (high)
//         DeterministicCasinoDiceGame game = new DeterministicCasinoDiceGame(100, 4, 4);
//         boolean won = game.playHighLowBet(25, true);

//         assertTrue(won);
//         assertEquals(125, game.getPlayerBalance());
//     }

//     @Test
//     @DisplayName("Should lose bet when roll is high (>= 8) and bet is low")
//     void testPlayHighLowBetHighLoss() {
//         // Rolls: 5 + 3 = 8 (high)
//         DeterministicCasinoDiceGame game = new DeterministicCasinoDiceGame(100, 5, 3);
//         boolean won = game.playHighLowBet(25, false);

//         assertFalse(won);
//         assertEquals(75, game.getPlayerBalance());
//     }

//     @Test
//     @DisplayName("Should win bet when roll is low (< 8) and bet is low")
//     void testPlayHighLowBetLowWin() {
//         // Rolls: 2 + 3 = 5 (low)
//         DeterministicCasinoDiceGame game = new DeterministicCasinoDiceGame(100, 2, 3);
//         boolean won = game.playHighLowBet(30, false);

//         assertTrue(won);
//         assertEquals(130, game.getPlayerBalance());
//     }

//     @Test
//     @DisplayName("Should lose bet when roll is low (< 8) and bet is high")
//     void testPlayHighLowBetLowLoss() {
//         // Rolls: 1 + 2 = 3 (low)
//         DeterministicCasinoDiceGame game = new DeterministicCasinoDiceGame(100, 1, 2);
//         boolean won = game.playHighLowBet(30, true);

//         assertFalse(won);
//         assertEquals(70, game.getPlayerBalance());
//     }

//     @Test
//     @DisplayName("Should play high low bet using standard instance rollDie fallback")
//     void testPlayHighLowBetStandardInstance() {
//         CasinoDiceGame game = new CasinoDiceGame(100);
//         int initialBalance = game.getPlayerBalance();
//         boolean won = game.playHighLowBet(10, true);

//         if (won) {
//             assertEquals(initialBalance + 10, game.getPlayerBalance());
//         } else {
//             assertEquals(initialBalance - 10, game.getPlayerBalance());
//         }
//     }

//     @Test
//     @DisplayName("Should validate null and empty usernames in processPayment")
//     void testProcessPaymentUserValidation() {
//         assertEquals("Null username", CasinoDiceGame.processPayment(null, 100.0, "ACC123", true));
//         assertEquals("Empty username", CasinoDiceGame.processPayment("", 100.0, "ACC123", true));
//     }

//     @Test
//     @DisplayName("Should validate negative amount in processPayment")
//     void testProcessPaymentAmountValidation() {
//         assertEquals("Invalid amount", CasinoDiceGame.processPayment("john_doe", -1.0, "ACC123", true));
//         assertEquals("Invalid amount", CasinoDiceGame.processPayment("john_doe", -0.01, "ACC123", true));
//     }

//     @Test
//     @DisplayName("Should validate null account in processPayment")
//     void testProcessPaymentAccountValidation() {
//         assertEquals("Account missing", CasinoDiceGame.processPayment("john_doe", 100.0, null, true));
//     }

//     @Test
//     @DisplayName("Should validate approval status in processPayment")
//     void testProcessPaymentApprovalValidation() {
//         assertEquals("Not approved", CasinoDiceGame.processPayment("john_doe", 100.0, "ACC123", false));
//     }

//     @ParameterizedTest
//     @CsvSource({
//         "600.0, HIGH VALUE",
//         "500.01, HIGH VALUE",
//         "500.0, MEDIUM VALUE",
//         "250.0, MEDIUM VALUE",
//         "100.01, MEDIUM VALUE",
//         "100.0, LOW VALUE",
//         "50.0, LOW VALUE",
//         "10.01, LOW VALUE",
//         "10.0, SMALL PAYMENT",
//         "5.0, SMALL PAYMENT",
//         "0.0, SMALL PAYMENT"
//     })
//     @DisplayName("Should correctly classify successful payment value tiers")
//     void testProcessPaymentTiers(double amount, String expectedTier) {
//         String result = CasinoDiceGame.processPayment("alice", amount, "ACC_001", true);
//         assertNotNull(result);
//         assertTrue(result.startsWith("Payment successful"), "Result should indicate successful payment");
//         assertTrue(result.endsWith(expectedTier), "Result should end with tier: " + expectedTier);
//     }

//     @ParameterizedTest
//     @CsvSource({
//         "1000.0, Payment successful",
//         "1000.01, Insufficient funds",
//         "1500.0, Insufficient funds"
//     })
//     @DisplayName("Should check balance sufficiency and report payment status correctly")
//     void testProcessPaymentBalanceSufficiency(double amount, String expectedStatus) {
//         String result = CasinoDiceGame.processPayment("bob", amount, "ACC_002", true);
//         assertNotNull(result);
//         assertTrue(result.startsWith(expectedStatus), "Result should start with: " + expectedStatus);
//     }
// }