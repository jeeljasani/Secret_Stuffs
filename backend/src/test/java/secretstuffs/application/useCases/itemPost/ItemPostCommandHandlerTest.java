package secretstuffs.application.useCases.itemPost;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import secretstuffs.application.services.ItemPostService;
import secretstuffs.domain.dtos.commands.itemPost.*;
import secretstuffs.domain.dtos.exception.BusinessException;
import secretstuffs.domain.entities.ItemPost;
import secretstuffs.domain.enums.CategoryEnum;
import secretstuffs.domain.enums.ConditionEnum;
import secretstuffs.domain.models.responses.itemPost.CreateItemPostResponseDTO;
import secretstuffs.domain.models.responses.itemPost.FetchItemPostResponseDTO;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemPostCommandHandlerTest {

    @InjectMocks
    private ItemPostCommandHandler itemPostCommandHandler;

    @Mock
    private ItemPostService itemPostService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private static final long USER_ID_TWO = 2L;

    @Test
    void testCreatePost() {
        // Arrange
        String email = "test@example.com";
        String title = "Test Item";
        String address = "123 Test Address";
        String description = "Test Description";
        ConditionEnum condition = ConditionEnum.NEW;
        String itemPostUrl = "https://example.com/item";
        CategoryEnum category = CategoryEnum.ELECTRONICS;

        CreateItemPostCommand command = new CreateItemPostCommand(
                email, title, address, description, condition, itemPostUrl, category
        );

        CreateItemPostResponseDTO.CreateItemPostResponseDTOBuilder expectedResponse = CreateItemPostResponseDTO.builder();
        expectedResponse.email(email);
        expectedResponse.title(title);
        expectedResponse.address(address);
        expectedResponse.description(description);
        expectedResponse.condition(condition);
        expectedResponse.itemPostImageUrl(itemPostUrl);
        expectedResponse.category(category);

        when(itemPostService.createItem(command)).thenReturn(expectedResponse.build());

        // Act
        CreateItemPostResponseDTO actualResponse = itemPostCommandHandler.createPost(command);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(title, actualResponse.getTitle());
        assertEquals(email, actualResponse.getEmail());
        verify(itemPostService, times(1)).createItem(command);
    }

    @Test
    void testDeletePost() {
        // Arrange
        Long postId = 1L;
        DeleteItemPostCommand command = new DeleteItemPostCommand(postId);

        doNothing().when(itemPostService).deleteItemPost(postId);

        // Act
        itemPostCommandHandler.deletePost(command);

        // Assert
        verify(itemPostService, times(1)).deleteItemPost(postId);
    }

    @Test
    void testFetchAllPosts() {
        // Arrange
        // Arrange
        Long postId1 = 1L;
        String email1 = "test1@example.com";
        String title1 = "Item 1";
        String address1 = "123 Test Address 1";
        String description1 = "Description 1";
        ConditionEnum condition1 = ConditionEnum.NEW;
        String itemPostUrl1 = "https://example.com/item1";
        CategoryEnum category1 = CategoryEnum.ELECTRONICS;

        Long postId2 = USER_ID_TWO;
        String email2 = "test2@example.com";
        String title2 = "Item 2";
        String address2 = "456 Test Address 2";
        String description2 = "Description 2";
        ConditionEnum condition2 = ConditionEnum.DAMAGED;
        String itemPostUrl2 = "https://example.com/item2";
        CategoryEnum category2 = CategoryEnum.FURNITURE;

        // Arrange
        FetchItemPostResponseDTO post1 = new FetchItemPostResponseDTO();
        post1.setId(postId1);
        post1.setEmail(email1);
        post1.setTitle(title1);
        post1.setAddress(address1);
        post1.setDescription(description1);
        post1.setCondition(condition1);
        post1.setItemPostImageUrl(itemPostUrl1);
        post1.setCategory(category1);

        FetchItemPostResponseDTO post2 = new FetchItemPostResponseDTO();
        post2.setId(postId2);
        post2.setEmail(email2);
        post2.setTitle(title2);
        post2.setAddress(address2);
        post2.setDescription(description2);
        post2.setCondition(condition2);
        post2.setItemPostImageUrl(itemPostUrl2);
        post2.setCategory(category2);

        List<FetchItemPostResponseDTO> expectedResponse = List.of(post1, post2);

        when(itemPostService.getAllActivePosts()).thenReturn(expectedResponse);

        // Act
        List<FetchItemPostResponseDTO> actualResponse = itemPostCommandHandler.fetchAllPosts();

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.size(), actualResponse.size());
        assertEquals(post1.getTitle(), actualResponse.get(0).getTitle());
        verify(itemPostService, times(1)).getAllActivePosts();
    }

    @Test
    void testFetchPostsByUser() {
        // Arrange
        String email = "test@example.com";
        FetchPostsByUserCommand command = new FetchPostsByUserCommand(email);

        // Arrange
        Long postId1 = 1L;
        String title1 = "Item 1";
        String address1 = "123 Test Address 1";
        String description1 = "Description 1";
        ConditionEnum condition1 = ConditionEnum.NEW;
        String itemPostUrl1 = "https://example.com/item1";
        CategoryEnum category1 = CategoryEnum.ELECTRONICS;

        // Arrange
        FetchItemPostResponseDTO post1 = new FetchItemPostResponseDTO();
        post1.setId(postId1);
        post1.setEmail(email);
        post1.setTitle(title1);
        post1.setAddress(address1);
        post1.setDescription(description1);
        post1.setCondition(condition1);
        post1.setItemPostImageUrl(itemPostUrl1);
        post1.setCategory(category1);

        List<FetchItemPostResponseDTO> expectedResponse = List.of(post1);

        when(itemPostService.getAllPostsByEmail(email)).thenReturn(expectedResponse);

        // Act
        List<FetchItemPostResponseDTO> actualResponse = itemPostCommandHandler.fetchPostsByUser(command);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.size(), actualResponse.size());
        assertEquals(post1.getTitle(), actualResponse.get(0).getTitle());
        verify(itemPostService, times(1)).getAllPostsByEmail(email);
    }

    @Test
    void testUpdatePost() throws BusinessException {
        // Arrange
        String updatedTitle = "Updated Title";
        String updatedDescription = "Updated Description";
        String updatedAddress = "123 Updated Address";
        CategoryEnum category = CategoryEnum.ELECTRONICS;
        ConditionEnum condition = ConditionEnum.NEW;

        // Arrange
        UpdateItemPostCommand command = new UpdateItemPostCommand();
        command.setTitle(updatedTitle);
        command.setDescription(updatedDescription);
        command.setAddress(updatedAddress);
        command.setCategory(category);
        command.setCondition(condition);

        UpdateItemPostCommand expectedUpdatedCommand = new UpdateItemPostCommand();
        expectedUpdatedCommand.setTitle(updatedTitle);
        expectedUpdatedCommand.setDescription(updatedDescription);
        expectedUpdatedCommand.setAddress(updatedAddress);
        expectedUpdatedCommand.setCategory(category);
        expectedUpdatedCommand.setCondition(condition);

        when(itemPostService.updateItemPost(command)).thenReturn(expectedUpdatedCommand);

        // Act
        UpdateItemPostCommand actualUpdatedCommand = itemPostCommandHandler.updatePost(command);

        // Assert
        assertNotNull(actualUpdatedCommand);
        assertEquals(updatedTitle, actualUpdatedCommand.getTitle());
        verify(itemPostService, times(1)).updateItemPost(command);
    }

    @Test
    void testGetItemPost() {
        // Arrange
        Long postId = 1L;
        String title = "Test Title";

        ItemPost expectedPost = new ItemPost();
        expectedPost.setId(postId);
        expectedPost.setTitle(title);

        when(itemPostService.getItemById(postId)).thenReturn(expectedPost);

        // Act
        ItemPost actualPost = itemPostCommandHandler.getItemPost(postId);

        // Assert
        assertNotNull(actualPost);
        assertEquals(postId, actualPost.getId());
        assertEquals(title, actualPost.getTitle());
        verify(itemPostService, times(1)).getItemById(postId);
    }
}