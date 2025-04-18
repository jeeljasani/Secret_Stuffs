package secretstuffs.application.helpers;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Stubber;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

class JwtHelperTest {

    private static final String TEST_SECRET_KEY = Arrays.toString(Keys.secretKeyFor(SignatureAlgorithm.HS512).getEncoded());
    private static final int TOKEN_EXPIRATION_OFFSET = 10; // Offset for token expiration simulation
    private static final int MILLISECONDS_IN_A_SECOND = 1000; // Conversion factor for milliseconds to seconds
    private JwtHelper jwtHelper;
    private String testUsername;
    private String testToken;

    @BeforeEach
    void setUp() {
        jwtHelper = new JwtHelper(TEST_SECRET_KEY);
        testUsername = "testUser";
        testToken = jwtHelper.generateToken(testUsername);
    }

    @Test
    void generateTokenWithClaims_ShouldIncludeClaimsInToken() {
        // Arrange
        Map<String, Object> claims = Map.of("role", "admin", "organization", "testOrg");

        // Act
        String token = jwtHelper.generateTokenWithClaims(claims, testUsername);

        // Assert
        assertNotNull(token, "Generated token should not be null");
        assertTrue(jwtHelper.validateToken(token, testUsername), "Token should be valid for the given username");

        // Extract claims
        String extractedRole = jwtHelper.extractClaim(token, claimsMap -> claimsMap.get("role", String.class));
        String extractedOrganization = jwtHelper.extractClaim(token, claimsMap -> claimsMap.get("organization", String.class));

        assertEquals("admin", extractedRole, "Extracted role should match the input claim");
        assertEquals("testOrg", extractedOrganization, "Extracted organization should match the input claim");
    }

    @Test
    void extractSubject_ShouldMatchUsernameInToken() {
        // Act
        String extractedSubject = jwtHelper.extractSubject(testToken);

        // Assert
        assertEquals(testUsername, extractedSubject, "Extracted subject should match the username in the token");
    }

    @Test
    void extractExpiration_ShouldReturnFutureDate() {
        // Act
        Date expirationDate = jwtHelper.extractExpiration(testToken);

        // Assert
        assertNotNull(expirationDate, "Expiration date should not be null");
        assertTrue(expirationDate.after(new Date()), "Expiration date should be in the future");
    }

    @Test
    void validateToken_ShouldVerifyValidToken() {
        // Act
        boolean isValid = jwtHelper.validateToken(testToken, testUsername);

        // Assert
        assertTrue(isValid, "Validation should succeed for a valid token and username");
    }

    @Test
    void validateToken_ShouldFailForIncorrectUsername() {
        // Act
        boolean isValid = jwtHelper.validateToken(testToken, "wrongUser");

        // Assert
        assertFalse(isValid, "Validation should fail for an incorrect username");
    }

    @Test
    void isTokenExpired_ShouldReturnFalseForValidToken() {
        // Act
        boolean isExpired = jwtHelper.isTokenExpired(testToken);

        // Assert
        assertFalse(isExpired, "Token should not be expired");
    }

    @Test
    void isTokenValid_ShouldReturnTrueForValidToken() {
        // Act
        boolean isValid = jwtHelper.isTokenValid(testToken);

        // Assert
        assertTrue(isValid, "Token should be valid");
    }

    @Test
    void isTokenValid_ShouldReturnFalseForInvalidToken() {
        // Arrange
        String invalidToken = "invalid.token.content";

        // Act
        boolean isValid = jwtHelper.isTokenValid(invalidToken);

        // Assert
        assertFalse(isValid, "Invalid token should not be valid");
    }

    @Test
    void getValidSubject_ShouldReturnSubjectForValidToken() {
        // Act
        String subject = jwtHelper.getValidSubject(testToken);

        // Assert
        assertEquals(testUsername, subject, "Valid subject should be extracted for a valid token");
    }

    @Test
    void getValidSubject_ShouldReturnNullForInvalidToken() {
        // Arrange
        String invalidToken = "invalid.token.content";

        // Act
        String subject = jwtHelper.getValidSubject(invalidToken);

        // Assert
        assertNull(subject, "Subject should be null for an invalid token");
    }

    @Test
    void extractAllClaims_ShouldThrowExceptionForInvalidToken() {
        // Arrange
        String invalidToken = "invalid.token.content";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            jwtHelper.extractClaim(invalidToken, Claims::getSubject);
        }, "Invalid token should throw IllegalArgumentException");
    }

    @Test
    void generateToken_ShouldGenerateValidToken() {
        String token = jwtHelper.generateToken(testUsername);

        assertNotNull(token, "Generated token should not be null");
        assertFalse(token.isEmpty(), "Generated token should not be empty");
        assertTrue(jwtHelper.validateToken(token, testUsername), "Token should be valid for the given username");
    }

    @Test
    void isTokenExpired_ShouldReturnTrueForExpiredToken() {
        // Arrange: Create a token with the current behavior
        String token = jwtHelper.generateToken(testUsername);

        // Mock the expiration check to simulate an expired token
        JwtHelper mockJwtHelper = spy(jwtHelper);
        Date d = new Date(System.currentTimeMillis() - TOKEN_EXPIRATION_OFFSET * MILLISECONDS_IN_A_SECOND);
        Stubber s = doReturn(d);
        s.when(mockJwtHelper).extractExpiration(token);

        // Act
        boolean isExpired = mockJwtHelper.isTokenExpired(token);

        // Assert
        assertTrue(isExpired, "Token should be expired");
    }

    @Test
    void validateToken_ShouldFailForExpiredToken() {
        // Arrange: Create a token with the current behavior
        String token = jwtHelper.generateToken(testUsername);

        // Mock the expiration check to simulate an expired token
        JwtHelper mockJwtHelper = spy(jwtHelper);
        Date d = new Date(System.currentTimeMillis() - TOKEN_EXPIRATION_OFFSET * MILLISECONDS_IN_A_SECOND);
        Stubber s = doReturn(d);
        s.when(mockJwtHelper).extractExpiration(token);

        // Act
        boolean isValid = mockJwtHelper.validateToken(token, testUsername);

        // Assert
        assertFalse(isValid, "Validation should fail for an expired token");
    }
}