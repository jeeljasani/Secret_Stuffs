package secretstuffs.application.services;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import secretstuffs.domain.dtos.commands.itemPost.CreateItemPostCommand;
import secretstuffs.domain.dtos.commands.itemPost.UpdateItemPostCommand;
import secretstuffs.domain.dtos.exception.BusinessException;
import secretstuffs.domain.models.responses.itemPost.FetchItemPostResponseDTO;
import secretstuffs.domain.entities.ItemPost;
import secretstuffs.domain.entities.User;
import secretstuffs.domain.enums.ConditionEnum;
import secretstuffs.domain.enums.CategoryEnum;
import secretstuffs.domain.enums.ItemPostStatusEnum;

import jakarta.persistence.EntityNotFoundException;
import secretstuffs.infrastructure.repositories.ItemPostRepository;
import secretstuffs.infrastructure.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class ItemPostServiceTest {

    @Mock
    private ItemPostRepository itemPostRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemPostService itemPostService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createItem_ShouldCreateItem_WhenUserExists() {
        String email = "user@example.com";
        String title = "Test Title";
        String address = "Test Address";
        String description = "Test Description";
        ConditionEnum condition = ConditionEnum.NEW;
        String itemPostUrl = "test-url";
        CategoryEnum category = CategoryEnum.FURNITURE;

        CreateItemPostCommand command = new CreateItemPostCommand(
                email, title, address, description, condition, itemPostUrl, category
        );

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new User()));
        when(itemPostRepository.save(any(ItemPost.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = itemPostService.createItem(command);

        assertNotNull(response);
        assertEquals(title, response.getTitle());
        assertEquals(address, response.getAddress());
    }

    @Test
    void createItem_ShouldThrowException_WhenUserDoesNotExist() {
        String email = "nonexistent@example.com";
        CreateItemPostCommand command = new CreateItemPostCommand(
                email, "Test Title", "Test Address", "Test Description",
                ConditionEnum.NEW, "test-url", CategoryEnum.FURNITURE
        );

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                itemPostService.createItem(command)
        );

        assertEquals("USER_NOT_FOUND", exception.getErrorCode()); // Validate the error code
        assertEquals("User with email " + email + " not found", exception.getErrorMessage()); // Validate the error message
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode()); // Validate the status code
    }

    @Test
    void getItemById_ShouldReturnItem_WhenValidId() {
        Long validId = 1L;
        ItemPost mockPost = new ItemPost();
        mockPost.setId(validId);

        when(itemPostRepository.findById(validId)).thenReturn(Optional.of(mockPost));

        ItemPost response = itemPostService.getItemById(validId);

        assertNotNull(response);
        assertEquals(validId, response.getId());
        verify(itemPostRepository, times(1)).findById(validId);
    }

    @Test
    void getItemById_ShouldThrowException_WhenItemNotFound() {
        Long nonExistentId = 1L;

        when(itemPostRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                itemPostService.getItemById(nonExistentId)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("ERR_404", exception.getErrorCode());
        assertEquals(String.format("Item with ID: %d not found", nonExistentId), exception.getErrorMessage());
    }

    @Test
    void getItemById_ShouldThrowBadRequestException_WhenInvalidId() {
        Long invalidId = -1L;

        BusinessException exception = assertThrows(BusinessException.class, () ->
                itemPostService.getItemById(invalidId)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("ERR_400", exception.getErrorCode());
        assertEquals(String.format("Invalid item id: %d", invalidId), exception.getErrorMessage());
    }

    @Test
    void deleteItemPost_ShouldDelete_WhenValidIdExists() {
        Long validId = 1L;
        when(itemPostRepository.existsById(validId)).thenReturn(true);

        itemPostService.deleteItemPost(validId);

        verify(itemPostRepository, times(1)).deleteById(validId);
    }

    @Test
    void deleteItemPost_ShouldThrowException_WhenIdNotFound() {
        Long invalidId = 1L;
        when(itemPostRepository.existsById(invalidId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> itemPostService.deleteItemPost(invalidId));
        verify(itemPostRepository, never()).deleteById(any());
    }

    @Test
    void getAllPostsByEmail_ShouldReturnPosts_WhenEmailExists() {
        String email = "user@example.com";
        ItemPost post1 = new ItemPost();
        post1.setEmail(email);
        ItemPost post2 = new ItemPost();
        post2.setEmail(email);

        List<ItemPost> posts = List.of(post1, post2);

        when(itemPostRepository.findAllByEmail(email)).thenReturn(posts);

        List<FetchItemPostResponseDTO> response = itemPostService.getAllPostsByEmail(email);

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(email, response.get(0).getEmail());
    }

    @Test
    void getAllPosts_ShouldReturnAllPosts() {
        ItemPost post1 = new ItemPost();
        ItemPost post2 = new ItemPost();

        when(itemPostRepository.findAll()).thenReturn(List.of(post1, post2));

        List<FetchItemPostResponseDTO> response = itemPostService.getAllPosts();

        assertNotNull(response);
        assertEquals(2, response.size());
    }

    @Test
    void getAllActivePosts_ShouldReturnOnlyActivePosts() {
        List<ItemPost> mockPosts = new ArrayList<>();
        ItemPost activePost = new ItemPost();
        activePost.setStatus(ItemPostStatusEnum.ACTIVE);
        mockPosts.add(activePost);

        when(itemPostRepository.findAllByStatus(ItemPostStatusEnum.ACTIVE)).thenReturn(mockPosts);

        var response = itemPostService.getAllActivePosts();

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void updateItemPost_ShouldUpdateItem_WhenValidId() {
        Long postId = 1L;
        String updatedTitle = "Updated Title";
        String updatedDescription = "Updated Description";
        String updatedAddress = "Updated Address";
        CategoryEnum category = CategoryEnum.FURNITURE;
        ConditionEnum condition = ConditionEnum.DAMAGED;

        UpdateItemPostCommand command = new UpdateItemPostCommand();
        command.setPostId(postId);
        command.setTitle(updatedTitle);
        command.setDescription(updatedDescription);
        command.setAddress(updatedAddress);
        command.setCategory(category);
        command.setCondition(condition);

        ItemPost existingPost = new ItemPost();
        existingPost.setId(postId);

        when(itemPostRepository.existsById(postId)).thenReturn(true);
        when(itemPostRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        when(itemPostRepository.save(any(ItemPost.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateItemPostCommand response = itemPostService.updateItemPost(command);

        assertNotNull(response);
        assertEquals(updatedTitle, response.getTitle());
        assertEquals(updatedDescription, response.getDescription());
        assertEquals(updatedAddress, response.getAddress());

        verify(itemPostRepository, times(1)).existsById(postId);
        verify(itemPostRepository, times(1)).findById(postId);
        verify(itemPostRepository, times(1)).save(existingPost);
    }

    @Test
    void updateItemPost_ShouldThrowException_WhenIdNotFound() {
        Long invalidId = 1L;
        UpdateItemPostCommand command = new UpdateItemPostCommand();
        command.setPostId(invalidId);

        when(itemPostRepository.existsById(invalidId)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                itemPostService.updateItemPost(command));

        assertEquals("Item post not found with id: " + invalidId, exception.getMessage());
    }

    @Test
    void getAllPostsByEmail_ShouldReturnEmptyList_WhenNoPostsExist() {
        String email = "user@example.com";

        when(itemPostRepository.findAllByEmail(email)).thenReturn(new ArrayList<>());

        List<FetchItemPostResponseDTO> response = itemPostService.getAllPostsByEmail(email);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void getAllPosts_ShouldReturnEmptyList_WhenNoPostsExist() {
        when(itemPostRepository.findAll()).thenReturn(new ArrayList<>());

        List<FetchItemPostResponseDTO> response = itemPostService.getAllPosts();

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void getAllActivePosts_ShouldReturnEmptyList_WhenNoActivePostsExist() {
        when(itemPostRepository.findAllByStatus(ItemPostStatusEnum.ACTIVE)).thenReturn(new ArrayList<>());

        List<FetchItemPostResponseDTO> response = itemPostService.getAllActivePosts();

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }
}