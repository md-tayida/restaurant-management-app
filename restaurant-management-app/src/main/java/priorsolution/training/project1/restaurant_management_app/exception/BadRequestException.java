package priorsolution.training.project1.restaurant_management_app.exception;
//400
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends BaseException {
    public BadRequestException(String message, String errorCode) {
        super(message, errorCode);
    }
}