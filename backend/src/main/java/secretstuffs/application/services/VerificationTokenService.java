package secretstuffs.application.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import secretstuffs.application.helpers.JwtHelper;

@Service
@RequiredArgsConstructor
public class VerificationTokenService {

    private final JwtHelper jwtHelper;

    /**
     * Creates a verification token for the given email.
     *
     * @param email the email to include in the token
     * @return the generated token
     */
    public String createToken(String email) {
        return jwtHelper.generateToken(email);
    }

    /**
     * Verifies the token by delegating validation to JwtHelper.
     *
     * @param token the JWT token
     * @return true if the token is valid, false otherwise
     */
    public boolean verifyToken(String token) {
        return jwtHelper.isTokenValid(token);
    }

    /**
     * Extracts the email (subject) from a valid token.
     *
     * @param token the JWT token
     * @return the extracted email if valid, or null if invalid
     */
    public String extractEmailFromToken(String token) {
        return jwtHelper.getValidSubject(token);
    }
}