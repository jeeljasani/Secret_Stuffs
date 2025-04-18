package secretstuffs.controllers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import secretstuffs.application.useCases.itemPost.ItemPostCommandHandler;
import secretstuffs.domain.dtos.commands.itemPost.CreateItemPostCommand;
import secretstuffs.domain.dtos.commands.itemPost.DeleteItemPostCommand;
import secretstuffs.domain.dtos.commands.itemPost.FetchPostsByUserCommand;
import secretstuffs.domain.dtos.commands.itemPost.UpdateItemPostCommand;
import secretstuffs.domain.entities.ItemPost;
import secretstuffs.domain.enums.CategoryEnum;
import secretstuffs.domain.enums.ConditionEnum;
import secretstuffs.domain.models.requests.itempost.CreateItemPostRequestDTO;
import secretstuffs.domain.models.requests.itempost.UpdateItemPostRequestDTO;
import secretstuffs.domain.models.responses.itemPost.CreateItemPostResponseDTO;
import secretstuffs.domain.models.responses.itemPost.FetchItemPostResponseDTO;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

class ItemPostControllerTest {

    @Mock
    private ItemPostCommandHandler itemPostCommandHandler;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ItemPostController itemPostController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(itemPostController).build();
    }

    @Test
    void getItemById_ShouldReturnItemPost_WhenIdIsValid() throws Exception {
        // Arrange
        Long id = 1L;
        ItemPost itemPost = new ItemPost();
        itemPost.setId(id);

        when(itemPostCommandHandler.getItemPost(id)).thenReturn(itemPost);

        // Act
        MockHttpServletRequestBuilder requestBuilder = get("/api/items/{id}", id);
        ResultActions response = mockMvc.perform(requestBuilder);

        // Assert
        response.andExpect(status().isOk());
        response.andExpect(jsonPath("$.data.id").value(id));
        response.andExpect(jsonPath("$.message").value("Item Fetched successfully"));
        String responseContent = response.andReturn().getResponse().getContentAsString();
        assertNotNull(responseContent, "Response should not be null");
    }

    @Test
    void createItemPost_ShouldReturnCreatedPost_WhenRequestIsValid() throws Exception {
        // Arrange
        CreateItemPostRequestDTO requestDTO = new CreateItemPostRequestDTO();
        requestDTO.setTitle("Test Item");
        requestDTO.setDescription("Test Description");
        requestDTO.setEmail("test@example.com");
        requestDTO.setAddress("123 Test Street");

        CreateItemPostCommand command = new CreateItemPostCommand();
        CreateItemPostResponseDTO responseDTO = new CreateItemPostResponseDTO();

        when(modelMapper.map(any(CreateItemPostRequestDTO.class), eq(CreateItemPostCommand.class))).thenReturn(command);
        when(itemPostCommandHandler.createPost(command)).thenReturn(responseDTO);

        // Act
        MockHttpServletRequestBuilder requestBuilder = post("/api/items/create");
        requestBuilder.contentType(MediaType.APPLICATION_JSON);
        requestBuilder.content("""
                        {
                            "title": "Test Item",
                            "description": "Test Description",
                            "email": "test@example.com",
                            "address": "123 Test Street"
                        }
                        """);
        ResultActions response = mockMvc.perform(requestBuilder);

        // Assert
        response.andExpect(status().isCreated());
        response.andExpect(jsonPath("$.message").value("Post Successfully Added"));
        String responseContent = response.andReturn().getResponse().getContentAsString();
        assertNotNull(responseContent, "Response should not be null");
    }

    @Test
    void getAllPostsByEmail_ShouldReturnPosts_WhenEmailIsValid() throws Exception {
        // Arrange
        String email = "test@example.com";
        FetchItemPostResponseDTO post = new FetchItemPostResponseDTO();
        post.setTitle("Test Post");

        List<FetchItemPostResponseDTO> posts = List.of(post);

        when(itemPostCommandHandler.fetchPostsByUser(new FetchPostsByUserCommand(email))).thenReturn(posts);

        // Act
        MockHttpServletRequestBuilder requestBuilder = get("/api/items/user-posts").param("email", email);
        ResultActions response = mockMvc.perform(requestBuilder);

        // Assert
        response.andExpect(status().isOk());
        response.andExpect(jsonPath("$.data[0].title").value("Test Post"));
        response.andExpect(jsonPath("$.message").value("Posts fetched successfully"));
        String responseContent = response.andReturn().getResponse().getContentAsString();
        assertNotNull(responseContent, "Response should not be null");
    }

    @Test
    void getAllPosts_ShouldReturnPosts_WhenPostsExist() throws Exception {
        // Arrange
        FetchItemPostResponseDTO post = new FetchItemPostResponseDTO();
        post.setTitle("Test Post");

        List<FetchItemPostResponseDTO> posts = List.of(post);

        when(itemPostCommandHandler.fetchAllPosts()).thenReturn(posts);

        // Act
        MockHttpServletRequestBuilder requestBuilder = get("/api/items/all-posts");
        ResultActions response = mockMvc.perform(requestBuilder);

        // Assert
        response.andExpect(status().isOk());
        response.andExpect(jsonPath("$.data[0].title").value("Test Post"));
        response.andExpect(jsonPath("$.message").value("Posts fetched successfully"));
        String responseContent = response.andReturn().getResponse().getContentAsString();
        assertNotNull(responseContent, "Response should not be null");
    }

    @Test
    void deleteItemPost_ShouldReturnSuccessMessage_WhenIdIsValid() throws Exception {
        // Arrange
        Long id = 1L;

        doNothing().when(itemPostCommandHandler).deletePost(new DeleteItemPostCommand(id));

        // Act
        MockHttpServletRequestBuilder requestBuilder = delete("/api/items/{id}", id);
        ResultActions response = mockMvc.perform(requestBuilder);

        // Assert
        response.andExpect(status().isOk());
        response.andExpect(jsonPath("$.message").value("Item post deleted successfully"));
        String responseContent = response.andReturn().getResponse().getContentAsString();
        assertNotNull(responseContent, "Response should not be null");
    }

    @Test
    void getCategories_ShouldReturnAllCategories() throws Exception {
        // Arrange
        List<String> categories = Arrays.stream(CategoryEnum.values())
                .map(Enum::name)
                .toList();

        // Act
        MockHttpServletRequestBuilder requestBuilder = get("/api/items/categories");
        ResultActions response = mockMvc.perform(requestBuilder);

        // Assert
        response.andExpect(status().isOk());
        response.andExpect(jsonPath("$.data").isArray());
        response.andExpect(jsonPath("$.data[0]").value(categories.get(0)));
        response.andExpect(jsonPath("$.message").value("Categories fetched successfully"));
        String responseContent = response.andReturn().getResponse().getContentAsString();
        assertNotNull(responseContent, "Response should not be null");
    }

    @Test
    void getConditions_ShouldReturnAllConditions() throws Exception {
        // Arrange
        List<String> conditions = Arrays.stream(ConditionEnum.values())
                .map(Enum::name)
                .toList();

        // Act
        MockHttpServletRequestBuilder requestBuilder = get("/api/items/conditions");
        ResultActions response = mockMvc.perform(requestBuilder);

        // Assert
        response.andExpect(status().isOk());
        response.andExpect(jsonPath("$.data").isArray());
        response.andExpect(jsonPath("$.data[0]").value(conditions.get(0)));
        response.andExpect(jsonPath("$.message").value("Conditions fetched successfully"));
        String responseContent = response.andReturn().getResponse().getContentAsString();
        assertNotNull(responseContent, "Response should not be null");
    }

    @Test
    void updateItemPost_ShouldReturnUpdatedPost_WhenRequestIsValid() throws Exception {
        // Arrange
        Long id = 1L;
        UpdateItemPostRequestDTO requestDTO = new UpdateItemPostRequestDTO();
        requestDTO.setTitle("Updated Title");
        requestDTO.setDescription("Updated Description");

        UpdateItemPostCommand command = new UpdateItemPostCommand();
        command.setId(id);
        command.setTitle("Updated Title");

        when(modelMapper.map(any(UpdateItemPostRequestDTO.class), eq(UpdateItemPostCommand.class)))
                .thenReturn(command);
        when(itemPostCommandHandler.updatePost(command)).thenReturn(command);

        // Act
        MockHttpServletRequestBuilder requestBuilder = put("/api/items/{id}", id);
        requestBuilder.contentType(MediaType.APPLICATION_JSON);
        requestBuilder.content("""
                        {
                            "title": "Updated Title",
                            "description": "Updated Description"
                        }
                        """);
        ResultActions response = mockMvc.perform(requestBuilder);

        // Assert
        response.andExpect(status().isOk());
        response.andExpect(jsonPath("$.message").value("Item post updated successfully"));
        response.andExpect(jsonPath("$.data.title").value("Updated Title"));
        String responseContent = response.andReturn().getResponse().getContentAsString();
        assertNotNull(responseContent, "Response should not be null");
    }
}