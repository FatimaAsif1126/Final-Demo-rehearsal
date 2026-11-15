package com.example.sqe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Validator Tests")
class UserValidatorTest {

    private UserValidator validator;

    @BeforeEach
    void setUp() {
        validator = new UserValidator();
    }

    @ParameterizedTest
    @ValueSource(strings = {"john_doe", "alice123", "User_99", "abc"})
    @DisplayName("Should accept valid usernames")
    void testValidateUsernameValid(String username) {
        assertTrue(validator.validateUsername(username));
    }

    @ParameterizedTest
    @ValueSource(strings = {"ab", "this_username_is_way_too_long_to_be_valid", "invalid-char!", "space name"})
    @DisplayName("Should reject invalid usernames")
    void testValidateUsernameInvalid(String username) {
        assertFalse(validator.validateUsername(username));
    }

    @Test
    @DisplayName("Should reject null username")
    void testValidateUsernameNull() {
        assertFalse(validator.validateUsername(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"user@example.com", "first.last@company.org", "dev+tag@sub.domain.co"})
    @DisplayName("Should accept valid email addresses")
    void testValidateEmailValid(String email) {
        assertTrue(validator.validateEmail(email));
    }

    @ParameterizedTest
    @ValueSource(strings = {"plainaddress", "missing@domain", "@missinguser.com", "user@.com", "user@domain.c"})
    @DisplayName("Should reject invalid email addresses")
    void testValidateEmailInvalid(String email) {
        assertFalse(validator.validateEmail(email));
    }

    @Test
    @DisplayName("Should reject null email")
    void testValidateEmailNull() {
        assertFalse(validator.validateEmail(null));
    }

    @ParameterizedTest
    @ValueSource(ints = {18, 25, 40, 65, 120})
    @DisplayName("Should accept valid age between 18 and 120")
    void testValidateAgeValid(int age) {
        assertTrue(validator.validateAge(age));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 10, 17, 121, 150})
    @DisplayName("Should reject invalid age")
    void testValidateAgeInvalid(int age) {
        assertFalse(validator.validateAge(age));
    }

    @Test
    @DisplayName("Should validate complete user correctly")
    void testIsValidUser() {
        assertTrue(validator.isValidUser("alice_99", "alice@test.com", 25));
        assertFalse(validator.isValidUser("ab", "alice@test.com", 25));
        assertFalse(validator.isValidUser("alice_99", "invalid-email", 25));
        assertFalse(validator.isValidUser("alice_99", "alice@test.com", 16));
        assertFalse(validator.isValidUser(null, null, 0));
    }
}
