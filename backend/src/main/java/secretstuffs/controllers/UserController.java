package secretstuffs.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import secretstuffs.application.useCases.user.UserCommandHandler;
import secretstuffs.domain.dtos.commands.user.ChangePasswordCommand;
import secretstuffs.domain.dtos.commands.user.DeleteUserCommand;
import secretstuffs.domain.dtos.commands.user.FetchUserDetailsCommand;
import secretstuffs.domain.dtos.commands.user.UpdateUserDetailsCommand;
import secretstuffs.domain.models.requests.user.ChangePasswordRequestDTO;
import secretstuffs.domain.models.requests.user.UpdateUserRequestDTO;
import secretstuffs.domain.models.responses.ApiResponseDTO;
import secretstuffs.domain.models.responses.user.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final ModelMapper modelMapper;
    private final UserCommandHandler userCommandHandler;

    public UserController(ModelMapper modelMapper, UserCommandHandler userCommandHandler) {
        this.modelMapper = modelMapper;
        this.userCommandHandler = userCommandHandler;
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponseDTO<UpdateUserResponseDTO>> updateUserDetails(
            @RequestParam("email") String email,
            @Valid @RequestBody UpdateUserRequestDTO dto) {
        UpdateUserDetailsCommand command = mapToCommand(dto, email);
        UpdateUserResponseDTO responseDTO = userCommandHandler.updateUser(command);
        return buildResponse("User updated successfully", HttpStatus.OK, responseDTO);
    }

    @GetMapping("/{email}")
    public ResponseEntity<ApiResponseDTO<FetchUserProfileResponseDTO>> getUserProfile(@PathVariable String email) {
        FetchUserDetailsCommand command = new FetchUserDetailsCommand(email);
        FetchUserProfileResponseDTO responseDTO = userCommandHandler.fetchUserDetails(command);
        return buildResponse("User profile fetched successfully", HttpStatus.OK, responseDTO);
    }

    @DeleteMapping("/delete/{email}")
    public ResponseEntity<ApiResponseDTO<String>> deleteUserProfile(@PathVariable String email) {
        userCommandHandler.deleteUser(new DeleteUserCommand(email));
        return buildResponse("User deleted successfully", HttpStatus.OK, null);
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponseDTO<String>> changePassword(
            @RequestParam("email") String email,
            @Valid @RequestBody ChangePasswordRequestDTO dto) {
        ChangePasswordCommand command = new ChangePasswordCommand();
        command.setEmail(email);
        command.setOldPassword(dto.getOldPassword());
        command.setNewPassword(dto.getNewPassword());
        command.setConfirmPassword(dto.getConfirmPassword());

        // Use changePasswordCommandHandler to handle the password change
        userCommandHandler.changePassword(command);
        return buildResponse("Password changed successfully", HttpStatus.OK, null);
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponseDTO<List<FetchUserProfileResponseDTO>>> listAllUsers() {
        List<FetchUserProfileResponseDTO> users = userCommandHandler.fetchAllUsers();
        return buildResponse("List of all users fetched successfully", HttpStatus.OK, users);
    }

    private <T> ResponseEntity<ApiResponseDTO<T>> buildResponse(String message, HttpStatus status, T data) {
        ApiResponseDTO<T> apiResponse = new ApiResponseDTO<>(message, status.value(), data);
        return ResponseEntity.status(status).body(apiResponse);
    }

    private UpdateUserDetailsCommand mapToCommand(UpdateUserRequestDTO dto, String email) {
        UpdateUserDetailsCommand command = modelMapper.map(dto, UpdateUserDetailsCommand.class);
        command.setEmail(email);
        return command;
    }
}
