package ru.alxstn.tastycoffeebulkpurchase.handler.update.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.SetProductCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.SetSubCategoryCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.event.SendMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.handler.update.CallbackUpdateHandler;
import ru.alxstn.tastycoffeebulkpurchase.repository.ProductRepository;
import ru.alxstn.tastycoffeebulkpurchase.util.DtoSerializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class SetSubCategoryCommandHandler extends CallbackUpdateHandler<SetSubCategoryCommandDto> {
    Logger logger = LogManager.getLogger(SetCategoryCommandHandler.class);
    private final ApplicationEventPublisher publisher;
    private final ProductRepository productRepository;

    public SetSubCategoryCommandHandler(ApplicationEventPublisher publisher,
                                     ProductRepository productRepository) {
        this.publisher = publisher;
        this.productRepository = productRepository;
    }

    @Override
    protected Class<SetSubCategoryCommandDto> getDtoType() {
        return SetSubCategoryCommandDto.class;
    }

    @Override
    protected SerializableInlineType getSerializableType() {
        return SerializableInlineType.SET_SUBCATEGORY;
    }

    @Override
    protected void handleCallback(Update update, SetSubCategoryCommandDto dto) {
        String message = dto.getMessage();
        logger.info("command received " + message);
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        String title = "Выберите продукт:";
        List<String> selectedProducts = productRepository.findAllProductsBySubCategory(message);

        for (String name : selectedProducts) {
            name = name.substring(0, Math.min(name.length(), 21));

            String callback = DtoSerializer.serialize(new SetProductCommandDto(name));
            assert (callback.length() < 64);

            logger.debug("Trying to set callback (" + callback.length() + ") to button: " + callback);
            buttons.add(Collections.singletonList(
                    InlineKeyboardButton.builder()
                            .text(name)
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
