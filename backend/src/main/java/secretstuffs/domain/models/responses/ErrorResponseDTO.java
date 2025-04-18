package secretstuffs.domain.models.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Exclude null fields from serialization
public class ErrorResponseDTO {

    private int status;
    private String message;
    private String errorCode;
    private LocalDateTime timestamp;
    private Map<String, String> errors;

    public void addError(String field, String errorMessage) {
        if (errors == null) {
            errors = new HashMap<>();
        }
        errors.put(field, errorMessage);
    }

    private static ErrorResponseDTO createErrorResponse(int status, String message, String errorCode, Map<String, String> errors) {
        ErrorResponseDTO.ErrorResponseDTOBuilder builder = ErrorResponseDTO.builder();
        builder.status(status);
        builder.message(message);
        builder.errorCode(errorCode);
        builder.timestamp(LocalDateTime.now());
        builder.errors(errors);
        return builder.build();
    }
}