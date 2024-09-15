package br.com.myka.buuking.exception;

public class ReservationNotCancelableException extends RuntimeException {
    public ReservationNotCancelableException(String message) {
        super(message);
    }
}
