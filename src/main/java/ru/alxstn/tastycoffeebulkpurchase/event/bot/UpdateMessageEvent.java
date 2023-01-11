package ru.alxstn.tastycoffeebulkpurchase.event.bot;

import org.springframework.context.ApplicationEvent;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

public class UpdateMessageEvent extends ApplicationEvent {
    private final EditMessageText message;

    public UpdateMessageEvent(Object source, EditMessageText message) {
        super(source);
        this.message = message;
    }

    public EditMessageText getMessage() {
        return message;
    }
}