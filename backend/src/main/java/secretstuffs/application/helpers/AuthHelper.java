package secretstuffs.application.helpers;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AuthHelper {

    private final BCryptPasswordEncoder encoder;

    public AuthHelper() {
        this.encoder = new BCryptPasswordEncoder();
    }

    public String encryptPassword(String password) {
        return encoder.encode(password);
    }

    public boolean passwordMatches(String rawPassword, String encryptedPassword) {
        return encoder.matches(rawPassword, encryptedPassword);
    }
}
