package secretstuffs.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import secretstuffs.application.services.ChatMessageService;
import secretstuffs.application.services.ChatRoomService;
import secretstuffs.domain.dtos.commands.chat.ChatNotification;
import secretstuffs.domain.entities.ChatMessage;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage) {
        ChatMessage savedMsg = chatMessageService.save(chatMessage);
        String destination = "/queue/messages/" + chatMessage.getSenderId();
        ChatNotification notification = new ChatNotification();
        notification.setId(savedMsg.getId());
        notification.setSenderId(savedMsg.getSenderId());
        notification.setRecipientId(savedMsg.getRecipientId());
        notification.setContent(savedMsg.getContent());

        String recipientId = chatMessage.getRecipientId();
        messagingTemplate.convertAndSendToUser(recipientId, destination, notification);
    }

    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<List<ChatMessage>> findChatMessages(@PathVariable String senderId,
            @PathVariable String recipientId) {
        return ResponseEntity.ok(chatMessageService.findChatMessages(senderId, recipientId));
    }

    @GetMapping("/recipients/{senderId}")
    public ResponseEntity<List<String>> getRecipientsBySender(@PathVariable String senderId) {
        List<String> recipientIds = chatRoomService.getUniqueRecipientsForSender(senderId).orElse(List.of());
        return ResponseEntity.ok(recipientIds);
    }

    @GetMapping("/exists/{senderId}/{recipientId}")
    public ResponseEntity<Boolean> doesChatExist(
            @PathVariable String senderId,
            @PathVariable String recipientId) {
        boolean exists = chatRoomService.doesChatExist(senderId, recipientId);
        return ResponseEntity.ok(exists);
    }
}