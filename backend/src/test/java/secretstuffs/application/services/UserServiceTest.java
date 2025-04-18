package secretstuffs.application.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import secretstuffs.application.helpers.AuthHelper;
import secretstuffs.domain.dtos.commands.user.ChangePasswordCommand;
import secretstuffs.domain.dtos.commands.user.UpdateUserDetailsCommand;
import secretstuffs.domain.entities.User;
import secretstuffs.domain.models.responses.user.UpdateUserResponseDTO;
import secretstuffs.domain.models.responses.user.FetchUserProfileResponseDTO;
import secretstuffs.domain.dtos.exception.BusinessException;
import secretstuffs.infrastructure.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class UserServiceTest {

    private static final int EXPECTED_USER_COUNT = 2; // Added constant for expected user count

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthHelper authHelper;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void activateUser_ActivateUser_WhenUserIsInactive() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setActive(false);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        userService.activateUser("test@example.com");

        assertTrue(user.isActive());
        verify(userRepository).save(user);
    }

    @Test
    void activateUser_ThrowException_WhenUserIsAlreadyActive() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setActive(true);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                userService.activateUser("test@example.com")
        );

        assertEquals("User is already active", exception.getErrorCode());
    }

    @Test
    void isUserRegistered_ReturnTrue_WhenUserExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new User()));

        assertTrue(userService.isUserRegistered("test@example.com"));
    }

    @Test
    void isUserActive_ReturnTrue_WhenUserIsActive() {
        User user = new User();
        user.setActive(true);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        assertTrue(userService.isUserActive("test@example.com"));
    }

    @Test
    void isUserActive_ThrowException_WhenUserNotFound() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.isUserActive("nonexistent@example.com");
        });

        assertEquals("User with email nonexistent@example.com not found", exception.getErrorCode());
    }

    @Test
    void getUserProfileByEmail_ReturnUserProfile_WhenUserExists() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setProfileImageURL("image-url");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        var result = userService.getUserProfileByEmail("test@example.com");

        assertEquals("test@example.com", result.getEmail());
        assertEquals("image-url", result.getProfileImageURL());
    }

    @Test
    void deleteUserByEmail_ShouldDeleteUser_WhenUserExists() {
        User user = new User();
        user.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> userService.deleteUserByEmail("test@example.com"));

        verify(userRepository).delete(user);
    }

    @Test
    void findUserByEmail_ShouldReturnUser_WhenUserExists() {
        String email = "user@example.com";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        User result = userService.findUserByEmail(email);

        assertEquals(user, result);
    }

    @Test
    void findUserByEmail_ShouldThrowException_WhenUserNotFound() {
        String email = "nonexistent@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                userService.findUserByEmail(email));

        assertEquals("User with email " + email + " not found", exception.getErrorCode());
        assertEquals("USER_NOT_FOUND", exception.getErrorMessage());
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        User user1 = new User();
        user1.setEmail("user1@example.com");
        User user2 = new User();
        user2.setEmail("user2@example.com");

        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);

        when(userRepository.findAll()).thenReturn(users);

        List<FetchUserProfileResponseDTO> result = userService.getAllUsers();

        assertEquals(EXPECTED_USER_COUNT, result.size()); // Use constant for user count
        assertEquals("user1@example.com", result.get(0).getEmail());
        assertEquals("user2@example.com", result.get(1).getEmail());
    }

    @Test
    void updateUserByEmail_ShouldUpdateUserDetails() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("OldFirst");
        user.setLastName("OldLast");

        UpdateUserDetailsCommand command = new UpdateUserDetailsCommand();
        command.setEmail("test@example.com");
        command.setFirstName("NewFirst");
        command.setLastName("NewLast");
        command.setProfileImageURL("new-image-url");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UpdateUserResponseDTO response = userService.updateUserByEmail(command);

        assertEquals("NewFirst", response.getFirstName());
        assertEquals("NewLast", response.getLastName());
        assertEquals("new-image-url", response.getProfileImageURL());
    }

    @Test
    void changePassword_ShouldChangePassword_WhenValid() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("oldEncryptedPassword");

        ChangePasswordCommand command = new ChangePasswordCommand();
        command.setEmail("test@example.com");
        command.setOldPassword("oldPassword");
        command.setNewPassword("newPassword");
        command.setConfirmPassword("newPassword");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(authHelper.passwordMatches("oldPassword", "oldEncryptedPassword")).thenReturn(true);
        when(authHelper.encryptPassword("newPassword")).thenReturn("newEncryptedPassword");

        userService.changePassword(command);

        assertEquals("newEncryptedPassword", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void changePassword_ShouldThrowException_WhenOldPasswordIsInvalid() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("oldEncryptedPassword");

        ChangePasswordCommand command = new ChangePasswordCommand();
        command.setEmail("test@example.com");
        command.setOldPassword("wrongOldPassword");
        command.setNewPassword("newPassword");
        command.setConfirmPassword("newPassword");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(authHelper.passwordMatches("wrongOldPassword", "oldEncryptedPassword")).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                userService.changePassword(command));

        assertEquals("Old password is incorrect", exception.getErrorCode());
    }

    @Test
    void changePassword_ShouldThrowException_WhenNewPasswordsDoNotMatch() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("oldEncryptedPassword");

        ChangePasswordCommand command = new ChangePasswordCommand();
        command.setEmail("test@example.com");
        command.setOldPassword("oldPassword");
        command.setNewPassword("newPassword1");
        command.setConfirmPassword("newPassword2");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(authHelper.passwordMatches("oldPassword", "oldEncryptedPassword")).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                userService.changePassword(command));

        assertEquals("New password and confirm password do not match", exception.getErrorCode());
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertEquals("test@example.com", result.getEmail());
        assertEquals(1L, result.getId());
    }

    @Test
    void getUserById_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                userService.getUserById(1L));

        assertEquals("User with id 1 not found", exception.getErrorCode());
    }
}