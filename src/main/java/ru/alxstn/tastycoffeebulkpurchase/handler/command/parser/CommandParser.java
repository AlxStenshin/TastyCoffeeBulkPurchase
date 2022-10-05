package ru.alxstn.tastycoffeebulkpurchase.handler.command.parser;

import ru.alxstn.tastycoffeebulkpurchase.handler.command.ParsedCommandDTO;

import java.util.Optional;

public interface CommandParser {

    Optional<ParsedCommandDTO> parseCommand(String message);

}
