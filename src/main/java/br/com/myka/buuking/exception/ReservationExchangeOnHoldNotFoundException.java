package br.com.myka.buuking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ReservationExchangeOnHoldNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Reservation Exchange on Hold %s not found";

    public ReservationExchangeOnHoldNotFoundException(UUID id) {
        super(MESSAGE.formatted(id));
    }
}
