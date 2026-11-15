package com.example.sqe;

import java.util.regex.Pattern;

/**
 * User validation utility checking username, email, and age requirements.
 */
public class UserValidator {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public boolean validateUsername(String username) {
        if (username == null) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username).matches();
    }

    public boolean validateEmail(String email) {
        if (email == null) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public boolean validateAge(int age) {
        return age >= 18 && age <= 120;
    }

    public boolean isValidUser(String username, String email, int age) {
        return validateUsername(username) && validateEmail(email) && validateAge(age);
    }
}
