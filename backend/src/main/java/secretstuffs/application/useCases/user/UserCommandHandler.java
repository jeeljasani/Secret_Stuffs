package secretstuffs.application.useCases.user;

import org.springframework.stereotype.Component;
import secretstuffs.application.services.UserService;
import secretstuffs.domain.dtos.commands.user.ChangePasswordCommand;
import secretstuffs.domain.dtos.commands.user.DeleteUserCommand;
import secretstuffs.domain.dtos.commands.user.FetchUserDetailsCommand;
import secretstuffs.domain.dtos.commands.user.UpdateUserDetailsCommand;
import secretstuffs.domain.models.responses.user.*;

import java.util.List;

@Component
public class UserCommandHandler {

    private final UserService userService;

    public UserCommandHandler(UserService userService) {
        this.userService = userService;
    }

    public void changePassword(ChangePasswordCommand command) {
        userService.changePassword(command);
    }

    public void deleteUser(DeleteUserCommand command) {
        userService.deleteUserByEmail(command.getEmail());
    }

    public List<FetchUserProfileResponseDTO> fetchAllUsers() {
        return userService.getAllUsers();
    }

    public FetchUserProfileResponseDTO fetchUserDetails(FetchUserDetailsCommand command) {
        return userService.getUserProfileByEmail(command.getEmail());
    }

    public UpdateUserResponseDTO updateUser(UpdateUserDetailsCommand command) {
        return userService.updateUserByEmail(command);
    }
}
