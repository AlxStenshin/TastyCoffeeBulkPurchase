package ru.alxstn.tastycoffeebulkpurchase.dto;

import ru.alxstn.tastycoffeebulkpurchase.entity.BotCommand;

public record ParsedCommandDto(BotCommand command, String text) {
}
