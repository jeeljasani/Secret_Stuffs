package secretstuffs.controllers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import secretstuffs.application.useCases.user.UserCommandHandler;
import secretstuffs.domain.dtos.commands.user.ChangePasswordCommand;
import secretstuffs.domain.dtos.commands.user.DeleteUserCommand;
import secretstuffs.domain.dtos.commands.user.FetchUserDetailsCommand;
import secretstuffs.domain.dtos.commands.user.UpdateUserDetailsCommand;
import secretstuffs.domain.models.requests.user.UpdateUserRequestDTO;
import secretstuffs.domain.models.responses.user.FetchUserProfileResponseDTO;
import secretstuffs.domain.models.responses.user.UpdateUserResponseDTO;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

class UserControllerTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserCommandHandler userCommandHandler;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    private static final long USER_ID_TWO = 2L;
    @Test
    void updateUserDetails_ShouldReturnSuccessResponse_WhenRequestIsValid() throws Exception {
        // Arrange
        String email = "user@example.com";
        String requestBody = "{\"firstName\":\"John\",\"lastName\":\"Doe\"}";
        String expectedMessage = "User updated successfully";

        String firstName = "John";
        String lastName = "Doe";

        UpdateUserRequestDTO requestDTO = new UpdateUserRequestDTO();
        requestDTO.setFirstName(firstName);
        requestDTO.setLastName(lastName);

        UpdateUserDetailsCommand command = new UpdateUserDetailsCommand();
        command.setEmail(email);

        UpdateUserResponseDTO responseDTO = new UpdateUserResponseDTO();
        responseDTO.setFirstName(firstName);
        responseDTO.setLastName(lastName);

        when(modelMapper.map(any(UpdateUserRequestDTO.class), eq(UpdateUserDetailsCommand.class))).thenReturn(command);
        when(userCommandHandler.updateUser(command)).thenReturn(responseDTO);

        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = put("/api/users/update");
        mockHttpServletRequestBuilder.param("email", email);
        mockHttpServletRequestBuilder.contentType(MediaType.APPLICATION_JSON);
        mockHttpServletRequestBuilder.content(requestBody);
        // Act & Assert
        ResultActions resultActions = mockMvc.perform(mockHttpServletRequestBuilder);
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.firstName").value(firstName));
        resultActions.andExpect(jsonPath("$.data.lastName").value(lastName));
        resultActions.andExpect(jsonPath("$.message").value(expectedMessage));

        String response = resultActions.andReturn().getResponse().getContentAsString();
        assertNotNull(response, "Response should not be null");
    }

    @Test
    void getUserProfile_ShouldReturnUserProfile_WhenEmailIsValid() throws Exception {
        // Arrange
        String email = "user@example.com";
        String expectedMessage = "User profile fetched successfully";

        Long userId = 1L;
        String firstName = "John";
        String lastName = "Doe";
        String profileImageURL = "https://example.com/profile.jpg";
        boolean active = true;

        FetchUserProfileResponseDTO responseDTO = new FetchUserProfileResponseDTO();
        responseDTO.setId(userId);
        responseDTO.setEmail(email);
        responseDTO.setProfileImageURL(profileImageURL);
        responseDTO.setActive(active);
        responseDTO.setFirstName(firstName);
        responseDTO.setLastName(lastName);

        when(userCommandHandler.fetchUserDetails(any(FetchUserDetailsCommand.class))).thenReturn(responseDTO);

        // Act & Assert
        ResultActions resultActions = mockMvc.perform(get("/api/users/{email}", email));
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.email").value(email));
        resultActions.andExpect(jsonPath("$.data.firstName").value(firstName));
        resultActions.andExpect(jsonPath("$.data.lastName").value(lastName));
        resultActions.andExpect(jsonPath("$.message").value(expectedMessage));

        String response = resultActions.andReturn().getResponse().getContentAsString();
        assertNotNull(response, "Response should not be null");
    }

    @Test
    void deleteUserProfile_ShouldReturnSuccessMessage_WhenEmailIsValid() throws Exception {
        // Arrange
        String email = "user@example.com";
        String expectedMessage = "User deleted successfully";

        doNothing().when(userCommandHandler).deleteUser(new DeleteUserCommand(email));

        // Act & Assert
        ResultActions resultActions = mockMvc.perform(delete("/api/users/delete/{email}", email));
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.message").value(expectedMessage));

        String response = resultActions.andReturn().getResponse().getContentAsString();
        assertNotNull(response, "Response should not be null");
    }

    @Test
    void changePassword_ShouldReturnSuccessResponse_WhenRequestIsValid() throws Exception {
        // Arrange
        String email = "user@example.com";
        String oldPassword = "oldPassword";
        String newPassword = "newPassword123";
        String confirmPassword = "newPassword123";

        // Arrange
        String jsonTemplate = """
            {
                "oldPassword": "%s",
                "newPassword": "%s",
                "confirmPassword": "%s"
            }
        """;

        String requestBody = String.format(
                jsonTemplate,
                oldPassword,
                newPassword,
                confirmPassword
        );
        String expectedMessage = "Password changed successfully";

        doNothing().when(userCommandHandler).changePassword(any(ChangePasswordCommand.class));

        // Act & Assert
        MockHttpServletRequestBuilder request = put("/api/users/change-password");
        request.param("email", email);
        request.contentType(MediaType.APPLICATION_JSON);
        request.content(requestBody);
        ResultActions resultActions = mockMvc.perform(request);
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.message").value(expectedMessage));

        String response = resultActions.andReturn().getResponse().getContentAsString();
        assertNotNull(response, "Response should not be null");
    }

    @Test
    void listAllUsers_ShouldReturnListOfUsers_WhenUsersExist() throws Exception {
        // Arrange
        Long user1Id = 1L;
        String user1Email = "user1@example.com";
        String user1ProfileImage = "https://example.com/profile1.jpg";
        boolean user1Active = true;
        String user1FirstName = "John";
        String user1LastName = "Doe";

        Long user2Id = USER_ID_TWO;
        String user2Email = "user2@example.com";
        String user2ProfileImage = "https://example.com/profile2.jpg";
        boolean user2Active = false;
        String user2FirstName = "Jane";
        String user2LastName = "Smith";

        FetchUserProfileResponseDTO user1 = new FetchUserProfileResponseDTO();
        user1.setId(user1Id);
        user1.setEmail(user1Email);
        user1.setProfileImageURL(user1ProfileImage);
        user1.setActive(user1Active);
        user1.setFirstName(user1FirstName);
        user1.setLastName(user1LastName);

        FetchUserProfileResponseDTO user2 = new FetchUserProfileResponseDTO();
        user2.setId(user2Id);
        user2.setEmail(user2Email);
        user2.setProfileImageURL(user2ProfileImage);
        user2.setActive(user2Active);
        user2.setFirstName(user2FirstName);
        user2.setLastName(user2LastName);

        List<FetchUserProfileResponseDTO> users = List.of(user1, user2);

        when(userCommandHandler.fetchAllUsers()).thenReturn(users);

        String url = "/api/users/";
        String successMessage = "List of all users fetched successfully";

        // Act & Assert
        ResultActions resultActions = mockMvc.perform(get(url));
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data").isArray());
        resultActions.andExpect(jsonPath("$.data[0].email").value(user1Email));
        resultActions.andExpect(jsonPath("$.data[1].email").value(user2Email));
        resultActions.andExpect(jsonPath("$.message").value(successMessage));
    }
}