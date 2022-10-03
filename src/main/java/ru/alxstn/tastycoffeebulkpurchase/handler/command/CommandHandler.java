package ru.alxstn.tastycoffeebulkpurchase.handler.command;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.alxstn.tastycoffeebulkpurchase.entity.BotCommand;

public interface CommandHandler {

    void handleCommand(Message message, String text) throws TelegramApiException;

    BotCommand getCommand();
}
