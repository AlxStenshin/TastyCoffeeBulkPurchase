package ru.alxstn.tastycoffeebulkpurchase.event;

import org.springframework.context.ApplicationEvent;

public class DiscountCheckRequestEvent extends ApplicationEvent {
    private final String reason;

    public DiscountCheckRequestEvent(Object source, String reason) {
        super(source);
        this.reason =reason;
    }

    public String getReason() {
        return reason;
    }
}
