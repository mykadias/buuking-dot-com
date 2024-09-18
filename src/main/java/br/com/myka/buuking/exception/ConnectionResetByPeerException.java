package br.com.myka.buuking.exception;

public class ConnectionResetByPeerException extends RuntimeException {

    public ConnectionResetByPeerException() {
        super("Connection was fakely reset by peer");
    }
}
