package ru.alxstn.tastycoffeebulkpurchase.event;

import org.springframework.context.ApplicationEvent;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;


public class ActiveSessionClosedNotificationEvent extends ApplicationEvent {
    private final Session session;

    public ActiveSessionClosedNotificationEvent(Object source,
                                                Session session) {
        super(source);
        this.session = session;
    }

    public Session getSession() {
        return session;
    }
}
