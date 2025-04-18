package secretstuffs.domain.dtos.commands.itemPost;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import secretstuffs.domain.enums.CategoryEnum;
import secretstuffs.domain.enums.ConditionEnum;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateItemPostCommand {
    private String email;
    private String title;
    private String address;
    private String description;
    private ConditionEnum condition;
    private String itemPostUrl;
    private CategoryEnum category;

    public boolean isValid() {
        return isNotEmpty(email) &&
                isNotEmpty(title) &&
                isNotEmpty(address) &&
                condition != null &&
                category != null;
    }

    private boolean isNotEmpty(String value) {
        return value != null && !value.isEmpty();
    }
}