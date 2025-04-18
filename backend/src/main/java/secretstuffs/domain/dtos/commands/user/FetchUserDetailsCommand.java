package secretstuffs.domain.dtos.commands.user;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FetchUserDetailsCommand {
    private String email;

    public boolean isValid() {
        return email != null && !email.isEmpty();
    }
}