package secretstuffs.application.useCases.itemPost;

import org.springframework.stereotype.Component;
import secretstuffs.application.services.ItemPostService;
import secretstuffs.domain.dtos.commands.itemPost.CreateItemPostCommand;
import secretstuffs.domain.dtos.commands.itemPost.DeleteItemPostCommand;
import secretstuffs.domain.dtos.commands.itemPost.FetchPostsByUserCommand;
import secretstuffs.domain.dtos.commands.itemPost.UpdateItemPostCommand;
import secretstuffs.domain.dtos.exception.BusinessException;
import secretstuffs.domain.entities.ItemPost;
import secretstuffs.domain.models.responses.itemPost.CreateItemPostResponseDTO;
import secretstuffs.domain.models.responses.itemPost.FetchItemPostResponseDTO;

import java.util.List;

@Component
public class ItemPostCommandHandler {

    private final ItemPostService itemPostService;

    public ItemPostCommandHandler(ItemPostService itemPostService) {
        this.itemPostService = itemPostService;
    }

    public CreateItemPostResponseDTO createPost(CreateItemPostCommand command) {
        return itemPostService.createItem(command);
    }

    public void deletePost(DeleteItemPostCommand command) {
        itemPostService.deleteItemPost(command.getId());
    }

    public List<FetchItemPostResponseDTO> fetchAllPosts() {
        return itemPostService.getAllActivePosts();
    }

    public List<FetchItemPostResponseDTO> fetchPostsByUser(FetchPostsByUserCommand command) {
        return itemPostService.getAllPostsByEmail(command.getEmail());
    }

    public UpdateItemPostCommand updatePost(UpdateItemPostCommand command) throws BusinessException {
        return itemPostService.updateItemPost(command);
    }

    public ItemPost getItemPost(Long id) {
        return itemPostService.getItemById(id);
    }
}
