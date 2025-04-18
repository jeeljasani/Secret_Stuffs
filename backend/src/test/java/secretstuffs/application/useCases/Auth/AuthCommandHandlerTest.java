package secretstuffs.application.useCases.Auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import secretstuffs.application.services.*;
import secretstuffs.application.useCases.auth.AuthCommandHandler;
import secretstuffs.domain.dtos.commands.auth.*;
import secretstuffs.domain.models.responses.auth.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthCommandHandlerTest {

    @InjectMocks
    private AuthCommandHandler authCommandHandler;

    @Mock
    private AuthService authService;

    @Mock
    private VerificationTokenService tokenService;

    @Mock
    private EmailService emailService;

    @Mock
    private UserService userService;

    private static final long ONE_HOUR_IN_MILLISECONDS = 3600000L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLogin() {
        // Arrange
        LoginUserCommand command = new LoginUserCommand("test@example.com", "password");
        long expirationTime = System.currentTimeMillis() + ONE_HOUR_IN_MILLISECONDS; // 1 hour from now
        String token = "token123";
        long id = 1L;
        String email = "test@example.com";
        LoginUserResponseDTO responseDTO = new LoginUserResponseDTO(
                id,
                token,
                expirationTime,
                email
        );
        when(authService.authenticateUser(command.getEmail(), command.getPassword())).thenReturn(responseDTO);
        // Act
        LoginUserResponseDTO result = authCommandHandler.login(command);
        // Assert
        assertNotNull(result);
        assertEquals("token123", result.getToken());
        verify(authService, times(1)).authenticateUser(command.getEmail(), command.getPassword());
    }

    @Test
    void testRegister() {
        // Arrange
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setContextPath("/api");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));

        String firstName = "John";
        String lastName = "Doe";
        String email = "johndoe@example.com";
        String password = "password123";
        String profileImageUrl = "https://example.com/profile.jpg";

        RegisterUserCommand command = new RegisterUserCommand(
                firstName,
                lastName,
                email,
                password,
                profileImageUrl
        );

        String token = "token123";
        long expirationTime = System.currentTimeMillis() + ONE_HOUR_IN_MILLISECONDS; // 1 hour from now
        RegisterUserResponseDTO.RegisterUserResponseDTOBuilder responseDTO = RegisterUserResponseDTO.builder();
        responseDTO.token(token);
        responseDTO.expiresAt(expirationTime);
        responseDTO.email(email);
        responseDTO.profileImageURL(profileImageUrl);
        responseDTO.active(true);
        responseDTO.build();

        when(authService.registerUser(command)).thenReturn(responseDTO.build());

        // Act
        RegisterUserResponseDTO result = authCommandHandler.register(command);

        // Assert
        assertNotNull(result);
        assertEquals(token, result.getToken());
        assertEquals("Bearer", result.getTokenType());
        assertEquals(email, result.getEmail());

        verify(authService, times(1)).registerUser(command);
        verify(emailService, times(1)).sendVerificationEmail(eq(email), anyString());

        // Clean-up
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void testVerifyEmailTokenSuccess() {
        String token = "validToken";
        String email = "test@example.com";
        when(tokenService.extractEmailFromToken(token)).thenReturn(email);
        when(tokenService.verifyToken(token)).thenReturn(true);
        boolean result = authCommandHandler.verifyEmailToken(token);
        assertTrue(result);
        verify(userService, times(1)).activateUser(email);
    }

    @Test
    void testVerifyEmailTokenFailure() {
        String token = "invalidToken";
        when(tokenService.verifyToken(token)).thenReturn(false);
        boolean result = authCommandHandler.verifyEmailToken(token);
        assertFalse(result);
        verify(userService, never()).activateUser(anyString());
    }

    @Test
    void testResendVerificationEmailSuccess() {
        String email = "johndoe@example.com";
        String token = "generatedToken";
        when(userService.isUserRegistered(email)).thenReturn(true);
        when(userService.isUserActive(email)).thenReturn(false);
        when(tokenService.createToken(email)).thenReturn(token);
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setContextPath("/api");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));
        boolean result = authCommandHandler.resendVerificationEmail(email);
        assertTrue(result);
        verify(emailService, times(1)).sendVerificationEmail(eq(email), contains("/api/auth/verify-email"));
        verify(tokenService, times(1)).createToken(email);
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void testResendVerificationEmailFailure() {
        String email = "test@example.com";
        when(userService.isUserRegistered(email)).thenReturn(false);
        boolean result = authCommandHandler.resendVerificationEmail(email);
        assertFalse(result);
        verify(emailService, never()).sendVerificationEmail(anyString(), anyString());
    }

    @Test
    void testSendVerificationEmail() {
        String email = "johndoe@example.com";
        String token = "generatedToken";
        when(tokenService.createToken(email)).thenReturn(token);
        when(userService.isUserRegistered(email)).thenReturn(true);
        when(userService.isUserActive(email)).thenReturn(false);
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setContextPath("/api");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));
        authCommandHandler.resendVerificationEmail(email);
        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailService, times(1)).sendVerificationEmail(emailCaptor.capture(), urlCaptor.capture());
        String capturedEmail = emailCaptor.getValue();
        String capturedUrl = urlCaptor.getValue();
        assertEquals(email, capturedEmail);
        assertTrue(capturedUrl.contains("/api/auth/verify-email"));
        assertTrue(capturedUrl.contains("token=generatedToken"));
        verify(tokenService, times(1)).createToken(email);
        RequestContextHolder.resetRequestAttributes();
    }
}
