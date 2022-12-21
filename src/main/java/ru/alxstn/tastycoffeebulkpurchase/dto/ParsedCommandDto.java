package ru.alxstn.tastycoffeebulkpurchase.dto;

import ru.alxstn.tastycoffeebulkpurchase.entity.BotCommand;

public class ParsedCommandDto {
    private final BotCommand command;
    private final String text;

    public ParsedCommandDto(BotCommand command, String text) {
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
