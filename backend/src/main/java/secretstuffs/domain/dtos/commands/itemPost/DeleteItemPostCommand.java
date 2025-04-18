package secretstuffs.domain.dtos.commands.itemPost;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeleteItemPostCommand {
    private Long id;

    public boolean isValid() {
        return id != null && id > 0;
    }
}



