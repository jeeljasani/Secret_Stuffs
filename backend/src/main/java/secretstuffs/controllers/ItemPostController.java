package secretstuffs.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
import secretstuffs.domain.models.responses.ApiResponseDTO;
import secretstuffs.domain.models.responses.itemPost.CreateItemPostResponseDTO;
import secretstuffs.domain.models.responses.itemPost.FetchItemPostResponseDTO;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/items")
public class ItemPostController {

    private final ModelMapper modelMapper;
    private final ItemPostCommandHandler itemPostCommandHandler;

    public ItemPostController(ModelMapper modelMapper, ItemPostCommandHandler itemPostCommandHandler) {
        this.modelMapper = modelMapper;
        this.itemPostCommandHandler = itemPostCommandHandler;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<ItemPost>> getItemById(@PathVariable("id") Long id) {
        ItemPost response = itemPostCommandHandler.getItemPost(id);
        return buildResponse("Item Fetched successfully", HttpStatus.OK, response);
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponseDTO<CreateItemPostResponseDTO>> createItemPost(
            @Valid @RequestBody CreateItemPostRequestDTO createItemPostRequestDTO) {
        CreateItemPostCommand command = modelMapper.map(createItemPostRequestDTO, CreateItemPostCommand.class);
        CreateItemPostResponseDTO responseDTO = itemPostCommandHandler.createPost(command);
        return buildResponse("Post Successfully Added", HttpStatus.CREATED, responseDTO);
    }

    @GetMapping("/user-posts")
    public ResponseEntity<ApiResponseDTO<List<FetchItemPostResponseDTO>>> getAllPostsByEmail(
            @RequestParam String email) {
        List<FetchItemPostResponseDTO> posts = itemPostCommandHandler.fetchPostsByUser(
                new FetchPostsByUserCommand(email));
        String message = posts.isEmpty() ? "No posts found for this user" : "Posts fetched successfully";
        return buildResponse(message, HttpStatus.OK, posts);
    }

    @GetMapping("/all-posts")
    public ResponseEntity<ApiResponseDTO<List<FetchItemPostResponseDTO>>> getAllPosts() {
        List<FetchItemPostResponseDTO> posts = itemPostCommandHandler.fetchAllPosts();
        String message = posts.isEmpty() ? "No posts found" : "Posts fetched successfully";
        return buildResponse(message, HttpStatus.OK, posts);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<String>> deleteItemPost(@PathVariable Long id) {
        itemPostCommandHandler.deletePost(new DeleteItemPostCommand(id));
        return buildResponse("Item post deleted successfully", HttpStatus.OK, null);
    }

    // New Endpoint to get CategoryEnum values
    @GetMapping("/categories")
    public ResponseEntity<ApiResponseDTO<List<String>>> getCategories() {
        List<String> categories = Arrays.stream(CategoryEnum.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        return buildResponse("Categories fetched successfully", HttpStatus.OK, categories);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<UpdateItemPostCommand>> updateItemPost(
            @PathVariable Long id,
            @Valid @RequestBody UpdateItemPostRequestDTO updateItemPostRequestDTO) {
        UpdateItemPostCommand command = modelMapper.map(updateItemPostRequestDTO, UpdateItemPostCommand.class);
        command.setId(id);
        UpdateItemPostCommand updatedCommand = itemPostCommandHandler.updatePost(command);
        return buildResponse("Item post updated successfully", HttpStatus.OK, updatedCommand);
    }

    // New Endpoint to get ConditionEnum values
    @GetMapping("/conditions")
    public ResponseEntity<ApiResponseDTO<List<String>>> getConditions() {
        List<String> conditions = Arrays.stream(ConditionEnum.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        return buildResponse("Conditions fetched successfully", HttpStatus.OK, conditions);
    }

    private <T> ResponseEntity<ApiResponseDTO<T>> buildResponse(
            String message, HttpStatus status, T data) {
        ApiResponseDTO<T> apiResponse = new ApiResponseDTO<>(message, status.value(), data);
        return ResponseEntity.status(status).body(apiResponse);
    }
}