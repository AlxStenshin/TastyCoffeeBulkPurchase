package ru.alxstn.tastycoffeebulkpurchase.exception.session;

public class SessionNotFoundException extends RuntimeException {
    public SessionNotFoundException() {
        super("Session Not Found");
    }

    public SessionNotFoundException(String message) {
        super(message);
    }
}
