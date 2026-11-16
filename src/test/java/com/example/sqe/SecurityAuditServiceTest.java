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
    @DisplayName("Should throw IllegalStateException when MD5 algorithm is unavailable")
    void testHashDataAlgorithmUnavailable() {
        java.security.Provider sun = java.security.Security.getProvider("SUN");
        try {
            java.security.Security.removeProvider("SUN");
            IllegalStateException ex = assertThrows(IllegalStateException.class, () -> auditService.hashData("test"));
            assertTrue(ex.getCause() instanceof java.security.NoSuchAlgorithmException);
            assertEquals("Algorithm unavailable", ex.getMessage());
        } finally {
            if (sun != null) {
                java.security.Security.addProvider(sun);
            }
        }
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
