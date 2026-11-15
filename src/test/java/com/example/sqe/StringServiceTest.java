package com.example.sqe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("String Service Tests")
class StringServiceTest {

    private StringService stringService;

    @BeforeEach
    void setUp() {
        stringService = new StringService();
    }

    @Test
    @DisplayName("Should reverse string correctly")
    void testReverse() {
        assertEquals("olleh", stringService.reverse("hello"));
        assertEquals("a", stringService.reverse("a"));
        assertEquals("", stringService.reverse(""));
        assertNull(stringService.reverse(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "racecar",
        "Madam",
        "A man, a plan, a canal: Panama",
        "Was it a car or a cat I saw?",
        "",
        "a"
    })
    @DisplayName("Should identify valid palindromes")
    void testIsPalindromeTrue(String input) {
        assertTrue(stringService.isPalindrome(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"hello", "world", "java testing", "openai"})
    @DisplayName("Should identify non-palindromes")
    void testIsPalindromeFalse(String input) {
        assertFalse(stringService.isPalindrome(input));
    }

    @Test
    @DisplayName("Should return false for null palindrome input")
    void testIsPalindromeNull() {
        assertFalse(stringService.isPalindrome(null));
    }

    @ParameterizedTest
    @CsvSource({
        "hello, 2",
        "HELLO, 2",
        "rhythm, 0",
        "aeiou, 5",
        "Quick Brown Fox, 4",
        "'', 0"
    })
    @DisplayName("Should count vowels correctly")
    void testCountVowels(String input, int expectedCount) {
        assertEquals(expectedCount, stringService.countVowels(input));
    }

    @Test
    @DisplayName("Should return 0 vowels for null input")
    void testCountVowelsNull() {
        assertEquals(0, stringService.countVowels(null));
    }

    @Test
    @DisplayName("Should truncate strings longer than max length")
    void testTruncateLongString() {
        assertEquals("Hello...", stringService.truncate("Hello World", 5));
    }

    @Test
    @DisplayName("Should not truncate strings shorter than or equal to max length")
    void testTruncateShortOrEqualString() {
        assertEquals("Hello", stringService.truncate("Hello", 5));
        assertEquals("Hi", stringService.truncate("Hi", 5));
    }

    @Test
    @DisplayName("Should handle null string in truncate")
    void testTruncateNull() {
        assertNull(stringService.truncate(null, 5));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when max length is negative")
    void testTruncateNegativeLength() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> stringService.truncate("test", -1)
        );
        assertEquals("Max length cannot be negative.", exception.getMessage());
    }
}
