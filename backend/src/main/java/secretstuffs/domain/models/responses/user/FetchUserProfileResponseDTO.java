package secretstuffs.domain.models.responses.user;

import lombok.*;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FetchUserProfileResponseDTO
{
    private Long id;
    private String email;
    private String profileImageURL;
    private boolean active;
    private String firstName;
    private String lastName;

    /**
     * Combines the first and last name into a full name.
     *
     * @return the full name of the user.
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
