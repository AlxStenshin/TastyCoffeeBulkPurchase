package ru.alxstn.tastycoffeebulkpurchase.handler.update.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.MainMenuCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.SetCategoryCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.event.SendMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.handler.update.CallbackUpdateHandler;
import ru.alxstn.tastycoffeebulkpurchase.repository.ProductRepository;
import ru.alxstn.tastycoffeebulkpurchase.util.DtoSerializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Component
public class MainMenuCommandHandler extends CallbackUpdateHandler<MainMenuCommandDto> {

    Logger logger = LogManager.getLogger(MainMenuCommandHandler.class);
    private final ApplicationEventPublisher publisher;
    private final ProductRepository productRepository;

    public MainMenuCommandHandler(ApplicationEventPublisher publisher,
                                     ProductRepository productRepository) {
        this.publisher = publisher;
        this.productRepository = productRepository;
    }

    @Override
    protected Class<MainMenuCommandDto> getDtoType() {
        return MainMenuCommandDto.class;
    }

    @Override
    protected SerializableInlineType getSerializableType() {
        return SerializableInlineType.SET_MAIN_COMMAND;
    }

    @Override
    protected void handleCallback(Update update, MainMenuCommandDto dto) {
        String message = dto.getMessage();
        logger.info("command received " + message);
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        String title = "Выберите категорию:";
        List<String> availableCategories = productRepository.findAllCategories();

        for (String cat : availableCategories) {
            cat = cat.substring(0, Math.min(cat.length(), 21));

            String callback = DtoSerializer.serialize(new SetCategoryCommandDto(cat));
            assert (callback.length() < 64);

            logger.debug("Trying to set callback (" + callback.length() + ") to button: " + callback);
            buttons.add(Collections.singletonList(
                    InlineKeyboardButton.builder()
                            .text(cat)
                            .callbackData(callback)
                            .build()));
        }

        publisher.publishEvent(new SendMessageEvent(this,
                SendMessage.builder()
                        .text(title)
                        .parseMode("html")
                        .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
                        .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                        .build()));
    }
}
