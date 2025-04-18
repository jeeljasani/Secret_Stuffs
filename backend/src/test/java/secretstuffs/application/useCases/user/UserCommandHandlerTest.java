package secretstuffs.application.useCases.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import secretstuffs.application.services.UserService;
import secretstuffs.domain.dtos.commands.user.*;
import secretstuffs.domain.models.responses.user.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserCommandHandlerTest {

    @InjectMocks
    private UserCommandHandler userCommandHandler;

    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private static final long USER_ID_ONE = 1L;
    private static final long USER_ID_TWO = 2L;
    @Test
    void testChangePassword() {
        ChangePasswordCommand command = new ChangePasswordCommand(
                "test@example.com",  // Email
                "oldPassword",       // Old Password
                "newPassword",       // New Password
                "newPassword"        // Confirm Password
        );

        doNothing().when(userService).changePassword(command);

        userCommandHandler.changePassword(command);

        verify(userService, times(1)).changePassword(command);
    }

    @Test
    void testDeleteUser() {
        DeleteUserCommand command = new DeleteUserCommand("test@example.com");

        doNothing().when(userService).deleteUserByEmail(command.getEmail());

        userCommandHandler.deleteUser(command);

        verify(userService, times(1)).deleteUserByEmail(command.getEmail());
    }

    @Test
    void testFetchAllUsers() {
        // Arrange
        String user1Email = "test1@example.com";
        boolean user1Active = true;
        String user1FirstName = "John";
        String user1LastName = "Doe";

        // Arrange
        FetchUserProfileResponseDTO user1 = new FetchUserProfileResponseDTO();
        user1.setId(USER_ID_ONE);
        user1.setEmail(user1Email);
        user1.setActive(user1Active);
        user1.setFirstName(user1FirstName);
        user1.setLastName(user1LastName);

        String user2Email = "test2@example.com";
        boolean user2Active = false;
        String user2FirstName = "Jane";
        String user2LastName = "Doe";

        // Arrange
        FetchUserProfileResponseDTO user2 = new FetchUserProfileResponseDTO();
        user2.setId(USER_ID_TWO);
        user2.setEmail(user2Email);
        user2.setActive(user2Active);
        user2.setFirstName(user2FirstName);
        user2.setLastName(user2LastName);

        List<FetchUserProfileResponseDTO> users = Arrays.asList(user1, user2);
        when(userService.getAllUsers()).thenReturn(users);

        // Act
        List<FetchUserProfileResponseDTO> result = userCommandHandler.fetchAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(user1Email, result.get(0).getEmail());
        assertEquals(user1FirstName, result.get(0).getFirstName());
        assertEquals(user2Email, result.get(1).getEmail());
        assertEquals(user2FirstName, result.get(1).getFirstName());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void testFetchUserDetails() {
        // Arrange
        String userEmail = "test@example.com";
        boolean userActive = true;
        String userFirstName = "John";
        String userLastName = "Doe";

        FetchUserDetailsCommand command = new FetchUserDetailsCommand(userEmail);

        // Arrange
        FetchUserProfileResponseDTO user = new FetchUserProfileResponseDTO();
        user.setId(USER_ID_ONE);
        user.setEmail(userEmail);
        user.setActive(userActive);
        user.setFirstName(userFirstName);
        user.setLastName(userLastName);

        when(userService.getUserProfileByEmail(command.getEmail())).thenReturn(user);

        // Act
        FetchUserProfileResponseDTO result = userCommandHandler.fetchUserDetails(command);

        // Assert
        assertNotNull(result);
        assertEquals(USER_ID_ONE, result.getId());
        assertEquals(userEmail, result.getEmail());
        assertEquals(userFirstName, result.getFirstName());
        assertEquals(userLastName, result.getLastName());
        assertTrue(result.isActive());
        verify(userService, times(1)).getUserProfileByEmail(command.getEmail());
    }

    @Test
    void testUpdateUser() {
        // Arrange
        String userEmail = "test@example.com";
        String newFirstName = "NewFirstName";
        String newLastName = "NewLastName";
        String newProfileImage = "https://example.com/new-profile.jpg";

        UpdateUserDetailsCommand command = new UpdateUserDetailsCommand(
                userEmail,
                newFirstName,
                newLastName,
                newProfileImage
        );

        UpdateUserResponseDTO response = new UpdateUserResponseDTO(
                userEmail,
                newProfileImage,
                newFirstName,
                newLastName
        );

        when(userService.updateUserByEmail(command)).thenReturn(response);

        // Act
        UpdateUserResponseDTO result = userCommandHandler.updateUser(command);

        // Assert
        assertNotNull(result);
        assertEquals(newFirstName, result.getFirstName());
        assertEquals(newLastName, result.getLastName());
        assertEquals(userEmail, result.getEmail());
        assertEquals(newProfileImage, result.getProfileImageURL());
        verify(userService, times(1)).updateUserByEmail(command);
    }
}