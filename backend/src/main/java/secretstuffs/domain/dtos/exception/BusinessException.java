package secretstuffs.domain.dtos.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.Serial;
import java.io.Serializable;

@Getter
@AllArgsConstructor
public class BusinessException extends RuntimeException implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String errorCode;
    private String errorMessage;
    private HttpStatus statusCode;

    public String formatErrorDetails() {
        return String.format("Error Code: %s, Message: %s, Status: %s", errorCode, errorMessage, statusCode);
    }
}
