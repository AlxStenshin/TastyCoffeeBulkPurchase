package ru.alxstn.tastycoffeebulkpurchase.event;

import org.springframework.context.ApplicationEvent;

public class SessionSummaryCheckRequestEvent extends ApplicationEvent {
    private final String reason;

    public SessionSummaryCheckRequestEvent(Object source, String reason) {
        super(source);
        this.reason =reason;
    }

    public String getReason() {
        return reason;
    }
}
