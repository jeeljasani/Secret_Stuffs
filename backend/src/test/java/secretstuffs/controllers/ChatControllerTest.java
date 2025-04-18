package secretstuffs.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import secretstuffs.application.services.ChatMessageService;
import secretstuffs.application.services.ChatRoomService;
import secretstuffs.domain.dtos.commands.chat.ChatNotification;
import secretstuffs.domain.entities.ChatMessage;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ChatControllerTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private ChatMessageService chatMessageService;

    @Mock
    private ChatRoomService chatRoomService;

    @InjectMocks
    private ChatController chatController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void processMessage_ShouldSaveMessageAndSendNotification() {
        // Arrange
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSenderId("sender123");
        chatMessage.setRecipientId("recipient123");
        chatMessage.setContent("Hello!");

        ChatMessage savedMessage = new ChatMessage();
        savedMessage.setId("msg123");
        savedMessage.setSenderId("sender123");
        savedMessage.setRecipientId("recipient123");
        savedMessage.setContent("Hello!");

        String expRecipientId = "recipient123";
        String expSenderId = "sender123";
        String expContent = "Hello!";

        when(chatMessageService.save(chatMessage)).thenReturn(savedMessage);
        chatController.processMessage(chatMessage);

        // Act
        verify(chatMessageService, times(1)).save(chatMessage);

        // Prepare parameters for the verify statement
        String destination = "/queue/messages/" + expSenderId;
        ArgumentMatcher<Object> notificationMatcher = noti ->
                isValidChatNotification(noti, expSenderId, expRecipientId, expContent);
        verify(messagingTemplate, times(1)).convertAndSendToUser(eq(expRecipientId),eq(destination),argThat(notificationMatcher));
    }

    // Helper Method
    private boolean isValidChatNotification(Object notification, String expectedSenderId, String expectedRecipientId, String expectedContent) {
        if (!(notification instanceof ChatNotification)) {
            return false;
        }
        ChatNotification chatNotification = (ChatNotification) notification;
        boolean senderMatches = chatNotification.getSenderId().equals(expectedSenderId);
        boolean recipientMatches = chatNotification.getRecipientId().equals(expectedRecipientId);
        boolean contentMatches = chatNotification.getContent().equals(expectedContent);
        return senderMatches && recipientMatches && contentMatches;
    }

    @Test
    void findChatMessages_ShouldReturnChatMessages() {
        String senderId = "sender123";
        String recipientId = "recipient123";
        String chatId = "sender123_recipient123";
        String content1 = "Hello!";
        String content2 = "How are you?";

        ChatMessage msg1 = new ChatMessage("msg1", chatId, senderId, recipientId, content1, null);
        ChatMessage msg2 = new ChatMessage("msg2", chatId, senderId, recipientId, content2, null);

        List<ChatMessage> messages = List.of(msg1, msg2);
        when(chatMessageService.findChatMessages(senderId, recipientId)).thenReturn(messages);
        ResponseEntity<List<ChatMessage>> response = chatController.findChatMessages(senderId, recipientId);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("msg1", response.getBody().get(0).getId());
        assertEquals("Hello!", response.getBody().get(0).getContent());
        verify(chatMessageService, times(1)).findChatMessages(senderId, recipientId);
    }

    @Test
    void getRecipientsBySender_ShouldReturnRecipientIds() {
        String senderId = "sender123";
        List<String> recipientIds = List.of("recipient123", "recipient456");
        when(chatRoomService.getUniqueRecipientsForSender(senderId)).thenReturn(Optional.of(recipientIds));
        ResponseEntity<List<String>> response = chatController.getRecipientsBySender(senderId);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("recipient123", response.getBody().get(0));
        assertEquals("recipient456", response.getBody().get(1));
        verify(chatRoomService, times(1)).getUniqueRecipientsForSender(senderId);
    }

    @Test
    void getRecipientsBySender_ShouldReturnEmptyListWhenNoRecipientsFound() {
        String senderId = "sender123";
        when(chatRoomService.getUniqueRecipientsForSender(senderId)).thenReturn(Optional.empty());
        ResponseEntity<List<String>> response = chatController.getRecipientsBySender(senderId);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().size());
        verify(chatRoomService, times(1)).getUniqueRecipientsForSender(senderId);
    }

    @Test
    void doesChatExist_ShouldReturnTrue_WhenChatExists() {
        String senderId = "sender123";
        String recipientId = "recipient123";
        when(chatRoomService.doesChatExist(senderId, recipientId)).thenReturn(true);
        ResponseEntity<Boolean> response = chatController.doesChatExist(senderId, recipientId);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody());
        verify(chatRoomService, times(1)).doesChatExist(senderId, recipientId);
    }

    @Test
    void doesChatExist_ShouldReturnFalse_WhenChatDoesNotExist() {
        String senderId = "sender123";
        String recipientId = "recipient123";
        when(chatRoomService.doesChatExist(senderId, recipientId)).thenReturn(false);
        ResponseEntity<Boolean> response = chatController.doesChatExist(senderId, recipientId);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertFalse(response.getBody());
        verify(chatRoomService, times(1)).doesChatExist(senderId, recipientId);
    }
}