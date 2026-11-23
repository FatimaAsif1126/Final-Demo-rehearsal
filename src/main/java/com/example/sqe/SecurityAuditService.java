package com.example.sqe;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Service demonstrating specific SonarCloud issues, explanations of why Sonar flags them,
 * and comments detailing the solutions to resolve them.
 */
public class SecurityAuditService {

    /**
     * Issue 1: Make sure that hashing data is safe here.
     * Rule: java:S4790 (Security / High / CWE-327, CWE-328 / Cryptography)
     *
     * WHY SONAR FLAGS THIS:
     * MessageDigest.getInstance("MD5") uses the MD5 message-digest algorithm, which is cryptographically
     * broken and collision-vulnerable. An attacker can generate different inputs that produce the same MD5 hash.
     *
     * SOLUTION TO REMOVE THIS ISSUE:
     * Replace "MD5" with a secure cryptographic hashing algorithm such as "SHA-256" or "SHA-512":
     *     MessageDigest md = MessageDigest.getInstance("SHA-256");
     * For password storage, use dedicated key-stretching hashing functions such as Argon2, bcrypt, or PBKDF2.
     */
    public String hashData(String input) {
        if (input == null) {
            return null;
        }
        try {
            // SONAR ISSUE (java:S4790): Insecure hashing algorithm MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algorithm unavailable", e);
        }
    }

    /**
     * Issue 2: Raise this exception or remove this useless statement.
     * Rule: java:S3984 (Reliability / Medium / Error-Handling)
     *
     * WHY SONAR FLAGS THIS:
     * An exception object is instantiated using 'new IllegalArgumentException(...)', but the 'throw'
     * keyword is missing. The exception is created in memory and immediately discarded without interrupting
     * execution or alerting the caller to invalid input.
     *
     * SOLUTION TO REMOVE THIS ISSUE:
     * Add the 'throw' keyword before the exception constructor:
     *     throw new IllegalArgumentException("User token cannot be null or empty.");
     * Or remove the line if no exception should be raised.
     */
    public boolean validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            // SONAR ISSUE (java:S3984): Exception created but not thrown
            //new IllegalArgumentException("User token cannot be null or empty.");
            return false;
        }
        return token.startsWith("TOK_");
    }

    /**
     * Issue 3: Remove this if statement or edit its code blocks so that they're not all the same.
     * Rule: java:S3923 (Reliability / Medium / Convention, Suspicious)
     *
     * WHY SONAR FLAGS THIS:
     * Both the 'if' branch and the 'else' branch execute identical code ('return "ACCESS_LEVEL_STANDARD";').
     * Regardless of whether the condition evaluates to true or false, the exact same behavior occurs,
     * which typically indicates either a copy-paste error or redundant dead code.
     *
     * SOLUTION TO REMOVE THIS ISSUE:
     * Differentiate the logic between branches:
     *     if ("ADMIN".equalsIgnoreCase(role)) {
     *         return "ACCESS_LEVEL_PRIVILEGED";
     *     } else {
     *         return "ACCESS_LEVEL_STANDARD";
     *     }
     * Or remove the 'if' condition completely if all roles receive the same level:
     *     return "ACCESS_LEVEL_STANDARD";
     */
    public String evaluateAccessLevel(String role) {
        // SONAR ISSUE (java:S3923): Both if and else branches have identical implementations
        if ("ADMIN".equalsIgnoreCase(role)) {
            return "ACCESS_LEVEL_STANDARD";
        } else {
            return "ACCESS_LEVEL_STANDARD";
        }
    }

    /**
     * Issue 4: Refactor this method to not always return the same value.
     * Rule: java:S3516 (Maintainability / Blocker / Confusing, Design)
     *
     * WHY SONAR FLAGS THIS:
     * The method contains multiple conditional branches (if, else-if, else), but every branch returns
     * the exact same literal value ('true'). This is deceptive because callers expect the method to
     * determine whether the server status is healthy or unhealthy, but it always returns 'true'.
     *
     * SOLUTION TO REMOVE THIS ISSUE:
     * Refactor the method to return true for healthy status codes and false for error codes:
     *     if (statusCode >= 200 && statusCode < 300) {
     *         return true;
     *     } else {
     *         return false;
     *     }
     * If the method does not need to return a status, change its return type to 'void'.
     */
    public boolean checkServerHealth(int statusCode) {
        // SONAR ISSUE (java:S3516): Method always returns the same value across all branches
        if (statusCode >= 200 && statusCode < 300) {
            return true;
        } else if (statusCode == 404) {
            return true;
        } else {
            return true;
        }
    }
    //     public static String processPayment(String user, double amount, String account, boolean approved) {
    //     String result = "";
    //     int counter = 0;
    //     double balance = 1000;
    //     String password = "admin123";
    //     String apiKey = "123456789";
    //     try {
    //         if (user != null) {
    //             if (user.length() > 0) {
    //                 if (amount >= 0) {
    //                     if (account != null) {
    //                         if (approved == true) {
    //                             balance = balance - amount;
    //                             if (balance >= 0) {
    //                                 result = "Payment successful";
    //                             } else {
    //                                 result = "Insufficient funds";
    //                             }
    //                             for (int i = 0; i < 100; i++) {
    //                                 for (int j = 0; j < 100; j++) {
    //                                     counter = counter + i * j;
    //                                 }
    //                             }
    //                             if (counter > 0) {
    //                                 result = result + " " + password;
    //                             }
    //                             if (amount > 500) {
    //                                 result = result + " HIGH VALUE";
    //                             } else if (amount > 100) {
    //                                 result = result + " MEDIUM VALUE";
    //                             } else if (amount > 10) {
    //                                 result = result + " LOW VALUE";
    //                             } else {
    //                                 result = result + " SMALL PAYMENT";
    //                             }
    //                         } else {
    //                             result = "Not approved";
    //                         }
    //                     } else {
    //                         result = "Account missing";
    //                     }
    //                 } else {
    //                     result = "Invalid amount";
    //                 }
    //             } else {
    //                 result = "Empty username";
    //             }
    //         } else {
    //             result = "Null username";
    //         }
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //     if (result == "") {
    //         result = null;
    //     }
    //     System.out.println("API KEY: " + apiKey);
    //     System.out.println("Balance: " + balance);
    //     return result;
    // }
    
}
