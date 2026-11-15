package com.example.sqe;

/**
 * String utility service for reversing strings, checking palindromes, counting vowels, and truncation.
 */
public class StringService {

    public String reverse(String input) {
        if (input == null) {
            return null;
        }
        return new StringBuilder(input).reverse().toString();
    }

    public boolean isPalindrome(String input) {
        if (input == null) {
            return false;
        }
        String clean = input.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        if (clean.isEmpty()) {
            return true;
        }
        int left = 0;
        int right = clean.length() - 1;
        while (left < right) {
            if (clean.charAt(left) != clean.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }

    public int countVowels(String input) {
        if (input == null) {
            return 0;
        }
        int count = 0;
        String lower = input.toLowerCase();
        for (int i = 0; i < lower.length(); i++) {
            char c = lower.charAt(i);
            if (c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u') {
                count++;
            }
        }
        return count;
    }

    public String truncate(String input, int maxLength) {
        if (input == null) {
            return null;
        }
        if (maxLength < 0) {
            throw new IllegalArgumentException("Max length cannot be negative.");
        }
        if (input.length() <= maxLength) {
            return input;
        }
        return input.substring(0, maxLength) + "...";
    }
}
