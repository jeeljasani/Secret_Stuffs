package secretstuffs.domain.models.responses.auth;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserResponseDTO {
    private Long id;
    private String token;
    private final String tokenType = "Bearer";
    private long expiresAt;
    private String email;

    public String getFormattedDetails() {
        return String.format("Token Type: %s, Expires At: %d, Email: %s", tokenType, expiresAt, email);
    }
}