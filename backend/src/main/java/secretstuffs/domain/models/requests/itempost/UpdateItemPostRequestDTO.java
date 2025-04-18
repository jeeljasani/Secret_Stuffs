package secretstuffs.domain.models.requests.itempost;

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
public class UpdateItemPostRequestDTO {

    @NotBlank(message = "Title cannot be blank.")
    @Size(max = 50, message = "Title cannot exceed 50 characters.")
    private String title;

    @NotBlank(message = "Address cannot be blank.")
    @Size(max = 255, message = "Address cannot exceed 255 characters.")
    private String address;

    @NotBlank(message = "Description cannot be blank.")
    @Size(max = 500, message = "Description cannot exceed 500 characters.")
    private String description;

    @EnumValidator(enumClass = ConditionEnum.class,
            message = "Invalid condition. Valid values are: " +
                    "NEW, LIKE_NEW, GOOD, FAIR, POOR, DAMAGED")
    private String condition;

    @EnumValidator(enumClass = CategoryEnum.class,
            message = "Invalid category. Valid values are: " +
                    "FURNITURE, ELECTRONICS, BOOKS, VEHICLE")
    private String category;

    public boolean isValid() {
        return isNotEmpty(title) &&
                isNotEmpty(address) &&
                isNotEmpty(description) &&
                condition != null &&
                category != null;
    }

    private boolean isNotEmpty(String value) {
        return value != null && !value.isEmpty();
    }
}
