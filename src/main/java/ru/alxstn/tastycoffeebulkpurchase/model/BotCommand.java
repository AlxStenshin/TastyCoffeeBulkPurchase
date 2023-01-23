package ru.alxstn.tastycoffeebulkpurchase.model;

import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.stream.Stream;

public enum BotCommand {
    START("start", "Initiate"),
    ;

    private final String name;
    private final String description;

    public static Optional<BotCommand> parseCommand(String command) {
        if (StringUtils.isBlank(command)) {
            return Optional.empty();
        }

        String format = StringUtils.trim(command).toLowerCase();
        return Stream.of(values()).filter(c -> c.name.equalsIgnoreCase(format)).findFirst();
    }

    BotCommand(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

}
