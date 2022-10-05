package ru.alxstn.tastycoffeebulkpurchase.handler.command;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.alxstn.tastycoffeebulkpurchase.entity.BotCommand;

public interface CommandHandler {

    void handleCommand(Message message, String text);

    BotCommand getCommand();
}
