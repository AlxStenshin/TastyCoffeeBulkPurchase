package ru.alxstn.tastycoffeebulkpurchase.handler.update.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.SetCategoryCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.SetProductNameCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.SetSubCategoryCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.serialize.DtoDeserializer;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.serialize.DtoSerializer;
import ru.alxstn.tastycoffeebulkpurchase.event.SendMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.handler.update.CallbackUpdateHandler;
import ru.alxstn.tastycoffeebulkpurchase.repository.ProductRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class SetCategoryCommandHandler extends CallbackUpdateHandler<SetCategoryCommandDto> {

    Logger logger = LogManager.getLogger(SetCategoryCommandHandler.class);
    private final ApplicationEventPublisher publisher;
    private final ProductRepository productRepository;
    private final DtoSerializer serializer;

    @Autowired
    private DtoDeserializer deserializer;

    public SetCategoryCommandHandler(ApplicationEventPublisher publisher,
                                     ProductRepository productRepository,
                                     DtoSerializer serializer) {
        this.publisher = publisher;
        this.productRepository = productRepository;
        this.serializer = serializer;
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

        List<String> availableSubCategories = productRepository.findAllSubCategories(message);
        String title;

        if (availableSubCategories.size() > 1) {
            title = "Выберите подкатегорию: ";
            for (String cat : availableSubCategories) {
                cat = cat.substring(0, Math.min(cat.length(), 21));

                String callback = serializer.serialize(new SetSubCategoryCommandDto(cat));
                assert (callback.length() < 64);

                logger.debug("Trying to set callback (" + callback.length() + ") to button: " + callback);
                buttons.add(Collections.singletonList(
                        InlineKeyboardButton.builder()
                                .text(cat)
                                .callbackData(callback)
                                .build()));
            }

        }
        else {
            title = "Выберите продукт: ";
            String targetCategory = availableSubCategories.get(0);
            List<String> selectedProducts = productRepository.findDistinctProductDisplayNamesBySubCategory(targetCategory);

            for (String displayName : selectedProducts) {
                String buttonTitle = displayName.substring(0, Math.min(displayName.length(), 21));
                String callback = serializer.serialize(new SetProductNameCommandDto(displayName, targetCategory));

                buttons.add(Collections.singletonList(
                        InlineKeyboardButton.builder()
                                .text(buttonTitle)
                                .callbackData(callback)
                                .build()));
            }
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
