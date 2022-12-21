package ru.alxstn.tastycoffeebulkpurchase.handler.update.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.PlaceOrderCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.SetProductCategoryCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.serialize.DtoDeserializer;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.serialize.DtoSerializer;
import ru.alxstn.tastycoffeebulkpurchase.event.UpdateMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.handler.update.CallbackUpdateHandler;
import ru.alxstn.tastycoffeebulkpurchase.bot.MenuNavigationBotMessage;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.ProductManagerService;

@Component
public class PlaceOrderUpdateHandler extends CallbackUpdateHandler<PlaceOrderCommandDto> {

    Logger logger = LogManager.getLogger(PlaceOrderUpdateHandler.class);
    private final ApplicationEventPublisher publisher;
    private final ProductManagerService productManagerService;
    private final DtoSerializer serializer;

    @Autowired
    private DtoDeserializer deserializer;

    public PlaceOrderUpdateHandler(ApplicationEventPublisher publisher,
                                   ProductManagerService productManagerService,
                                   DtoSerializer serializer) {
        super();
        this.publisher = publisher;
        this.productManagerService = productManagerService;
        this.serializer = serializer;
    }

    @Override
    protected Class<PlaceOrderCommandDto> getDtoType() {
        return PlaceOrderCommandDto.class;
    }

    @Override
    protected SerializableInlineType getSerializableType() {
        return SerializableInlineType.SET_MAIN_COMMAND;
    }

    @Override
    protected void handleCallback(Update update, PlaceOrderCommandDto dto) {

        String message = dto.getMessage();
        logger.info("Place Order Update Received: " + message);

        MenuNavigationBotMessage<String> answer = new MenuNavigationBotMessage<>(update);
        answer.setTitle("Выберите категорию: ");
        answer.setDataSource(productManagerService.findAllActiveCategories());
        answer.setButtonCreator(s -> InlineKeyboardButton.builder()
                .text(s)
                .callbackData(serializer.serialize(new SetProductCategoryCommandDto(s,
                        new PlaceOrderCommandDto("PlaceOrder"))))
                .build());

        publisher.publishEvent(new UpdateMessageEvent(this, answer.updatePrevious()));
    }
}
