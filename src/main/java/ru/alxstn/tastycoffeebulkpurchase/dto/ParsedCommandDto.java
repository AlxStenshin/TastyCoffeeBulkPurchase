package ru.alxstn.tastycoffeebulkpurchase.dto;

import ru.alxstn.tastycoffeebulkpurchase.model.BotCommand;

public record ParsedCommandDto(BotCommand command, String text) {
}
