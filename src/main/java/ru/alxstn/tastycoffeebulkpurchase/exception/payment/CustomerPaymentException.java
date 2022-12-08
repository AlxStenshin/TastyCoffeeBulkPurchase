package ru.alxstn.tastycoffeebulkpurchase.exception.payment;

public class CustomerPaymentException extends RuntimeException {
    public CustomerPaymentException(String message) {
        super(message);
    }
}
