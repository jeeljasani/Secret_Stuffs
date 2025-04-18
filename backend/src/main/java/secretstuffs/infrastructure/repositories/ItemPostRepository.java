package secretstuffs.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import secretstuffs.domain.entities.ItemPost;
import secretstuffs.domain.enums.ItemPostStatusEnum;

import java.util.List;

@Repository
public interface ItemPostRepository extends JpaRepository<ItemPost, Long> {
    List<ItemPost> findAllByEmail(String email);
    List<ItemPost> findAllByStatus(ItemPostStatusEnum status);
    boolean existsById(Long id);
}
