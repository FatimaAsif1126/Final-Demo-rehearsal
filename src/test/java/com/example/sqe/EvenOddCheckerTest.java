package com.example.sqe;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("EvenOddChecker Tests")
class EvenOddCheckerTest {

    @Test
    @DisplayName("Should generate a number between 0 and 10 inclusive")
    void testGenerateNumberRange() {
        EvenOddChecker checker = new EvenOddChecker();
        for (int i = 0; i < 1000; i++) {
            int number = checker.generateNumber();
            assertTrue(number >= 0 && number <= 10, "Number out of range: " + number);
        }
    }

    @ParameterizedTest
    @CsvSource({
        "10, 0",
        "10, 5",
        "10, 10"
    })
    @DisplayName("Should map random offsets to the expected number")
    void testGenerateNumberUsingFixedRandom(int maxExclusive, int nextIntResult) {
        Random fixed = new Random() {
            @Override
            public int nextInt(int bound) {
                assertEquals(maxExclusive + 1, bound);
                return nextIntResult;
            }
        };
        assertEquals(nextIntResult, new EvenOddChecker(fixed).generateNumber());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 2, 4, 6, 8, 10})
    @DisplayName("Should return true for even numbers")
    void testIsEvenTrue(int number) {
        assertTrue(new EvenOddChecker().isEven(number));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 5, 7, 9})
    @DisplayName("Should return false for odd numbers")
    void testIsEvenFalse(int number) {
        assertFalse(new EvenOddChecker().isEven(number));
    }

    @ParameterizedTest
    @CsvSource({
        "0, 0 is even",
        "1, 1 is odd",
        "4, 4 is even",
        "7, 7 is odd",
        "10, 10 is even"
    })
    @DisplayName("Should describe a number as even or odd")
    void testDescribe(int number, String expected) {
        assertEquals(expected, new EvenOddChecker().describe(number));
    }

    @Test
    @DisplayName("Should describe the generated random number")
    void testCheckRandomNumber() {
        Random fixed = new Random() {
            @Override
            public int nextInt(int bound) {
                return 7;
            }
        };
        assertEquals("7 is odd", new EvenOddChecker(fixed).checkRandomNumber());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when Random is null")
    void testNullRandom() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new EvenOddChecker(null)
        );
        assertEquals("Random must not be null.", exception.getMessage());
    }
}
