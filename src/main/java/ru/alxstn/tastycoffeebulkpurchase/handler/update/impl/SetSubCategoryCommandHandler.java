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
public class SetSubCategoryCommandHandler extends CallbackUpdateHandler<SetSubCategoryCommandDto> {

    Logger logger = LogManager.getLogger(SetSubCategoryCommandHandler.class);
    private final ApplicationEventPublisher publisher;
    private final ProductRepository productRepository;
    private final DtoSerializer serializer;

    @Autowired
    private DtoDeserializer deserializer;

    public SetSubCategoryCommandHandler(ApplicationEventPublisher publisher,
                                        ProductRepository productRepository,
                                        DtoSerializer serializer) {
        this.publisher = publisher;
        this.productRepository = productRepository;
        this.serializer = serializer;
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
        String targetCategory = dto.getMessage();
        logger.info("command received " + targetCategory);
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        String title = "Выберите продукт:";
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

        publisher.publishEvent(new SendMessageEvent(this,
                SendMessage.builder()
                        .text(title)
                        .parseMode("html")
                        .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
                        .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                        .build()));
    }
}
