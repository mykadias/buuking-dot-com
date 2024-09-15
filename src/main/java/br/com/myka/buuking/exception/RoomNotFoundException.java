package br.com.myka.buuking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RoomNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Room %s not found.";

    public RoomNotFoundException(UUID roomId) {
        super(MESSAGE.formatted(roomId));
    }
}
