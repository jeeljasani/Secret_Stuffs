package secretstuffs.domain.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "chat_room")
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generate ID
    private Long id;

    @NotBlank(message = "Chat ID cannot be blank")
    private String chatId;

    @NotBlank(message = "Sender ID cannot be blank")
    private String senderId;

    @NotBlank(message = "Recipient ID cannot be blank")
    private String recipientId;

    public boolean isParticipant(String userId) {
        return senderId.equals(userId) || recipientId.equals(userId);
    }
}