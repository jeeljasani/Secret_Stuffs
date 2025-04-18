package secretstuffs.domain.dtos.commands.itemPost;

import lombok.*;
import secretstuffs.domain.enums.CategoryEnum;
import secretstuffs.domain.enums.ConditionEnum;

@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateItemPostCommand {
    private Long id;
    private String title;
    private String description;
    private String address;
    private CategoryEnum category;
    private ConditionEnum condition;

    public boolean isValid() {
        return isValidId(id) &&
                isNotEmpty(title) &&
                isNotEmpty(address) &&
                category != null &&
                condition != null;
    }

    private boolean isValidId(Long value) {
        return value != null && value > 0;
    }

    private boolean isNotEmpty(String value) {
        return value != null && !value.isEmpty();
    }

    public void setPostId(Long postId) {
        id = postId;
    }
}
