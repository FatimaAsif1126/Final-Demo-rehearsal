package com.example.sqe;

import java.util.Random;

/**
 * Generates a random number between 0 and 10 (inclusive) and reports whether it is even or odd.
 */
public class EvenOddChecker {

    private static final int MIN = 0;
    private static final int MAX = 10;

    private final Random random;

    public EvenOddChecker() {
        this(new Random());
    }

    public EvenOddChecker(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("Random must not be null.");
        }
        this.random = random;
    }

    public int generateNumber() {
        return random.nextInt(MAX - MIN + 1) + MIN;
    }

    public boolean isEven(int number) {
        return number % 2 == 0;
    }

    public String describe(int number) {
        return number + " is " + (isEven(number) ? "even" : "odd");
    }

    public String checkRandomNumber() {
        return describe(generateNumber());
    }
}
