package ru.alxstn.tastycoffeebulkpurchase.handler.command;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.alxstn.tastycoffeebulkpurchase.entity.BotCommand;
import ru.alxstn.tastycoffeebulkpurchase.entity.Customer;
import ru.alxstn.tastycoffeebulkpurchase.event.SendMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.repository.CustomerRepository;
import ru.alxstn.tastycoffeebulkpurchase.util.DateTimeProvider;

@Component
public class StartCommandHandler implements CommandHandler {
    private final ApplicationEventPublisher publisher;
    private final CustomerRepository repository;
    private final DateTimeProvider dateTimeProvider;

    public StartCommandHandler(ApplicationEventPublisher publisher, CustomerRepository repository,
                               DateTimeProvider dateTimeProvider) {
        this.publisher = publisher;
        this.repository = repository;
        this.dateTimeProvider = dateTimeProvider;
    }

    @Override
    public void handleCommand(Message message, String text) throws TelegramApiException {
        String welcomeMessage = "Привет";

        if (repository.findById(message.getChatId()).isPresent()) {
            welcomeMessage = "С возвращением";
        }
        else {
            Customer customer = new Customer();
            customer.setChatId(message.getChatId());
            customer.setFirstName(message.getChat().getFirstName());
            customer.setLastName(message.getChat().getLastName());
            customer.setUserName(message.getChat().getUserName());
            customer.setRegistrationTimestamp(dateTimeProvider.getCurrentTimestamp());
            repository.save(customer);
        }

        publisher.publishEvent(new SendMessageEvent(this,
                SendMessage.builder()
                .chatId(message.getChatId().toString())
                .parseMode("html")
                .text(welcomeMessage + ", " + message.getChat().getFirstName() + " " + message.getChat().getLastName() + "!")
                .build()));
    }

    @Override
    public BotCommand getCommand() {
        return BotCommand.START;
    }
}
