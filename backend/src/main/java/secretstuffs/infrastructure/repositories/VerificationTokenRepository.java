package secretstuffs.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import secretstuffs.domain.entities.VerificationToken;
import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);

    VerificationToken save(VerificationToken token);
}
