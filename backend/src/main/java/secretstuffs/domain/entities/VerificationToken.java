package secretstuffs.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "verification_token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Token cannot be blank")
    private String token;

    @Column(name = "user_email", nullable = false)
    @Email(message = "Invalid email format")
    @NotBlank(message = "User email cannot be blank")
    private String userEmail;

    @Column(name = "expiry_date", nullable = false)
    @NotNull(message = "Expiry date cannot be null")
    private LocalDateTime expiryDate;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }
}