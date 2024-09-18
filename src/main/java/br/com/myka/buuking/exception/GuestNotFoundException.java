package br.com.myka.buuking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class GuestNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Guest %s not found.";

    public GuestNotFoundException(UUID guestId) {
        super(MESSAGE.formatted(guestId));
    }
}
