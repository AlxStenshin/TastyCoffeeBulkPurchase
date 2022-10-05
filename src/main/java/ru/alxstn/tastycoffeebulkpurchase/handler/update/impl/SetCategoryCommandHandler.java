package ru.alxstn.tastycoffeebulkpurchase.handler.update.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.SetCategoryCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.SetSubCategoryCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.event.SendMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.handler.update.CallbackUpdateHandler;
import ru.alxstn.tastycoffeebulkpurchase.repository.ProductRepository;
import ru.alxstn.tastycoffeebulkpurchase.util.DtoSerializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class SetCategoryCommandHandler extends CallbackUpdateHandler<SetCategoryCommandDto> {

    Logger logger = LogManager.getLogger(SetCategoryCommandHandler.class);
    private final ApplicationEventPublisher publisher;
    private final ProductRepository productRepository;

    public SetCategoryCommandHandler(ApplicationEventPublisher publisher,
                                     ProductRepository productRepository) {
        this.publisher = publisher;
        this.productRepository = productRepository;
    }

    @Override
    protected Class<SetCategoryCommandDto> getDtoType() {
        return SetCategoryCommandDto.class;
    }

    @Override
    protected SerializableInlineType getSerializableType() {
        return SerializableInlineType.SET_CATEGORY;
    }

    @Override
    protected void handleCallback(Update update, SetCategoryCommandDto dto) {
        String message = dto.getMessage();
        logger.info("command received " + message);
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        String title = "Выберите подкатегорию:";
        List<String> availableSubCategories = productRepository.findAllSubCategories(message);

        for (String cat : availableSubCategories) {
            cat = cat.substring(0, Math.min(cat.length(), 21));

            String callback = DtoSerializer.serialize(new SetSubCategoryCommandDto(cat));
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
