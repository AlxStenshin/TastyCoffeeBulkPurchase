package ru.alxstn.tastycoffeebulkpurchase.handler.command;

import java.util.Optional;

public interface CommandParser {

    Optional<ParsedCommandDTO> parseCommand(String message);

}
