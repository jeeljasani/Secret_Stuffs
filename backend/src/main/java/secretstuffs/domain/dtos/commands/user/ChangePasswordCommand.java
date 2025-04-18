package secretstuffs.domain.dtos.commands.user;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordCommand {
    private String email;
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;

    public boolean isValid() {
        return isNotEmpty(email) &&
                isNotEmpty(oldPassword) &&
                isNotEmpty(newPassword) &&
                passwordsMatch();
    }

    private boolean isNotEmpty(String value) {
        return value != null && !value.isEmpty();
    }

    private boolean passwordsMatch() {
        return newPassword.equals(confirmPassword);
    }
}