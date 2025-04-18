package secretstuffs.controllers;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import secretstuffs.application.useCases.auth.AuthCommandHandler;
import secretstuffs.application.services.AuthService;
import secretstuffs.domain.dtos.commands.auth.LoginUserCommand;
import secretstuffs.domain.dtos.commands.auth.RegisterUserCommand;
import secretstuffs.domain.models.requests.auth.LoginUserRequestDTO;
import secretstuffs.domain.models.requests.auth.RegisterUserRequestDTO;
import secretstuffs.domain.models.responses.ApiResponseDTO;
import secretstuffs.domain.models.responses.auth.LoginUserResponseDTO;
import secretstuffs.domain.models.responses.auth.RegisterUserResponseDTO;
import java.net.URI;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    @Value("${system.host.name}")
    private String hostName;

    private final ModelMapper modelMapper;
    private final AuthService authService;
    private final AuthCommandHandler authCommandHandler;

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<LoginUserResponseDTO>> loginUser(
            @Valid @RequestBody LoginUserRequestDTO dto) {
        LoginUserCommand command = modelMapper.map(dto, LoginUserCommand.class);
        LoginUserResponseDTO responseDTO = authCommandHandler.login(command);
        return buildResponse("Login successful", HttpStatus.OK, responseDTO);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponseDTO<RegisterUserResponseDTO>> registerUser(
            @Valid @RequestBody RegisterUserRequestDTO dto) {
        RegisterUserCommand command = modelMapper.map(dto, RegisterUserCommand.class);
        RegisterUserResponseDTO responseDTO = authCommandHandler.register(command);
        return buildResponse(
                "User created successfully. A verification email has been sent.",
                HttpStatus.CREATED,
                responseDTO
        );
    }

    @GetMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(@RequestParam("token") String token) {
        boolean isVerified = authCommandHandler.verifyEmailToken(token);
        String redirectUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .port(3000) // Set the port to 3000
                .path("/auth")
                .queryParam("status", isVerified ? "success" : "failure")
                .toUriString();
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(redirectUrl));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @PostMapping("/resend-verification-email")
    public ResponseEntity<ApiResponseDTO<String>> resendVerificationEmail(@RequestParam("email") String email) {
        log.info("Resending verification email to: {}", email);
        boolean isResent = authCommandHandler.resendVerificationEmail(email);
        String message = "Failed to resend verification email. Please try again later.";
        String data = "Resend failed";
        HttpStatus status = HttpStatus.BAD_REQUEST;

        if (isResent) {
            message = "Verification email resent successfully";
            data = "Verification email resent";
            status = HttpStatus.OK;
        }
        return buildResponse(message, status, data);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponseDTO<String>> forgotPassword(@RequestParam("email") String email) {
        ApiResponseDTO<String> response = authService.forgotPassword(email);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponseDTO<String>> resetPassword(
            @RequestParam("token") String token,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword
    ) {
        if (!newPassword.equals(confirmPassword)) {
            String message = "Passwords do not match";
            String data = "PASSWORD_MISMATCH";
            return buildResponse(message, HttpStatus.BAD_REQUEST, data);
        }

        ApiResponseDTO<String> response = authService.resetPassword(token, newPassword);
        return ResponseEntity.ok(response);
    }

    private <T> ResponseEntity<ApiResponseDTO<T>> buildResponse(String message, HttpStatus status, T data) {
        ApiResponseDTO<T> apiResponse = new ApiResponseDTO<>(message, status.value(), data);
        return ResponseEntity.status(status).body(apiResponse);
    }
}