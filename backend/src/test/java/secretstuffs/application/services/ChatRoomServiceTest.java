package secretstuffs.application.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import secretstuffs.domain.entities.ChatRoom;
import secretstuffs.infrastructure.repositories.ChatRoomRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChatRoomServiceTest {

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @InjectMocks
    private ChatRoomService chatRoomService;

    @Captor
    private ArgumentCaptor<ChatRoom> chatRoomCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private static final int TWO = 2;
    @Test
    void getChatRoomId_ShouldReturnExistingChatId_WhenChatRoomExists() {
        String senderId = "user1";
        String recipientId = "user2";
        String existingChatId = "chatId123";
        ChatRoom existingChatRoom = ChatRoom.builder()
                .chatId(existingChatId)
                .senderId(senderId)
                .recipientId(recipientId)
                .build();
        when(chatRoomRepository.findBySenderIdAndRecipientId(senderId, recipientId))
                .thenReturn(Optional.of(existingChatRoom));
        Optional<String> chatRoomId = chatRoomService.getChatRoomId(senderId, recipientId, false);
        assertTrue(chatRoomId.isPresent());
        assertEquals(existingChatId, chatRoomId.get());
        verify(chatRoomRepository, times(1)).findBySenderIdAndRecipientId(senderId, recipientId);
        verifyNoMoreInteractions(chatRoomRepository);
    }

    @Test
    void getChatRoomId_ShouldCreateNewChatRoom_WhenNotExistsAndCreateNewIsTrue() {
        String senderId = "user1";
        String recipientId = "user2";
        String expectedChatId = "user1_user2";
        when(chatRoomRepository.findBySenderIdAndRecipientId(senderId, recipientId))
                .thenReturn(Optional.empty());
        Optional<String> chatRoomId = chatRoomService.getChatRoomId(senderId, recipientId, true);
        assertTrue(chatRoomId.isPresent());
        assertEquals(expectedChatId, chatRoomId.get());
        verify(chatRoomRepository, times(1)).findBySenderIdAndRecipientId(senderId, recipientId);
        verify(chatRoomRepository, times(TWO)).save(chatRoomCaptor.capture());
        List<ChatRoom> savedChatRooms = chatRoomCaptor.getAllValues();
        ChatRoom chatRoom1 = savedChatRooms.get(0);
        ChatRoom chatRoom2 = savedChatRooms.get(1);
        assertEquals(expectedChatId, chatRoom1.getChatId());
        assertEquals(senderId, chatRoom1.getSenderId());
        assertEquals(recipientId, chatRoom1.getRecipientId());
        assertEquals(expectedChatId, chatRoom2.getChatId());
        assertEquals(recipientId, chatRoom2.getSenderId());
        assertEquals(senderId, chatRoom2.getRecipientId());
    }

    @Test
    void getChatRoomId_ShouldReturnEmptyOptional_WhenNotExistsAndCreateNewIsFalse() {
        String senderId = "user1";
        String recipientId = "user2";
        when(chatRoomRepository.findBySenderIdAndRecipientId(senderId, recipientId))
                .thenReturn(Optional.empty());
        Optional<String> chatRoomId = chatRoomService.getChatRoomId(senderId, recipientId, false);
        assertTrue(chatRoomId.isEmpty());
        verify(chatRoomRepository, times(1)).findBySenderIdAndRecipientId(senderId, recipientId);
        verify(chatRoomRepository, never()).save(any(ChatRoom.class));
    }

    @Test
    void getUniqueRecipientsForSender_ShouldReturnListOfRecipientIds_WhenRecipientsExist() {
        String senderId = "user1";
        List<String> expectedRecipients = List.of("recipient1", "recipient2");
        when(chatRoomRepository.findUniqueRecipientsBySenderId(senderId))
                .thenReturn(Optional.of(expectedRecipients));
        Optional<List<String>> actualRecipients = chatRoomService.getUniqueRecipientsForSender(senderId);
        assertTrue(actualRecipients.isPresent());
        assertEquals(expectedRecipients.size(), actualRecipients.get().size());
        assertEquals(expectedRecipients, actualRecipients.get());
        verify(chatRoomRepository, times(1)).findUniqueRecipientsBySenderId(senderId);
    }

    @Test
    void getUniqueRecipientsForSender_ShouldReturnEmptyOptional_WhenNoRecipientsExist() {
        String senderId = "user1";
        when(chatRoomRepository.findUniqueRecipientsBySenderId(senderId))
                .thenReturn(Optional.empty());
        Optional<List<String>> actualRecipients = chatRoomService.getUniqueRecipientsForSender(senderId);
        assertTrue(actualRecipients.isEmpty());
        verify(chatRoomRepository, times(1)).findUniqueRecipientsBySenderId(senderId);
    }

    @Test
    void doesChatExist_ShouldReturnTrue_WhenChatRoomExists() {
        String senderId = "user1";
        String recipientId = "user2";
        when(chatRoomRepository.existsBySenderAndRecipient(senderId, recipientId)).thenReturn(true);
        boolean exists = chatRoomService.doesChatExist(senderId, recipientId);
        assertTrue(exists);
        verify(chatRoomRepository, times(1)).existsBySenderAndRecipient(senderId, recipientId);
    }

    @Test
    void doesChatExist_ShouldReturnFalse_WhenChatRoomDoesNotExist() {
        String senderId = "user1";
        String recipientId = "user2";
        when(chatRoomRepository.existsBySenderAndRecipient(senderId, recipientId)).thenReturn(false);
        boolean exists = chatRoomService.doesChatExist(senderId, recipientId);
        assertFalse(exists);
        verify(chatRoomRepository, times(1)).existsBySenderAndRecipient(senderId, recipientId);
    }

    @Test
    void createChatId_ShouldGenerateCorrectChatId() {
        String senderId = "user1";
        String recipientId = "user2";
        String expectedChatId = "user1_user2";
        String chatId = chatRoomService.getChatRoomId(senderId, recipientId, true).get();
        assertEquals(expectedChatId, chatId);
    }
}




