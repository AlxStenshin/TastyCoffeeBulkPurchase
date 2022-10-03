package ru.alxstn.tastycoffeebulkpurchase.handler.command;

import ru.alxstn.tastycoffeebulkpurchase.entity.BotCommand;

public class ParsedCommandDTO {
    private final BotCommand command;
    private final String text;

    public ParsedCommandDTO(BotCommand command, String text) {
        this.command = command;
        this.text = text;
    }

    public BotCommand getCommand() {
        return command;
    }

    public String getText() {
        return text;
    }
}
