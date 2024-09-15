package br.com.myka.buuking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PropertyNotFoundException extends RuntimeException {
    private static final String MESSAGE = "Property %s not found";

    public PropertyNotFoundException(UUID propertyId) {
        super(MESSAGE.formatted(propertyId));
    }
}
