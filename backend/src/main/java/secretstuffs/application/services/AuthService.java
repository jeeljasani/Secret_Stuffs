package secretstuffs.application.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import secretstuffs.application.helpers.JwtHelper;
import secretstuffs.application.helpers.AuthHelper;
import secretstuffs.domain.dtos.commands.auth.RegisterUserCommand;
import secretstuffs.domain.dtos.exception.UserException;
import secretstuffs.domain.entities.User;
import secretstuffs.domain.entities.VerificationToken;
import secretstuffs.domain.models.responses.auth.LoginUserResponseDTO;
import secretstuffs.domain.models.responses.auth.RegisterUserResponseDTO;
import secretstuffs.domain.models.responses.ApiResponseDTO;
import secretstuffs.infrastructure.repositories.UserRepository;
import secretstuffs.infrastructure.repositories.VerificationTokenRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AuthHelper authHelper;
    private final JwtHelper jwtHelper;
    private final EmailService emailService;
    private final VerificationTokenRepository verificationTokenRepository;
    private static final long TOKEN_EXPIRATION_TIME_MS = 3600L * 1000;

    public AuthService(
            UserRepository userRepository,
            AuthHelper authHelper,
            JwtHelper jwtHelper,
            EmailService emailService,
            VerificationTokenRepository verificationTokenRepository
    ) {
        this.userRepository = userRepository;
        this.authHelper = authHelper;
        this.jwtHelper = jwtHelper;
        this.emailService = emailService;
        this.verificationTokenRepository = verificationTokenRepository;
    }

    public LoginUserResponseDTO authenticateUser(String email, String password) {
        User user = findUserByEmail(email);
        validateUserIsActive(user);
        validatePassword(password, user.getPassword());
        return buildLoginResponse(user);
    }

    public RegisterUserResponseDTO registerUser(RegisterUserCommand command) {
        ensureEmailIsUnique(command.getEmail());
        String encryptedPassword = authHelper.encryptPassword(command.getPassword());
        User user = createUser(command, encryptedPassword);
        return buildRegisterResponse(user);
    }

    private User createUser(RegisterUserCommand command, String encryptedPassword) {
        User.UserBuilder user = User.builder();
        user.firstName(command.getFirstName());
        user.lastName(command.getLastName());
        user.email(command.getEmail());
        user.password(encryptedPassword);
        user.profileImageURL(command.getProfileImageURL());
        user.active(false);

        return userRepository.save(user.build());
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> UserException.userNotFound(email));
    }

    private void validateUserIsActive(User user) {
        if (!user.isActive()) {
            throw UserException.userNotVerified();
        }
    }

    private void validatePassword(String rawPassword, String encryptedPassword) {
        if (!authHelper.passwordMatches(rawPassword, encryptedPassword)) {
            throw UserException.invalidCredentials();
        }
    }

    private void ensureEmailIsUnique(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw UserException.emailAlreadyTaken();
        }
    }

    private LoginUserResponseDTO buildLoginResponse(User user) {
        String token = jwtHelper.generateToken(user.getEmail());
        long expiresAt = calculateExpirationTime();
        return LoginUserResponseDTO.builder()
                .token(token)
                .expiresAt(expiresAt)
                .email(user.getEmail())
                .id(user.getId())
                .build();
    }

    private RegisterUserResponseDTO buildRegisterResponse(User user) {
        String token = jwtHelper.generateToken(user.getEmail());
        long expiresAt = calculateExpirationTime();
        RegisterUserResponseDTO.RegisterUserResponseDTOBuilder builder = RegisterUserResponseDTO.builder();
        builder.email(user.getEmail());
        builder.profileImageURL(user.getProfileImageURL());
        builder.active(user.isActive());
        builder.token(token);
        builder.expiresAt(expiresAt);
        return builder.build();
    }

    private long calculateExpirationTime() {
        return Instant.now().toEpochMilli() + TOKEN_EXPIRATION_TIME_MS;
    }

    public ApiResponseDTO<String> forgotPassword(String email) {
        VerificationToken token = new VerificationToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUserEmail(email);
        token.setExpiryDate(LocalDateTime.now().plusHours(1));
        verificationTokenRepository.save(token);
        ServletUriComponentsBuilder servletBuilder = ServletUriComponentsBuilder.fromCurrentContextPath();
        servletBuilder.path("/reset-password/");
        servletBuilder.path(token.getToken());
        emailService.sendForgotPasswordEmail(email, servletBuilder.toUriString());
        return new ApiResponseDTO<>("Password reset email sent!", HttpStatus.OK.value(), "Success");
    }

    public ApiResponseDTO<String> resetPassword(String token, String newPassword) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(UserException::invalidToken);
        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw UserException.expiredToken();
        }
        User user = findUserByEmail(verificationToken.getUserEmail());
        user.setPassword(authHelper.encryptPassword(newPassword));
        userRepository.save(user);
        verificationTokenRepository.delete(verificationToken);
        return new ApiResponseDTO<>("Password successfully reset.", HttpStatus.OK.value(), "Success");
    }
}