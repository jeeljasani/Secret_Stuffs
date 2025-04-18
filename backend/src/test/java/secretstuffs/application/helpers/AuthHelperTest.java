package secretstuffs.application.helpers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthHelperTest {

    private AuthHelper authHelper;

    @BeforeEach
    void setUp() {
        authHelper = new AuthHelper();
    }

    @Test
    void encryptPassword_ShouldReturnEncodedPassword() {
        String rawPassword = "password123";
        String encryptedPassword = authHelper.encryptPassword(rawPassword);
        assertNotNull(encryptedPassword, "Encrypted password should not be null");
        assertNotEquals(rawPassword, encryptedPassword, "Encrypted password should not match the raw password");
    }

    @Test
    void passwordMatches_ShouldReturnTrueForMatchingPasswords() {
        String rawPassword = "password123";
        String encryptedPassword = authHelper.encryptPassword(rawPassword);
        boolean matches = authHelper.passwordMatches(rawPassword, encryptedPassword);
        assertTrue(matches, "Password should match the encrypted password");
    }

    @Test
    void passwordMatches_ShouldReturnFalseForNonMatchingPasswords() {
        String rawPassword = "password123";
        String encryptedPassword = authHelper.encryptPassword(rawPassword);
        boolean matches = authHelper.passwordMatches("wrongPassword123", encryptedPassword);
        assertFalse(matches, "Password should not match the encrypted password for a different raw password");
    }

    @Test
    void encryptPassword_ShouldGenerateDifferentHashesForSamePassword() {
        String rawPassword = "password123";
        String encryptedPassword1 = authHelper.encryptPassword(rawPassword);
        String encryptedPassword2 = authHelper.encryptPassword(rawPassword);
        assertNotEquals(encryptedPassword1, encryptedPassword2, "Encrypting the same password should generate different hashes");
    }
}
