package ru.alxstn.tastycoffeebulkpurchase.event;

import org.springframework.context.ApplicationEvent;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

public class RemoveMessageEvent extends ApplicationEvent {
    DeleteMessage deleteMessage;

    public RemoveMessageEvent(Object source, DeleteMessage deleteMessage) {
        super(source);
        this.deleteMessage = deleteMessage;
    }

    public DeleteMessage getDeleteMessage() {
        return deleteMessage;
    }
}
