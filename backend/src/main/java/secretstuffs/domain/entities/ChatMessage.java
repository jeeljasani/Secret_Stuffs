package secretstuffs.domain.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "chat_message")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    private String id;

    @NotBlank(message = "Chat ID cannot be blank")
    private String chatId;

    @NotBlank(message = "Sender ID cannot be blank")
    private String senderId;

    @NotBlank(message = "Recipient ID cannot be blank")
    private String recipientId;

    @NotBlank(message = "Content cannot be blank")
    @Size(max = 500, message = "Content cannot exceed 500 characters")
    private String content;

    @NotNull(message = "Timestamp cannot be null")
    @PastOrPresent(message = "Timestamp must be in the past or present")
    private LocalDateTime timestamp;

    public boolean isRecent() {
        return timestamp.isAfter(LocalDateTime.now().minusDays(1));
    }
}