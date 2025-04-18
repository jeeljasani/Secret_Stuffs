package secretstuffs.infrastructure.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import secretstuffs.domain.entities.Donation;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {
    List<Donation> findByUser_Id(Long userId);
    List<Donation> findByItemPost_Id(Long itemPostId);
    Optional<Donation> findByUser_IdAndItemPost_Id(Long userId, Long itemPostId);
    Optional<Donation> findById(Long id);
}
