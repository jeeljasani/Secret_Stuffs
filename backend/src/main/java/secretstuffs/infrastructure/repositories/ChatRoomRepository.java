package secretstuffs.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import secretstuffs.domain.entities.ChatRoom;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // Find a chat room by sender and recipient
    Optional<ChatRoom> findBySenderIdAndRecipientId(String senderId, String recipientId);

    // Query to find unique recipients for a sender
    @Query("SELECT DISTINCT c.recipientId FROM ChatRoom c WHERE c.senderId = :senderId")
    Optional<List<String>> findUniqueRecipientsBySenderId(String senderId);

    // Check if a chat room exists between sender and recipient (bidirectional)
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END " +
            "FROM ChatRoom c " +
            "WHERE (c.senderId = :senderId AND c.recipientId = :recipientId) " +
            "OR (c.senderId = :recipientId AND c.recipientId = :senderId)")
    boolean existsBySenderAndRecipient(String senderId, String recipientId);
}