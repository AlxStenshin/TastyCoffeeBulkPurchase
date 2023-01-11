package ru.alxstn.tastycoffeebulkpurchase.handler.command.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.alxstn.tastycoffeebulkpurchase.bot.MainMenuKeyboard;
import ru.alxstn.tastycoffeebulkpurchase.entity.BotCommand;
import ru.alxstn.tastycoffeebulkpurchase.entity.Customer;
import ru.alxstn.tastycoffeebulkpurchase.entity.CustomerNotificationSettings;
import ru.alxstn.tastycoffeebulkpurchase.dto.serialize.DtoDeserializer;
import ru.alxstn.tastycoffeebulkpurchase.event.bot.SendMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.handler.command.CommandHandler;
import ru.alxstn.tastycoffeebulkpurchase.repository.CustomerRepository;

import java.util.Arrays;

@Component
public class StartCommandHandler implements CommandHandler {
    private final ApplicationEventPublisher publisher;
    private final CustomerRepository customerRepository;

    @Autowired
    private DtoDeserializer deserializer;

    public StartCommandHandler(ApplicationEventPublisher publisher,
                               CustomerRepository customerRepository) {
        this.publisher = publisher;
        this.customerRepository = customerRepository;
    }

    @Override
    public void handleCommand(Message message, String text) {
        String welcomeMessage = "Привет";

        if (customerRepository.findById(message.getChatId()).isPresent()) {
            welcomeMessage = "С возвращением";
        } else {
            Customer customer = new Customer(message.getChatId(),
                    message.getChat().getFirstName(),
                    message.getChat().getLastName(),
                    message.getChat().getUserName());

            CustomerNotificationSettings settings = new CustomerNotificationSettings();
            customer.setNotificationSettings(settings);
            customerRepository.save(customer);
        }

        publisher.publishEvent(new SendMessageEvent(this,
                SendMessage.builder()
                        .text(welcomeMessage + ", " +
                                message.getChat().getFirstName() + " " +
                                message.getChat().getLastName() + "!")
                        .replyMarkup(
                                ReplyKeyboardMarkup.builder()
                                        .resizeKeyboard(true)
                                        .keyboardRow(
                                                new KeyboardRow(
                                                        Arrays.asList(
                                                                KeyboardButton.builder()
                                                                        .text(MainMenuKeyboard.PLACE_ORDER.getLabel())
                                                                        .build(),
                                                                KeyboardButton.builder()
                                                                        .text(MainMenuKeyboard.SETTING.getLabel())
                                                                        .build())))
                                        .keyboardRow(
                                                new KeyboardRow(
                                                        Arrays.asList(
                                                                KeyboardButton.builder()
                                                                        .text(MainMenuKeyboard.EDIT_ORDER.getLabel())
                                                                        .build(),
                                                                KeyboardButton.builder()
                                                                        .text(MainMenuKeyboard.INFORMATION.getLabel())
                                                                        .build())))
                                        .build())
                        .chatId(message.getChatId().toString())
                        .parseMode("html")
                        .build()));
    }

    @Override
    public BotCommand getCommand() {
        return BotCommand.START;
    }
}
