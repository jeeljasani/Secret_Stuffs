package secretstuffs.domain.models.requests.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequestDTO {

    @NotBlank(message = "Old password is required")
    private String oldPassword;

    @NotBlank(message = "New password is required")
    private String newPassword;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    public boolean isValid() {
        return isNotEmpty(oldPassword) &&
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
