package secretstuffs.domain.dtos.exception;

import org.springframework.http.HttpStatus;

public class UserException extends BusinessException {

    public UserException(String message, String code, HttpStatus status) {
        super(message, code, status);
    }

    public static UserException userNotFound(String email) {
        String message = "User with email " + email + " not found";
        return new UserException(message, "USER_NOT_FOUND", HttpStatus.NOT_FOUND);
    }

    public static UserException userNotFound(Long userId) {
        String message = "User with id " + userId + " not found";
        return new UserException(message, "USER_NOT_FOUND", HttpStatus.NOT_FOUND);
    }

    public static UserException invalidOldPassword() {
        String message = "Old password is incorrect";
        return new UserException(message, "INVALID_OLD_PASSWORD", HttpStatus.BAD_REQUEST);
    }

    public static UserException passwordsDoNotMatch() {
        String message = "New password and confirm password do not match";
        return new UserException(message, "PASSWORDS_DO_NOT_MATCH", HttpStatus.BAD_REQUEST);
    }

    public static UserException userAlreadyActive() {
        String message = "User is already active";
        return new UserException(message, "USER_ALREADY_ACTIVE", HttpStatus.BAD_REQUEST);
    }

    public static UserException userNotVerified() {
        String message = "User is not verified. Please verify your email.";
        return new UserException(message, "USER_NOT_VERIFIED", HttpStatus.UNAUTHORIZED);
    }

    public static UserException emailAlreadyTaken() {
        String message = "Email already taken";
        return new UserException(message, "EMAIL_ALREADY_TAKEN", HttpStatus.CONFLICT);
    }

    public static UserException invalidCredentials() {
        String message = "Invalid email or password";
        return new UserException(message, "INVALID_CREDENTIALS", HttpStatus.UNAUTHORIZED);
    }

    public static UserException expiredToken() {
        String message = "Token has expired";
        return new UserException(message, "EXPIRED_TOKEN", HttpStatus.BAD_REQUEST);
    }

    public static UserException invalidToken() {
        String message = "Invalid token";
        return new UserException(message, "INVALID_TOKEN", HttpStatus.BAD_REQUEST);
    }
}
