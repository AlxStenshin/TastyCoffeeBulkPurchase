package ru.alxstn.tastycoffeebulkpurchase.handler.command.parser;

import ru.alxstn.tastycoffeebulkpurchase.dto.ParsedCommandDto;

import java.util.Optional;

public interface CommandParser {

    Optional<ParsedCommandDto> parseCommand(String message);

}
