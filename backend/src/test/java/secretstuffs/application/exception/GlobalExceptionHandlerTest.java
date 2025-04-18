package secretstuffs.application.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import secretstuffs.domain.dtos.exception.BusinessException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private HttpServletRequest mockRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleValidationExceptions_ShouldReturnBadRequest() {
        // Arrange
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        var bindingResult = mock(org.springframework.validation.BindingResult.class); // Mock BindingResult
        FieldError fieldError = new FieldError("objectName", "fieldName", "Invalid value");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError)); // Mock FieldErrors
        when(mockRequest.getRequestURI()).thenReturn("/test");

        // Act
        var response = exceptionHandler.handleValidationExceptions(exception, mockRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo("VALIDATION_ERROR");
        assertThat(response.getBody().getErrors()).containsEntry("fieldName", "Invalid value");
        assertThat(response.getBody().getMessage()).isEqualTo("Validation failed");
    }

    @Test
    void handleBusinessException_ShouldReturnCustomStatus() {
        // Arrange
        BusinessException exception = new BusinessException("CUSTOM_ERROR", "Custom error occurred", HttpStatus.CONFLICT);
        when(mockRequest.getRequestURI()).thenReturn("/business-error");

        // Act
        var response = exceptionHandler.handleBusinessException(exception, mockRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo("CUSTOM_ERROR");
        assertThat(response.getBody().getMessage()).isEqualTo("Custom error occurred");
    }

    @Test
    void handleEntityNotFound_ShouldReturnNotFound() {
        // Arrange
        EntityNotFoundException exception = new EntityNotFoundException("Entity not found");
        when(mockRequest.getRequestURI()).thenReturn("/entity-not-found");

        // Act
        var response = exceptionHandler.handleEntityNotFound(exception, mockRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo("NOT_FOUND");
        assertThat(response.getBody().getMessage()).isEqualTo("Resource not found");
    }

    @Test
    void handleArgumentTypeMismatch_ShouldReturnBadRequest() {
        // Arrange
        MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);
        WebRequest mockWebRequest = mock(WebRequest.class); // Mock WebRequest
        when(mockWebRequest.getDescription(false)).thenReturn("/type-mismatch");

        // Act
        var response = exceptionHandler.handleArgumentTypeMismatch(exception, mockWebRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo("ARGUMENT_TYPE_MISMATCH");
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid argument type");
    }

    @Test
    void handleUnreadableMessage_ShouldReturnBadRequest() {
        // Arrange
        HttpInputMessage mockInputMessage = mock(HttpInputMessage.class);
        String errorMessage = "Malformed JSON request";
        HttpMessageNotReadableException exception =
                new HttpMessageNotReadableException(errorMessage, mockInputMessage);
        when(mockRequest.getRequestURI()).thenReturn("/unreadable-message");

        // Act
        var response = exceptionHandler.handleUnreadableMessage(exception, mockRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo("MESSAGE_NOT_READABLE");
        assertThat(response.getBody().getMessage()).isEqualTo(errorMessage);
    }

    @Test
    void handleGenericException_ShouldReturnInternalServerError() {
        // Arrange
        Exception exception = new Exception("Unexpected error");
        when(mockRequest.getRequestURI()).thenReturn("/generic-error");

        // Act
        var response = exceptionHandler.handleGenericException(exception, mockRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo("INTERNAL_SERVER_ERROR");
        assertThat(response.getBody().getMessage()).isEqualTo("An unexpected error occurred");
    }
}