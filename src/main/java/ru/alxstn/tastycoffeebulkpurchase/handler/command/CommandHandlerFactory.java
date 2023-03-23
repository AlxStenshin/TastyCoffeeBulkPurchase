package ru.alxstn.tastycoffeebulkpurchase.handler.command;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import ru.alxstn.tastycoffeebulkpurchase.model.BotCommand;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class CommandHandlerFactory {

    private final List<CommandHandler> handlers;
    private Map<BotCommand, CommandHandler> map;

    public CommandHandlerFactory(List<CommandHandler> handlers) {
        this.handlers = handlers;
    }

    @PostConstruct
    private void init() {
        map = new HashMap<>();
        handlers.forEach(h -> map.put(h.getCommand(), h));
    }

    public CommandHandler getHandler(BotCommand command) {
         return Optional.ofNullable(map.get(command))
                 .orElseThrow(() -> new IllegalStateException("Command Not Supported: " + command));
    }

}
