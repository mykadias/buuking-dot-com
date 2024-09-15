package br.com.myka.buuking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ReservationNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Reservation %s not found.";

    public ReservationNotFoundException(UUID roomId) {
        super(MESSAGE.formatted(roomId));
    }
}
