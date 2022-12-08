package ru.alxstn.tastycoffeebulkpurchase.handler.update.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.SetProductNameCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.SetProductSubCategoryCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.serialize.DtoDeserializer;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.serialize.DtoSerializer;
import ru.alxstn.tastycoffeebulkpurchase.event.UpdateMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.handler.update.CallbackUpdateHandler;
import ru.alxstn.tastycoffeebulkpurchase.bot.MenuNavigationBotMessage;
import ru.alxstn.tastycoffeebulkpurchase.repository.ProductRepository;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class SetSubCategoryUpdateHandler extends CallbackUpdateHandler<SetProductSubCategoryCommandDto> {

    Logger logger = LogManager.getLogger(SetSubCategoryUpdateHandler.class);
    private final ApplicationEventPublisher publisher;
    private final ProductRepository productRepository;
    private final DtoSerializer serializer;

    @Autowired
    private DtoDeserializer deserializer;

    public SetSubCategoryUpdateHandler(ApplicationEventPublisher publisher,
                                       ProductRepository productRepository,
                                       DtoSerializer serializer) {
        this.publisher = publisher;
        this.productRepository = productRepository;
        this.serializer = serializer;
    }

    @Override
    protected Class<SetProductSubCategoryCommandDto> getDtoType() {
        return SetProductSubCategoryCommandDto.class;
    }

    @Override
    protected SerializableInlineType getSerializableType() {
        return SerializableInlineType.SET_PRODUCT_SUBCATEGORY;
    }

    @Override
    protected void handleCallback(Update update, SetProductSubCategoryCommandDto dto) {

        String targetCategory = dto.getMessage();
        logger.info("Command Received: Set Product SubCategory " + targetCategory);

        MenuNavigationBotMessage<String> answer = new MenuNavigationBotMessage<>(update);
        answer.setTitle("Выберите продукт: ");
        answer.setDataSource(new ArrayList<>(productRepository.findDistinctActiveProductsBySubCategory(targetCategory)
                .stream()
                .filter(Product::isAvailable)
                    .map(Product::getName)
                .collect(Collectors.toSet())));

        answer.setBackButtonCallback(serializer.serialize(dto.getPrevious()));
        answer.setButtonCreator(s -> InlineKeyboardButton.builder()
                .text(s)
                .callbackData(serializer.serialize(new SetProductNameCommandDto(s, targetCategory, dto)))
                .build());

        publisher.publishEvent(new UpdateMessageEvent( this, answer.updatePrevious()));
    }
}
