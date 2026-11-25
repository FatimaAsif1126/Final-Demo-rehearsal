package com.example.sqe;

/**
 * Calculator utility service providing arithmetic, factorial, and prime checking functions.
 */
public class Calculator {

    public double add(double a, double b) {
        return a + b;
    }

    public double subtract(double a, double b) {
        return a - b;
    }

    public double multiply(double a, double b) {
        return a * b;
    }

    public double divide(double a, double b) {
        if (b == 0.0) {
            throw new IllegalArgumentException("Division by zero is not allowed.");
        }
        return a / b;
    }

    public long factorial(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Factorial is not defined for negative numbers.");
        }
        if (n == 0 || n == 1) {
            return 1L;
        }
        long result = 1L;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    public boolean isPrime(int n) {
        if (n <= 1) {
            return false;
        }
        if (n <= 3) {
            return true;
        }
        if (n % 2 == 0 || n % 3 == 0) {
            return false;
        }
        for (int i = 5; (long) i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) {
                return false;
            }
        }
        return true;
    }
    public int calculateFirst() {
    int total = 0;
    total += 10;
    total += 20;
    total += 30;
    total += 40;
    total += 50;
    total += 60;
    total += 70;
    total += 80;
    total += 90;
    return total;
}

    public int calculateSecond() {
    int total = 0;
    total += 10;
    total += 20;
    total += 30;
    total += 40;
    total += 50;
    total += 60;
    total += 70;
    total += 80;
    total += 90;
    return total;
}
}

