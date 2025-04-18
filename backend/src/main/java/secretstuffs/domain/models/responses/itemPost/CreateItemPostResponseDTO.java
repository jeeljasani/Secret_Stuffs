package secretstuffs.domain.models.responses.itemPost;

import lombok.*;
import secretstuffs.domain.enums.CategoryEnum;
import secretstuffs.domain.enums.ConditionEnum;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateItemPostResponseDTO {
    private String email;
    private String title;
    private String address;
    private String description;
    private ConditionEnum condition;
    private String itemPostImageUrl;
    private CategoryEnum category;

    public boolean isValid() {
        return email != null && !email.isEmpty() && title != null && !title.isEmpty();
    }
}