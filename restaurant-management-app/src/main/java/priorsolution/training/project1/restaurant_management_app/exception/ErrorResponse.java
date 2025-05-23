package priorsolution.training.project1.restaurant_management_app.exception;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String errorCode;
    private String message;

    public ErrorResponse(LocalDateTime timestamp, int status, String errorCode, String message) {
        this.timestamp = timestamp;
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
    }

    // getters and setters (ถ้าต้องการ)
}
