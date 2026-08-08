package io.turismo.backend.exception;

public class UserIsNotAdminOrOwnerException extends RuntimeException {
    public UserIsNotAdminOrOwnerException(String message) {
        super(message);
    }
}
