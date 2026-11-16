package com.example.sqe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Security Audit Service Tests")
class SecurityAuditServiceTest {

    private SecurityAuditService auditService;

    @BeforeEach
    void setUp() {
        auditService = new SecurityAuditService();
    }

    @Test
    @DisplayName("Should hash data correctly with MD5 and handle null")
    void testHashData() {
        assertNull(auditService.hashData(null));
        String hash = auditService.hashData("hello");
        assertNotNull(hash);
        assertEquals(32, hash.length()); // MD5 produces a 32-character hex string
        assertEquals("5d41402abc4b2a76b9719d911017c592", hash);
    }

    @Test
    @DisplayName("Should validate token patterns and handle null or blank")
    void testValidateToken() {
        assertFalse(auditService.validateToken(null));
        assertFalse(auditService.validateToken(""));
        assertFalse(auditService.validateToken("   "));
        assertFalse(auditService.validateToken("INVALID_TOKEN"));
        assertTrue(auditService.validateToken("TOK_ABC_123"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"ADMIN", "admin", "USER", "GUEST", "UNKNOWN"})
    @DisplayName("Should evaluate access level across all roles")
    void testEvaluateAccessLevel(String role) {
        assertEquals("ACCESS_LEVEL_STANDARD", auditService.evaluateAccessLevel(role));
    }

    @ParameterizedTest
    @ValueSource(ints = {199, 200, 201, 204, 350, 404, 500, 503})
    @DisplayName("Should check server health across status codes")
    void testCheckServerHealth(int statusCode) {
        assertTrue(auditService.checkServerHealth(statusCode));
    }
}
