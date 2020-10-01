package pl.poznan.put.thesisapi.exceptions;

public class UserAlreadyExistException extends Exception {
    private String message;

    public UserAlreadyExistException(final String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
