package priorsolution.training.project1.restaurant_management_app.exception;
//401

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends BaseException {
    public UnauthorizedException(String message, String errorCode) {
        super(message, errorCode);
    }
}
