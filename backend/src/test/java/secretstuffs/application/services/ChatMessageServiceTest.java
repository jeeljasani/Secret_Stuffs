package secretstuffs.application.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import secretstuffs.domain.entities.ChatMessage;
import secretstuffs.infrastructure.repositories.ChatMessageRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChatMessageServiceTest {

    @Mock
    private ChatMessageRepository repository;

    @Mock
    private ChatRoomService chatRoomService;

    @InjectMocks
    private ChatMessageService chatMessageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveChatMessage_WhenChatRoomIdExists() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSenderId("sender123");
        chatMessage.setRecipientId("recipient123");
        String chatRoomId = "chatRoom123";

        String senderId = chatMessage.getSenderId();
        String recipientId = chatMessage.getRecipientId();
        when(chatRoomService.getChatRoomId(senderId, recipientId, true))
                .thenReturn(Optional.of(chatRoomId));
        ChatMessage savedMessage = chatMessageService.save(chatMessage);
        assertNotNull(savedMessage.getChatId());
        assertEquals(chatRoomId, savedMessage.getChatId());
        assertNotNull(savedMessage.getTimestamp());
        verify(repository, times(1)).save(savedMessage);
    }

    @Test
    void saveChatMessage_ThrowException_WhenChatRoomIdNotFound() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSenderId("sender123");
        chatMessage.setRecipientId("recipient123");

        String senderId = chatMessage.getSenderId();
        String recipientId = chatMessage.getRecipientId();
        when(chatRoomService.getChatRoomId(senderId, recipientId, true))
                .thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> chatMessageService.save(chatMessage));
        verify(repository, never()).save(any(ChatMessage.class));
    }

    @Test
    void findChatMessages_ReturnMessages_WhenChatRoomIdExists() {
        String senderId = "sender123";
        String recipientId = "recipient123";
        String chatRoomId = "chatRoom123";
        List<ChatMessage> messages = List.of(new ChatMessage(), new ChatMessage());
        when(chatRoomService.getChatRoomId(senderId, recipientId, false)).thenReturn(Optional.of(chatRoomId));
        when(repository.findByChatId(chatRoomId)).thenReturn(messages);
        assertDoesNotThrow(() -> chatMessageService.findChatMessages(senderId, recipientId));
        verify(repository, times(1)).findByChatId(chatRoomId);
    }

    @Test
    void findChatMessages_ReturnEmptyList_WhenChatRoomIdNotFound() {
        String senderId = "sender123";
        String recipientId = "recipient123";
        when(chatRoomService.getChatRoomId(senderId, recipientId, false)).thenReturn(Optional.empty());
        List<ChatMessage> result = chatMessageService.findChatMessages(senderId, recipientId);
        assertTrue(result.isEmpty());
        verify(repository, never()).findByChatId(anyString());
    }
}
