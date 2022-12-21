package ru.alxstn.tastycoffeebulkpurchase.handler.command.parser;

import org.springframework.stereotype.Component;
import ru.alxstn.tastycoffeebulkpurchase.configuration.TelegramBotConfigProperties;
import ru.alxstn.tastycoffeebulkpurchase.entity.BotCommand;
import ru.alxstn.tastycoffeebulkpurchase.dto.ParsedCommandDto;

import java.util.Optional;

@Component
public class BasicCommandParser implements CommandParser {

    private final TelegramBotConfigProperties botConfigProperties;
    private final String COMMAND_PREFIX = "/";
    private final String COMMAND_BOT_NAME_DELIMITER = "@";

    public BasicCommandParser(TelegramBotConfigProperties botConfigProperties) {
        this.botConfigProperties = botConfigProperties;
    }

    @Override
    public Optional<ParsedCommandDto> parseCommand(String message) {

        if (message.isEmpty()) {
            return Optional.empty();
        }

        String trimText = message.trim();
        Pair commandAndText = getDelimitedCommandFromText(trimText);

        if (isCommand(commandAndText.getKey())) {
            if (isCommandForMe(commandAndText.getKey())) {
                String commandForParse = cutCommandFromFullText(commandAndText.getKey());
                Optional<BotCommand> command = BotCommand.parseCommand(commandForParse);
                return command.map(c -> new ParsedCommandDto(c, commandAndText.getValue()));
            } else {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    private Pair getDelimitedCommandFromText(String trimText) {
        Pair commandText;

        if (trimText.contains(" ")) {
            int indexOfSpace = trimText.indexOf(" ");
            commandText = new Pair(
                    trimText.substring(0, indexOfSpace),
                    trimText.substring(indexOfSpace + 1));

        } else commandText = new Pair(trimText, "");
        return commandText;
    }

    private String cutCommandFromFullText(String text) {
        return text.contains(COMMAND_BOT_NAME_DELIMITER)
                ? text.substring(1, text.indexOf(COMMAND_BOT_NAME_DELIMITER))
                : text.substring(1);
    }

    private boolean isCommandForMe(String command) {
        if (command.contains(COMMAND_BOT_NAME_DELIMITER)) {
            String botNameForEqual = command.substring(command.indexOf(COMMAND_BOT_NAME_DELIMITER) + 1);
            return botConfigProperties.getName().equals(botNameForEqual);
        }
        return true;
    }

    private boolean isCommand(String text) {
        return text.startsWith(COMMAND_PREFIX);
    }

    private static class Pair {
        String key;
        String value;

        public Pair(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }
    }

}
