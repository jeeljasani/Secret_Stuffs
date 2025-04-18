package secretstuffs.application.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import secretstuffs.domain.dtos.exception.BusinessException;
import secretstuffs.domain.models.responses.ErrorResponseDTO;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String errorMessage = "Validation failed";
        String errorCode = "VALIDATION_ERROR";
        logger.warn("Validation error: URI={}, ErrorCode={}, Message={}", requestURI, errorCode, errorMessage);

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        });

        return buildErrorResponse(
                errorMessage,
                errorCode,
                HttpStatus.BAD_REQUEST,
                fieldErrors
        );
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponseDTO> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String errorCode = ex.getErrorCode();
        String errorMessage = ex.getErrorMessage();
        logger.warn("Business exception: URI={}, ErrorCode={}, Message={}", requestURI, errorCode, errorMessage);

        return buildErrorResponse(
                errorMessage,
                errorCode,
                ex.getStatusCode(),
                null
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleEntityNotFound(EntityNotFoundException ex, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String errorMessage = "Resource not found";
        String errorCode = "NOT_FOUND";
        logger.warn("Entity not found: URI={}, ErrorCode={}, Message={}", requestURI, errorCode, errorMessage);

        return buildErrorResponse(
                errorMessage,
                errorCode,
                HttpStatus.NOT_FOUND,
                null
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDTO> handleArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        String description = request.getDescription(false);
        String errorMessage = "Invalid argument type";
        String errorCode = "ARGUMENT_TYPE_MISMATCH";
        logger.warn("Argument type mismatch: Description={}, ErrorCode={}, Message={}", description, errorCode, errorMessage);

        return buildErrorResponse(
                errorMessage,
                errorCode,
                HttpStatus.BAD_REQUEST,
                null
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleUnreadableMessage(HttpMessageNotReadableException ex, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String errorMessage = "Malformed JSON request";
        String errorCode = "MESSAGE_NOT_READABLE";
        logger.warn("Malformed JSON request: URI={}, ErrorCode={}, Message={}", requestURI, errorCode, errorMessage);

        return buildErrorResponse(
                errorMessage,
                errorCode,
                HttpStatus.BAD_REQUEST,
                null
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String errorMessage = "An unexpected error occurred";
        String errorCode = "INTERNAL_SERVER_ERROR";
        logger.error("Unexpected error: URI={}, ErrorCode={}, Message={}", requestURI, errorCode, errorMessage);

        return buildErrorResponse(
                errorMessage,
                errorCode,
                HttpStatus.INTERNAL_SERVER_ERROR,
                null
        );
    }

    private ResponseEntity<ErrorResponseDTO> buildErrorResponse(String message, String errorCode, HttpStatus status, Map<String, String> errors) {
        ErrorResponseDTO.ErrorResponseDTOBuilder errorResponse = ErrorResponseDTO.builder();
        errorResponse.status(status.value());
        errorResponse.message(message);
        errorResponse.errorCode(errorCode);
        errorResponse.timestamp(LocalDateTime.now());
        errorResponse.errors(errors);

        return ResponseEntity.status(status).body(errorResponse.build());
    }
}