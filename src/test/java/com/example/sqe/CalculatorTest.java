package com.example.sqe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Calculator Tests")
class CalculatorTest {

    private Calculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }

    @Test
    @DisplayName("Should add two numbers correctly")
    void testAdd() {
        assertEquals(5.0, calculator.add(2.0, 3.0), 0.0001);
        assertEquals(-1.0, calculator.add(2.0, -3.0), 0.0001);
        assertEquals(0.0, calculator.add(0.0, 0.0), 0.0001);
    }

    @Test
    @DisplayName("Should subtract two numbers correctly")
    void testSubtract() {
        assertEquals(2.0, calculator.subtract(5.0, 3.0), 0.0001);
        assertEquals(8.0, calculator.subtract(5.0, -3.0), 0.0001);
    }

    @Test
    @DisplayName("Should multiply two numbers correctly")
    void testMultiply() {
        assertEquals(15.0, calculator.multiply(3.0, 5.0), 0.0001);
        assertEquals(-15.0, calculator.multiply(3.0, -5.0), 0.0001);
        assertEquals(0.0, calculator.multiply(5.0, 0.0), 0.0001);
    }

    @Test
    @DisplayName("Should divide two numbers correctly")
    void testDivide() {
        assertEquals(2.5, calculator.divide(5.0, 2.0), 0.0001);
        assertEquals(-2.0, calculator.divide(6.0, -3.0), 0.0001);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when dividing by zero")
    void testDivideByZero() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> calculator.divide(5.0, 0.0)
        );
        assertEquals("Division by zero is not allowed.", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
        "0, 1",
        "1, 1",
        "2, 2",
        "3, 6",
        "4, 24",
        "5, 120"
    })
    @DisplayName("Should calculate factorial correctly for non-negative integers")
    void testFactorial(int n, long expected) {
        assertEquals(expected, calculator.factorial(n));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for negative factorial")
    void testFactorialNegative() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> calculator.factorial(-1)
        );
        assertEquals("Factorial is not defined for negative numbers.", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31})
    @DisplayName("Should return true for prime numbers")
    void testIsPrimeTrue(int prime) {
        assertTrue(calculator.isPrime(prime));
    }

    @ParameterizedTest
    @ValueSource(ints = {-5, 0, 1, 4, 6, 8, 9, 25, 27, 35, 49, 77})
    @DisplayName("Should return false for non-prime numbers")
    void testIsPrimeFalse(int nonPrime) {
        assertFalse(calculator.isPrime(nonPrime));
    }
}
