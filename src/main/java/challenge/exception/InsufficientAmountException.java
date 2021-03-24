package challenge.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class InsufficientAmountException extends RuntimeException {
    public InsufficientAmountException() {
        super();
    }

    public InsufficientAmountException(String message, Throwable cause) {
        super(message, cause);
    }

    public InsufficientAmountException(String message) {
        super(message);
    }

    public InsufficientAmountException(Throwable cause) {
        super(cause);
    }
}
