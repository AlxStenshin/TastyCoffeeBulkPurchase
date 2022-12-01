package ru.alxstn.tastycoffeebulkpurchase.exception.session;

public class SessionIsNotOpenException extends RuntimeException {
    public SessionIsNotOpenException(String message) {
        super(message);
    }
}
