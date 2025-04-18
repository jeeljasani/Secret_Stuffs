package secretstuffs.domain.models.requests.user;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequestDTO {
    private String firstName;
    private String lastName;
    private String profileImageURL;

    public boolean isValid() {
        return firstName != null && !firstName.isEmpty() &&
                lastName != null && !lastName.isEmpty();
    }
}