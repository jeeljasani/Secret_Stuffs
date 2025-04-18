package secretstuffs.domain.models.responses.auth;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserResponseDTO {
    private String token;
    private final String tokenType = "Bearer";
    private long expiresAt;
    private String email;
    private String profileImageURL;
    private boolean active;

    public boolean isValid() {
        return email != null && !email.isEmpty() && token != null && !token.isEmpty();
    }
}