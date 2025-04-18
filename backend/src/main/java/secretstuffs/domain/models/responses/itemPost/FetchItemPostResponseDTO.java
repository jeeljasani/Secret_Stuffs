package secretstuffs.domain.models.responses.itemPost;

import lombok.*;
import secretstuffs.domain.enums.CategoryEnum;
import secretstuffs.domain.enums.ConditionEnum;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FetchItemPostResponseDTO {
    private Long id;
    private String email;
    private String title;
    private String address;
    private String description;
    private ConditionEnum condition;
    private String itemPostImageUrl;
    private CategoryEnum category;

    public String getSummary() {
        return String.format("%s (%s) - %s", title, condition, category);
    }
}