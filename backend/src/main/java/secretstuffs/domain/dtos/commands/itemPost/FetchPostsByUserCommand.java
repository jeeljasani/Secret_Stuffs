package secretstuffs.domain.dtos.commands.itemPost;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FetchPostsByUserCommand {
    private String email;

    public boolean isValid() {
        return email != null && !email.isEmpty();
    }
}

