package ru.alxstn.tastycoffeebulkpurchase.event;

import org.springframework.context.ApplicationEvent;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;

public class AlertMessageEvent  extends ApplicationEvent {
    private final AnswerCallbackQuery message;

    public AlertMessageEvent(Object source, AnswerCallbackQuery message) {
        super(source);
        this.message = message;
    }

    public AnswerCallbackQuery getMessage() {
        return message;
    }
}