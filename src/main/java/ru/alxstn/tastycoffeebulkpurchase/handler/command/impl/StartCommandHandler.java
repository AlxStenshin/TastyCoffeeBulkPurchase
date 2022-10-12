package ru.alxstn.tastycoffeebulkpurchase.handler.command.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.alxstn.tastycoffeebulkpurchase.entity.BotCommand;
import ru.alxstn.tastycoffeebulkpurchase.entity.Customer;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.MainMenuCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.serialize.DtoDeserializer;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.serialize.DtoSerializer;
import ru.alxstn.tastycoffeebulkpurchase.event.SendMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.handler.CommandHandler;
import ru.alxstn.tastycoffeebulkpurchase.repository.CustomerRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class StartCommandHandler implements CommandHandler {
    private final ApplicationEventPublisher publisher;
    private final CustomerRepository repository;
    private final DtoSerializer serializer;

    @Autowired
    private DtoDeserializer deserializer;

    public StartCommandHandler(ApplicationEventPublisher publisher,
                               CustomerRepository repository,
                               DtoSerializer serializer) {
        this.publisher = publisher;
        this.repository = repository;
        this.serializer = serializer;
    }

    @Override
    public void handleCommand(Message message, String text) {
        String welcomeMessage = "Привет";

        if (repository.findById(message.getChatId()).isPresent()) {
            welcomeMessage = "С возвращением";
        } else {
            Customer customer = new Customer();
            customer.setChatId(message.getChatId());
            customer.setFirstName(message.getChat().getFirstName());
            customer.setLastName(message.getChat().getLastName());
            customer.setUserName(message.getChat().getUserName());
            repository.save(customer);
        }

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        buttons.add(Collections.singletonList(
                InlineKeyboardButton.builder()
                        .text("Собрать заказ")
                        .callbackData(serializer.serialize(new MainMenuCommandDto("PlaceOrder")))
                        .build()));

        buttons.add(Collections.singletonList(
                InlineKeyboardButton.builder()
                        .text("Просмотреть заказ")
                        .callbackData(serializer.serialize(new MainMenuCommandDto("ViewOrder")))
                        .build()));

        buttons.add(Collections.singletonList(
                InlineKeyboardButton.builder()
                        .text("Настройки")
                        .callbackData(serializer.serialize(new MainMenuCommandDto("Settings")))
                        .build()));

        buttons.add(Collections.singletonList(
                InlineKeyboardButton.builder()
                        .text("Статистика")
                        .callbackData(serializer.serialize(new MainMenuCommandDto("ViewStats")))
                        .build()));

        publisher.publishEvent(new SendMessageEvent(this,
                SendMessage.builder()
                        .text(welcomeMessage + ", " +
                                message.getChat().getFirstName() + " " +
                                message.getChat().getLastName() + "!")
                        .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                        .chatId(message.getChatId().toString())
                        .parseMode("html")
                        .build()));
    }

    @Override
    public BotCommand getCommand() {
        return BotCommand.START;
    }
}
