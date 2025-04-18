package secretstuffs.domain.models.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseDTO<T> {

    private String message;
    private int statusCode;
    private T data;  // Data can be of any type, including a class object
    private LocalDateTime timestamp;

    public ApiResponseDTO(String message, int statusCode, T data) {
        this.message = message;
        this.statusCode = statusCode;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }
}
