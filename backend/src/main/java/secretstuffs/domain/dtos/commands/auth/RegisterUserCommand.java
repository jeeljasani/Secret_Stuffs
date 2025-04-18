package secretstuffs.domain.dtos.commands.auth;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserCommand {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String profileImageURL;

    public boolean isValid() {
        return isNotEmpty(firstName) &&
                isNotEmpty(lastName) &&
                isNotEmpty(email) &&
                isNotEmpty(password);
    }

    private boolean isNotEmpty(String value) {
        return value != null && !value.isEmpty();
    }
}

