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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import secretstuffs.application.services.AuthService;
import secretstuffs.application.useCases.auth.AuthCommandHandler;
import secretstuffs.domain.dtos.commands.auth.LoginUserCommand;
import secretstuffs.domain.dtos.commands.auth.RegisterUserCommand;
import secretstuffs.domain.models.requests.auth.LoginUserRequestDTO;
import secretstuffs.domain.models.requests.auth.RegisterUserRequestDTO;
import secretstuffs.domain.models.responses.auth.LoginUserResponseDTO;
import secretstuffs.domain.models.responses.auth.RegisterUserResponseDTO;
import secretstuffs.domain.models.responses.ApiResponseDTO;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

class AuthControllerTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private AuthService authService;

    @Mock
    private AuthCommandHandler authCommandHandler;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void loginUser_ShouldReturnSuccessResponse_WhenCredentialsAreValid() throws Exception {
        LoginUserRequestDTO requestDTO = new LoginUserRequestDTO();
        requestDTO.setEmail("test@example.com");
        requestDTO.setPassword("password123");

        LoginUserCommand command = new LoginUserCommand();
        command.setEmail("test@example.com");
        command.setPassword("password123");

        LoginUserResponseDTO responseDTO = new LoginUserResponseDTO();
        responseDTO.setToken("jwt-token");

        when(modelMapper.map(any(LoginUserRequestDTO.class), eq(LoginUserCommand.class))).thenReturn(command);
        when(authCommandHandler.login(command)).thenReturn(responseDTO);

        MockHttpServletRequestBuilder m = post("/api/auth/login");
        m.contentType(MediaType.APPLICATION_JSON);
        m.content("""
                        {
                            "email": "test@example.com",
                            "password": "password123"
                        }
                        """);
        ResultActions response = mockMvc.perform(m);

        response.andExpect(status().isOk());
        response.andExpect(jsonPath("$.data.token").value("jwt-token"));
        response.andExpect(jsonPath("$.message").value("Login successful"));
        String responseContent = response.andReturn().getResponse().getContentAsString();
        assertNotNull(responseContent, "Response should not be null");
    }

    @Test
    void registerUser_ShouldReturnSuccessResponse_WhenRegistrationIsValid() throws Exception {
        RegisterUserRequestDTO requestDTO = new RegisterUserRequestDTO();
        requestDTO.setEmail("newuser@example.com");
        requestDTO.setPassword("password123");
        requestDTO.setFirstName("John");
        requestDTO.setLastName("Doe");

        RegisterUserCommand command = new RegisterUserCommand();
        command.setEmail("newuser@example.com");
        command.setPassword("password123");
        command.setFirstName("John");
        command.setLastName("Doe");

        RegisterUserResponseDTO responseDTO = new RegisterUserResponseDTO();
        responseDTO.setEmail("newuser@example.com");

        when(modelMapper.map(any(RegisterUserRequestDTO.class), eq(RegisterUserCommand.class))).thenReturn(command);
        when(authCommandHandler.register(command)).thenReturn(responseDTO);

        MockHttpServletRequestBuilder m = post("/api/auth/register");
                m.contentType(MediaType.APPLICATION_JSON);
                m.content("""
                        {
                            "email": "newuser@example.com",
                            "password": "password123",
                            "firstName": "John",
                            "lastName": "Doe"
                        }
                        """);

        ResultActions response = mockMvc.perform(m);

        response.andExpect(status().isCreated());
        response.andExpect(jsonPath("$.data.email").value("newuser@example.com"));
        response.andExpect(jsonPath("$.message").value("User created successfully. A verification email has been sent."));
        String responseContent = response.andReturn().getResponse().getContentAsString();
        assertNotNull(responseContent, "Response should not be null");
    }

    @Test
    void resendVerificationEmail_ShouldReturnSuccessResponse_WhenEmailIsValid() throws Exception {
        String email = "test@example.com";
        when(authCommandHandler.resendVerificationEmail(email)).thenReturn(true);

        MockHttpServletRequestBuilder requestBuilder = post("/api/auth/resend-verification-email")
                .param("email", email);
        ResultActions response = mockMvc.perform(requestBuilder);

        response.andExpect(status().isOk());
        response.andExpect(jsonPath("$.data").value("Verification email resent"));
        response.andExpect(jsonPath("$.message").value("Verification email resent successfully"));
        String responseContent = response.andReturn().getResponse().getContentAsString();
        assertNotNull(responseContent, "Response should not be null");
    }

    @Test
    void resendVerificationEmail_ShouldReturnFailureResponse_WhenEmailIsInvalid() throws Exception {
        String email = "invalid@example.com";
        when(authCommandHandler.resendVerificationEmail(email)).thenReturn(false);

        MockHttpServletRequestBuilder requestBuilder = post("/api/auth/resend-verification-email")
                .param("email", email);
        ResultActions response = mockMvc.perform(requestBuilder);

        response.andExpect(status().isBadRequest());
        response.andExpect(jsonPath("$.data").value("Resend failed"));
        response.andExpect(jsonPath("$.message").value("Failed to resend verification email. Please try again later."));
        String responseContent = response.andReturn().getResponse().getContentAsString();
        assertNotNull(responseContent, "Response should not be null");
    }

    @Test
    void verifyEmail_ShouldRedirect_WhenTokenIsValid() throws Exception {
        String token = "valid-token";
        when(authCommandHandler.verifyEmailToken(token)).thenReturn(true);

        MockHttpServletRequestBuilder requestBuilder = get("/api/auth/verify-email")
                .param("token", token);
        ResultActions response = mockMvc.perform(requestBuilder);

        response.andExpect(status().isFound());
        response.andExpect(header().string("Location", "http://localhost:3000/auth?status=success"));
    }

    @Test
    void verifyEmail_ShouldReturnFailure_WhenTokenIsInvalid() throws Exception {
        String token = "invalid-token";
        when(authCommandHandler.verifyEmailToken(token)).thenReturn(false);

        MockHttpServletRequestBuilder requestBuilder = get("/api/auth/verify-email")
                .param("token", token);
        ResultActions response = mockMvc.perform(requestBuilder);

        response.andExpect(status().isFound());
        response.andExpect(header().string("Location", "http://localhost:3000/auth?status=failure"));
    }

    @Test
    void forgotPassword_ShouldReturnSuccessResponse_WhenEmailExists() throws Exception {
        String email = "user@example.com";
        when(authService.forgotPassword(email)).thenReturn(new ApiResponseDTO<>("Password reset email sent!", 200, "Success"));

        MockHttpServletRequestBuilder requestBuilder = post("/api/auth/forgot-password")
                .param("email", email);
        ResultActions response = mockMvc.perform(requestBuilder);

        response.andExpect(status().isOk());
        response.andExpect(jsonPath("$.message").value("Password reset email sent!"));
    }



    @Test
    void resetPassword_ShouldReturnSuccessResponse_WhenTokenIsValid() throws Exception {
        String token = "valid-token";
        String newPassword = "newPassword123";
        String confirmPassword = "newPassword123";

        when(authService.resetPassword(token, newPassword)).thenReturn(new ApiResponseDTO<>("Password successfully reset.", 200, "Success"));

        MockHttpServletRequestBuilder requestBuilder = post("/api/auth/reset-password")
                .param("token", token)
                .param("newPassword", newPassword)
                .param("confirmPassword", confirmPassword);
        ResultActions response = mockMvc.perform(requestBuilder);

        response.andExpect(status().isOk());
        response.andExpect(jsonPath("$.message").value("Password successfully reset."));
    }

    @Test
    void resetPassword_ShouldReturnError_WhenPasswordsDoNotMatch() throws Exception {
        String token = "valid-token";
        String newPassword = "newPassword123";
        String confirmPassword = "differentPassword";

        MockHttpServletRequestBuilder requestBuilder = post("/api/auth/reset-password")
                .param("token", token)
                .param("newPassword", newPassword)
                .param("confirmPassword", confirmPassword);
        ResultActions response = mockMvc.perform(requestBuilder);

        response.andExpect(status().isBadRequest());
        response.andExpect(jsonPath("$.message").value("Passwords do not match"));
    }
}
