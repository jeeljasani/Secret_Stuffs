package secretstuffs.domain.dtos.commands.user;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDetailsCommand {
    private String email;
    private String firstName;
    private String lastName;
    private String profileImageURL;
    
    public boolean isValid() {
        return isNotEmpty(email) &&
                isNotEmpty(firstName) &&
                isNotEmpty(lastName);
    }

    private boolean isNotEmpty(String value) {
        return value != null && !value.isEmpty();
    }
}