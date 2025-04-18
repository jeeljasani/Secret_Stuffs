package secretstuffs.application.useCases.auth;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import secretstuffs.application.services.*;
import secretstuffs.domain.dtos.commands.auth.*;
import secretstuffs.domain.models.responses.auth.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class AuthCommandHandler {

    private final AuthService authService;
    private final VerificationTokenService tokenService;
    private final EmailService emailService;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(AuthCommandHandler.class);

    public LoginUserResponseDTO login(LoginUserCommand command) {
        return authService.authenticateUser(command.getEmail(), command.getPassword());
    }

    public RegisterUserResponseDTO register(RegisterUserCommand command) {
        RegisterUserResponseDTO response = authService.registerUser(command);
        sendVerificationEmail(command.getEmail());
        return response;
    }

    public boolean verifyEmailToken(String token) {
        String email = tokenService.extractEmailFromToken(token);
        if (tokenService.verifyToken(token)) {
            userService.activateUser(email);
            return true;
        }
        return false;
    }

    public boolean resendVerificationEmail(String email) {
        if (userService.isUserRegistered(email) && !userService.isUserActive(email)) {
            sendVerificationEmail(email);
            return true;
        }
        return false;
    }

    private void sendVerificationEmail(String email) {
        String token = tokenService.createToken(email);
        ServletUriComponentsBuilder verificationLink = ServletUriComponentsBuilder.fromCurrentContextPath();
        verificationLink.path("/api/auth/verify-email");
        verificationLink.queryParam("token", token);
        logger.info("Verification link generated for email {}: {}", email, verificationLink);
        emailService.sendVerificationEmail(email, verificationLink.toUriString());
    }
}