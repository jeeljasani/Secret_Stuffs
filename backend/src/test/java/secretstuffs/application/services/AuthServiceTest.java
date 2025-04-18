package secretstuffs.application.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import secretstuffs.domain.dtos.commands.auth.RegisterUserCommand;
import secretstuffs.domain.dtos.exception.UserException;
import secretstuffs.domain.entities.User;
import secretstuffs.domain.entities.VerificationToken;
import secretstuffs.domain.models.responses.auth.LoginUserResponseDTO;
import secretstuffs.domain.models.responses.auth.RegisterUserResponseDTO;
import secretstuffs.domain.models.responses.ApiResponseDTO;
import secretstuffs.infrastructure.repositories.UserRepository;
import secretstuffs.infrastructure.repositories.VerificationTokenRepository;
import secretstuffs.application.helpers.JwtHelper;
import secretstuffs.application.helpers.AuthHelper;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtHelper jwtHelper;

    @Mock
    private AuthHelper authHelper;

    @Mock
    private VerificationTokenRepository verificationTokenRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // Mock request context to avoid "No current ServletRequestAttributes" error
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attributes);
    }

    private static final int THIRTY_MINUTES = 30;

    @Test
    void authenticateUser_ShouldReturnToken_WhenValidCredentials() {
        User user = new User();
        user.setEmail("newuser@example.com");
        user.setPassword("encryptedPassword");
        user.setActive(true);

        when(userRepository.findByEmail("newuser@example.com")).thenReturn(Optional.of(user));
        when(authHelper.passwordMatches("password", "encryptedPassword")).thenReturn(true);
        when(jwtHelper.generateToken("newuser@example.com")).thenReturn("valid_token");

        LoginUserResponseDTO result = authService.authenticateUser("newuser@example.com", "password");

        assertNotNull(result.getToken());
        assertEquals("valid_token", result.getToken());
    }

    @Test
    void registerUser_ShouldReturnResponse_WhenValidData() {
        RegisterUserCommand command = new RegisterUserCommand();
        command.setFirstName("John");
        command.setLastName("Doe");
        command.setEmail("newuser@example.com");
        command.setPassword("password");
        command.setProfileImageURL("profile.jpg");

        User mockUser = new User();
        mockUser.setEmail(command.getEmail());
        mockUser.setFirstName(command.getFirstName());
        mockUser.setLastName(command.getLastName());
        mockUser.setProfileImageURL(command.getProfileImageURL());
        mockUser.setActive(false);
        mockUser.setPassword("encryptedPassword");

        when(userRepository.findByEmail(command.getEmail())).thenReturn(Optional.empty());
        when(authHelper.encryptPassword(command.getPassword())).thenReturn("encryptedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(jwtHelper.generateToken(command.getEmail())).thenReturn("valid_token");

        RegisterUserResponseDTO result = authService.registerUser(command);

        assertNotNull(result);
        assertEquals(command.getEmail(), result.getEmail());
        assertEquals("profile.jpg", result.getProfileImageURL());
        assertFalse(result.isActive());
        assertEquals("valid_token", result.getToken());
    }

    @Test
    void registerUser_ShouldThrowException_WhenEmailExists() {
        RegisterUserCommand command = new RegisterUserCommand();
        command.setEmail("existing@example.com");
        when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(new User()));

        assertThrows(UserException.class, () -> authService.registerUser(command));
    }

    @Test
    void authenticateUser_ShouldThrowException_WhenUserIsInactive() {
        User user = new User();
        user.setEmail("inactive@example.com");
        user.setPassword("password");
        user.setActive(false); // Inactive user

        when(userRepository.findByEmail("inactive@example.com")).thenReturn(Optional.of(user));

        assertThrows(UserException.class, () -> authService.authenticateUser("inactive@example.com", "password"));
    }

    // New tests for Forgot Password and Reset Password
    @Test
    void forgotPassword_ShouldSendEmail_WhenEmailIsValid() {
        String email = "user@example.com";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        ApiResponseDTO<String> result = authService.forgotPassword(email);

        assertEquals("Password reset email sent!", result.getMessage());
        verify(emailService, times(1)).sendForgotPasswordEmail(eq(email), anyString());
    }


    @Test
    void resetPassword_ShouldResetPassword_WhenTokenIsValid() {
        String token = UUID.randomUUID().toString();
        String email = "user@example.com";
        String newPassword = "newPassword";
        User user = new User();
        user.setEmail(email);
        user.setPassword("oldPassword");

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUserEmail(email);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(1));

        when(verificationTokenRepository.findByToken(token)).thenReturn(Optional.of(verificationToken));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(authHelper.encryptPassword(newPassword)).thenReturn("newEncryptedPassword");

        ApiResponseDTO<String> result = authService.resetPassword(token, newPassword);

        assertEquals("Password successfully reset.", result.getMessage());
        assertEquals("newEncryptedPassword", user.getPassword());
        verify(verificationTokenRepository, times(1)).delete(verificationToken);
    }

    @Test
    void resetPassword_ShouldThrowException_WhenTokenIsExpired() {
        String token = UUID.randomUUID().toString();
        String email = "user@example.com";
        String newPassword = "newPassword";

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUserEmail(email);
        verificationToken.setExpiryDate(LocalDateTime.now().minusHours(1));  // Expired token

        when(verificationTokenRepository.findByToken(token)).thenReturn(Optional.of(verificationToken));

        assertThrows(UserException.class, () -> authService.resetPassword(token, newPassword));
    }

    @Test
    void resetPassword_ShouldThrowException_WhenTokenIsInvalid() {
        String token = UUID.randomUUID().toString();
        String email = "user@example.com";
        String newPassword = "newPassword";

        when(verificationTokenRepository.findByToken(token)).thenReturn(Optional.empty());

        assertThrows(UserException.class, () -> authService.resetPassword(token, newPassword));
    }
}
