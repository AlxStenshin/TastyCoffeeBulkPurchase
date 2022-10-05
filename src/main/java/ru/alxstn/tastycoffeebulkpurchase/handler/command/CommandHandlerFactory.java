package ru.alxstn.tastycoffeebulkpurchase.handler.command;

import org.springframework.stereotype.Component;
import ru.alxstn.tastycoffeebulkpurchase.entity.BotCommand;
import ru.alxstn.tastycoffeebulkpurchase.handler.CommandHandler;

import javax.annotation.PostConstruct;
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
