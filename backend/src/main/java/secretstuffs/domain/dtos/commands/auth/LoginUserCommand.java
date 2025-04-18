package secretstuffs.domain.dtos.commands.auth;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserCommand {
    private String email;
    private String password;

    public boolean isValid() {
        return email != null && !email.isEmpty() && password != null && !password.isEmpty();
    }
}