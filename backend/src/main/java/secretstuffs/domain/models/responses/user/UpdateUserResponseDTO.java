package secretstuffs.domain.models.responses.user;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserResponseDTO {
    private String email;
    private String profileImageURL;
    private String firstName;
    private String lastName;

    /**
     * Validates if critical fields are correctly set.
     *
     * @return true if valid, false otherwise.
     */
    public boolean isValid() {
        return email != null && !email.isEmpty() && firstName != null && !firstName.isEmpty();
    }
}