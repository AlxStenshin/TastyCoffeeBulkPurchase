package ru.alxstn.tastycoffeebulkpurchase.handler.update.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.alxstn.tastycoffeebulkpurchase.bot.MenuNavigationBotMessage;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.MainMenuCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.SetProductCategoryCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.serialize.DtoDeserializer;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.serialize.DtoSerializer;
import ru.alxstn.tastycoffeebulkpurchase.event.UpdateMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.handler.update.CallbackUpdateHandler;
import ru.alxstn.tastycoffeebulkpurchase.repository.ProductRepository;

@Component
public class PlaceOrderUpdateHandler extends CallbackUpdateHandler<MainMenuCommandDto> {

    Logger logger = LogManager.getLogger(PlaceOrderUpdateHandler.class);
    private final ApplicationEventPublisher publisher;
    private final ProductRepository productRepository;
    private final DtoSerializer serializer;

    @Autowired
    private DtoDeserializer deserializer;

    public PlaceOrderUpdateHandler(ApplicationEventPublisher publisher,
                                   ProductRepository productRepository,
                                   DtoSerializer serializer){
        super();
        this.publisher = publisher;
        this.productRepository = productRepository;
        this.serializer = serializer;
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
        logger.info("Place Order Update Received: " + message);

        MenuNavigationBotMessage answer = new MenuNavigationBotMessage(update);
        answer.setTitle("Выберите категорию: ");
        answer.setDataSource(productRepository.findAllActiveCategories());
        answer.setButtonCreator(s -> InlineKeyboardButton.builder()
                .text(s)
                .callbackData(serializer.serialize(new SetProductCategoryCommandDto(s,
                        new MainMenuCommandDto("PlaceOrder"))))
                .build());

        publisher.publishEvent(new UpdateMessageEvent( this, answer.updatePrevious()));
    }
}