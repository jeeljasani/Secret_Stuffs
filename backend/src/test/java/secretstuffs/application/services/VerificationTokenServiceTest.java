package secretstuffs.application.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import secretstuffs.application.helpers.JwtHelper;

import java.util.Date;

class VerificationTokenServiceTest {

    @Mock
    private JwtHelper jwtHelper;

    @InjectMocks
    private VerificationTokenService tokenService;

    private static final long ONE_HOUR_IN_MILLIS = 3600000; // 1 hour in milliseconds

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createToken_ShouldGenerateToken_WhenEmailIsProvided() {
        // Arrange
        String email = "test@example.com";
        String expectedToken = "valid_token";

        when(jwtHelper.generateToken(email)).thenReturn(expectedToken);

        // Act
        String result = tokenService.createToken(email);

        // Assert
        assertEquals(expectedToken, result, "Generated token should match the expected token");
        verify(jwtHelper).generateToken(email);
    }

    @Test
    void verifyToken_ShouldReturnTrue_WhenTokenIsValid() {
        // Arrange
        String token = "valid_token";
        String email = "test@example.com";
        Date futureDate = new Date(System.currentTimeMillis() + ONE_HOUR_IN_MILLIS);

        when(jwtHelper.extractSubject(token)).thenReturn(email);
        when(jwtHelper.extractExpiration(token)).thenReturn(futureDate);
        when(jwtHelper.isTokenValid(token)).thenReturn(true);

        // Act
        boolean isValid = tokenService.verifyToken(token);

        // Assert
        assertTrue(isValid, "Valid token should return true");
        verify(jwtHelper).isTokenValid(token);
    }

    @Test
    void verifyToken_ShouldReturnFalse_WhenTokenIsExpired() {
        // Arrange
        String token = "expired_token";

        when(jwtHelper.isTokenValid(token)).thenReturn(false);

        // Act
        boolean isValid = tokenService.verifyToken(token);

        // Assert
        assertFalse(isValid, "Expired token should return false");
        verify(jwtHelper).isTokenValid(token);
    }

    @Test
    void verifyToken_ShouldReturnFalse_WhenTokenIsInvalid() {
        // Arrange
        String invalidToken = "invalid_token";

        when(jwtHelper.isTokenValid(invalidToken)).thenReturn(false);

        // Act
        boolean isValid = tokenService.verifyToken(invalidToken);

        // Assert
        assertFalse(isValid, "Invalid token should return false");
        verify(jwtHelper).isTokenValid(invalidToken);
    }

    @Test
    void extractEmailFromToken_ShouldReturnEmail_WhenTokenIsValid() {
        // Arrange
        String token = "valid_token";
        String expectedEmail = "test@example.com";

        when(jwtHelper.getValidSubject(token)).thenReturn(expectedEmail);

        // Act
        String email = tokenService.extractEmailFromToken(token);

        // Assert
        assertEquals(expectedEmail, email, "Extracted email should match the expected email");
        verify(jwtHelper).getValidSubject(token);
    }
}