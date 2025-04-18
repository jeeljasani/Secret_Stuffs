package secretstuffs.domain.models.requests.itempost;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import secretstuffs.domain.enums.CategoryEnum;
import secretstuffs.domain.enums.ConditionEnum;
import secretstuffs.domain.validation.EnumValidator;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateItemPostRequestDTO {

    @NotBlank(message = "Email cannot be blank.")
    @Email(message = "Invalid email format.")
    @Size(max = 100, message = "Email cannot exceed 100 characters.")
    private String email;

    @NotBlank(message = "Title cannot be blank.")
    @Size(max = 50, message = "Title cannot exceed 50 characters.")
    private String title;

    @NotBlank(message = "Address cannot be blank.")
    @Size(max = 255, message = "Address cannot exceed 255 characters.")
    private String address;

    @NotBlank(message = "Description cannot be blank.")
    @Size(max = 500, message = "Description cannot exceed 500 characters.")
    private String description;

    @Size(max = 255, message = "Image URL cannot exceed 255 characters.")
    private String itemPostImageUrl;

    @EnumValidator(enumClass = ConditionEnum.class,
            message = "Invalid condition. Valid values are: " +
                    "NEW, LIKE_NEW, GOOD, FAIR, POOR, DAMAGED")
    private String condition;

    @EnumValidator(enumClass = CategoryEnum.class,
            message = "Invalid category. Valid values are: " +
                    "FURNITURE, ELECTRONICS, BOOKS, VEHICLE")
    private String category;

    public boolean isValid() {
        boolean isValid = isNotEmpty(email);
        isValid = isValid && isNotEmpty(title);
        isValid = isValid && isNotEmpty(address);
        isValid = isValid && isNotEmpty(description);
        isValid = isValid && condition != null;
        isValid = isValid && category != null;
        return isValid;
    }

    private boolean isNotEmpty(String value) {
        return value != null && !value.isEmpty();
    }
}