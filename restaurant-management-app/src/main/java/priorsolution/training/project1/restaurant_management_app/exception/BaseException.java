package priorsolution.training.project1.restaurant_management_app.exception;

import lombok.Getter;


@Getter
public abstract class BaseException extends RuntimeException {
    private final String errorCode;

    public BaseException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

}


