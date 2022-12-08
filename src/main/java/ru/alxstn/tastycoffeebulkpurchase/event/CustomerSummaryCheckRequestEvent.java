package ru.alxstn.tastycoffeebulkpurchase.event;

import org.springframework.context.ApplicationEvent;
import ru.alxstn.tastycoffeebulkpurchase.entity.Customer;

public class CustomerSummaryCheckRequestEvent extends ApplicationEvent {
    private final String reason;
    private final Customer customer;

    public CustomerSummaryCheckRequestEvent(Object source,
                                            Customer customer,
                                            String reason) {
        super(source);
        this.customer = customer;
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public Customer getCustomer() {
        return customer;
    }
}
