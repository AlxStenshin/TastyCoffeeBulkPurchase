package ru.alxstn.tastycoffeebulkpurchase.handler.command;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.alxstn.tastycoffeebulkpurchase.entity.BotCommand;
import ru.alxstn.tastycoffeebulkpurchase.handler.UpdateHandler;
import ru.alxstn.tastycoffeebulkpurchase.handler.CommandHandler;
import ru.alxstn.tastycoffeebulkpurchase.handler.command.parser.CommandParser;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.ParsedCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.handler.update.UpdateHandlerStage;

import java.util.Optional;

@Component
public class CommandUpdateHandler implements UpdateHandler {
    private final CommandParser commandParser;
    private final CommandHandlerFactory commandHandlerFactory;

    public CommandUpdateHandler(CommandParser commandParser,
                                CommandHandlerFactory commandHandlerFactory) {
        this.commandParser = commandParser;
        this.commandHandlerFactory = commandHandlerFactory;
    }

    @Override
    public boolean handleUpdate(Update update) {
        if (!update.hasMessage()) {
            return false;
        }

        Message message = update.getMessage();
        if (!message.hasText()) {
            return false;
        }

        String text = message.getText();
        Optional<ParsedCommandDto> command = commandParser.parseCommand(text);
        if (command.isEmpty()) {
            return false;
        }
        handleCommand(update, command.get().getCommand(), command.get().getText());
        return true;
    }

    private void handleCommand(Update update, BotCommand command, String text) {
        CommandHandler commandHandler = commandHandlerFactory.getHandler(command);
        commandHandler.handleCommand(update.getMessage(), text);
    }

    @Override
    public UpdateHandlerStage getStage() {
        return UpdateHandlerStage.COMMAND;
    }
}
