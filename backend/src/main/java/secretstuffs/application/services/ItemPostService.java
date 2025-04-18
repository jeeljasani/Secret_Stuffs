package secretstuffs.application.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import secretstuffs.domain.dtos.commands.itemPost.CreateItemPostCommand;
import secretstuffs.domain.dtos.commands.itemPost.UpdateItemPostCommand;
import secretstuffs.domain.dtos.exception.BusinessException;
import secretstuffs.domain.models.responses.itemPost.CreateItemPostResponseDTO;
import secretstuffs.domain.models.responses.itemPost.FetchItemPostResponseDTO;
import secretstuffs.infrastructure.repositories.ItemPostRepository;
import secretstuffs.infrastructure.repositories.UserRepository;
import secretstuffs.domain.entities.ItemPost;
import secretstuffs.domain.enums.ItemPostStatusEnum;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class ItemPostService {

    private final ItemPostRepository itemPostRepository;
    private final UserRepository userRepository;

    public ItemPostService(
            ItemPostRepository itemPostRepository,
            UserRepository userRepository
    ) {
        this.itemPostRepository = itemPostRepository;
        this.userRepository = userRepository;
    }

    public ItemPost getItemById(Long id) throws BusinessException {
        if(id < 0) {
            throw new BusinessException("ERR_400", String.format("Invalid item id: %d", id), HttpStatus.BAD_REQUEST);
        }
        Optional<ItemPost> response = this.itemPostRepository.findById(id);
        if(response.isEmpty()) {
            throw new BusinessException("ERR_404", String.format("Item with ID: %d not found", id), HttpStatus.NOT_FOUND);
        }
        return response.get();
    }

    public CreateItemPostResponseDTO createItem(CreateItemPostCommand command) {
        validateUserExists(command.getEmail());
        ItemPost newItemPost = toEntity(command);
        itemPostRepository.save(newItemPost);
        return buildCreateItemPostResponse(newItemPost);
    }

    public List<FetchItemPostResponseDTO> getAllPostsByEmail(String email) {
        List<ItemPost> posts = itemPostRepository.findAllByEmail(email);
        Stream<ItemPost> stream = posts.stream();
        Stream<FetchItemPostResponseDTO> responseStream = stream.map(this::buildFetchItemPostResponse);
        return responseStream.toList();
    }

    public List<FetchItemPostResponseDTO> getAllPosts() {
        List<ItemPost> posts = itemPostRepository.findAll();
        return posts.stream()
                .map(this::buildFetchItemPostResponse)
                .toList();
    }

    public List<FetchItemPostResponseDTO> getAllActivePosts() {
        List<ItemPost> posts = itemPostRepository.findAllByStatus(ItemPostStatusEnum.ACTIVE);
        return posts.stream()
                .map(this::buildFetchItemPostResponse)
                .toList();
    }

    public void deleteItemPost(Long id) {
        validateItemPostExists(id);
        itemPostRepository.deleteById(id);
    }

    private void validateUserExists(String email) {
        if (userRepository.findByEmail(email).isEmpty()) {
            throw new BusinessException(
                    "USER_NOT_FOUND",
                    "User with email " + email + " not found",
                    HttpStatus.NOT_FOUND
            );
        }
    }

    private void validateItemPostExists(Long id) {
        if (!itemPostRepository.existsById(id)) {
            throw new EntityNotFoundException("Item post not found with id: " + id);
        }
    }

    private ItemPost toEntity(CreateItemPostCommand command) {
         ItemPost.ItemPostBuilder itemPostBuilder = ItemPost.builder();
        itemPostBuilder.email(command.getEmail());
        itemPostBuilder.title(command.getTitle());
        itemPostBuilder.description(command.getDescription());
        itemPostBuilder.address(command.getAddress());
        itemPostBuilder.itemPostImageUrl(command.getItemPostUrl());
        itemPostBuilder.condition(command.getCondition());
        itemPostBuilder.category(command.getCategory());
        return itemPostBuilder.build();
    }

    private CreateItemPostResponseDTO buildCreateItemPostResponse(ItemPost post) {
        CreateItemPostResponseDTO.CreateItemPostResponseDTOBuilder builder = CreateItemPostResponseDTO.builder();
        builder.email(post.getEmail());
        builder.title(post.getTitle());
        builder.description(post.getDescription());
        builder.address(post.getAddress());
        builder.itemPostImageUrl(post.getItemPostImageUrl());
        builder.condition(post.getCondition());
        builder.category(post.getCategory());
        return builder.build();
    }

    private FetchItemPostResponseDTO buildFetchItemPostResponse(ItemPost post) {
        FetchItemPostResponseDTO.FetchItemPostResponseDTOBuilder  builder = FetchItemPostResponseDTO.builder();
        builder.id(post.getId());
        builder.email(post.getEmail());
        builder.title(post.getTitle());
        builder.description(post.getDescription());
        builder.address(post.getAddress());
        builder.itemPostImageUrl(post.getItemPostImageUrl());
        builder.condition(post.getCondition());
        builder.category(post.getCategory());
        return builder.build();
    }

    public UpdateItemPostCommand updateItemPost(UpdateItemPostCommand command) throws BusinessException {
        validateItemPostExists(command.getId());
        Optional<ItemPost> optionalExistingItemPost = itemPostRepository.findById(command.getId());
        ItemPost existingItemPost = optionalExistingItemPost.orElseThrow(() -> {
            String message = "Item post not found with id: " + command.getId();
            return new EntityNotFoundException(message);
        });
        existingItemPost.setTitle(command.getTitle());
        existingItemPost.setDescription(command.getDescription());
        existingItemPost.setAddress(command.getAddress());
        existingItemPost.setCondition(command.getCondition());
        existingItemPost.setCategory(command.getCategory());
        itemPostRepository.save(existingItemPost);
        return command;
    }

}
