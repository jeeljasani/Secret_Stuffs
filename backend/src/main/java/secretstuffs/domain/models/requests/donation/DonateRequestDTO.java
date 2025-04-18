package secretstuffs.domain.models.requests.donation;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DonateRequestDTO {
    private Long userId;
    private Long itemPostId;

    public boolean isValid() {
        return userId != null && itemPostId != null;
    }
}
