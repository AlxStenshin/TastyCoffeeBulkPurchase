package ru.alxstn.tastycoffeebulkpurchase.handler.update.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.SetProductCategoryCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.SetProductSubCategoryCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.serialize.DtoDeserializer;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.serialize.DtoSerializer;
import ru.alxstn.tastycoffeebulkpurchase.event.UpdateMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.handler.update.CallbackUpdateHandler;
import ru.alxstn.tastycoffeebulkpurchase.bot.MenuNavigationBotMessage;
import ru.alxstn.tastycoffeebulkpurchase.repository.ProductRepository;

@Component
public class SetProductCategoryUpdateHandler extends CallbackUpdateHandler<SetProductCategoryCommandDto> {

    Logger logger = LogManager.getLogger(SetProductCategoryUpdateHandler.class);
    private final ApplicationEventPublisher publisher;
    private final ProductRepository productRepository;
    private final DtoSerializer serializer;

    @Autowired
    private DtoDeserializer deserializer;

    public SetProductCategoryUpdateHandler(ApplicationEventPublisher publisher,
                                           ProductRepository productRepository,
                                           DtoSerializer serializer) {
        this.publisher = publisher;
        this.productRepository = productRepository;
        this.serializer = serializer;
    }

    @Override
    protected Class<SetProductCategoryCommandDto> getDtoType() {
        return SetProductCategoryCommandDto.class;
    }

    @Override
    protected SerializableInlineType getSerializableType() {
        return SerializableInlineType.SET_PRODUCT_CATEGORY;
    }

    @Override
    protected void handleCallback(Update update, SetProductCategoryCommandDto dto) {

        String message = dto.getMessage();
        logger.info("Command received: Set Product Category " + message);

        MenuNavigationBotMessage<String> answer = new MenuNavigationBotMessage<>(update);
        answer.setTitle("Выберите подкатегорию: ");
        answer.setDataSource(productRepository.findAllActiveSubCategories(message));
        answer.setBackButtonCallback(serializer.serialize(dto.getPrevious()));

        answer.setButtonCreator(s -> InlineKeyboardButton.builder()
                .text(s)
                .callbackData(serializer.serialize(new SetProductSubCategoryCommandDto(s, dto)))
                .build());

        publisher.publishEvent(new UpdateMessageEvent( this, answer.updatePrevious()));
    }
}
