package ru.alxstn.tastycoffeebulkpurchase.event.bot;

import org.springframework.context.ApplicationEvent;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class SendMessageEvent extends ApplicationEvent {
    private final SendMessage message;

    public SendMessageEvent(Object source, SendMessage message) {
        super(source);
        this.message = message;
    }

    public SendMessage getMessage() {
        return message;
    }
}
