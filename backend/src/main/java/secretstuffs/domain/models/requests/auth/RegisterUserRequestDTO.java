package secretstuffs.domain.models.requests.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserRequestDTO {

    @NotNull(message = "First name is required")
    @Size(min = 2, message = "First name must be at least 2 characters long")
    private String firstName;

    @NotNull(message = "Last name is required")
    @Size(min = 2, message = "Last name must be at least 2 characters long")
    private String lastName;

    @NotNull(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    private String profileImageURL; // Optional field for the user's profile image

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