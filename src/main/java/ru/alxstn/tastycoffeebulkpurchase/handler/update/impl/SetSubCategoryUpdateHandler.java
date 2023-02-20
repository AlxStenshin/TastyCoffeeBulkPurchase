package ru.alxstn.tastycoffeebulkpurchase.handler.update.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.alxstn.tastycoffeebulkpurchase.dto.impl.ShowProductNotAvailableAlertDto;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.dto.SerializableInlineType;
import ru.alxstn.tastycoffeebulkpurchase.dto.impl.SetProductNameCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.dto.impl.SetProductSubCategoryCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.dto.serialize.DtoDeserializer;
import ru.alxstn.tastycoffeebulkpurchase.dto.serialize.DtoSerializer;
import ru.alxstn.tastycoffeebulkpurchase.event.bot.UpdateMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.handler.update.CallbackUpdateHandler;
import ru.alxstn.tastycoffeebulkpurchase.bot.MenuNavigationBotMessage;
import ru.alxstn.tastycoffeebulkpurchase.model.ProductCaptionBuilder;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.ProductManagerService;

import java.util.ArrayList;
import static ru.alxstn.tastycoffeebulkpurchase.util.Predicates.distinctByKey;


@Component
public class SetSubCategoryUpdateHandler extends CallbackUpdateHandler<SetProductSubCategoryCommandDto> {

    Logger logger = LogManager.getLogger(SetSubCategoryUpdateHandler.class);
    private final ApplicationEventPublisher publisher;
    private final ProductManagerService productManagerService;
    private final DtoSerializer serializer;

    @Autowired
    private DtoDeserializer deserializer;

    public SetSubCategoryUpdateHandler(ApplicationEventPublisher publisher,
                                       ProductManagerService productManagerService,
                                       DtoSerializer serializer) {
        this.publisher = publisher;
        this.productManagerService = productManagerService;
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

        MenuNavigationBotMessage<Product> answer = new MenuNavigationBotMessage<>(update);
        answer.setTitle("Выберите продукт: ");
        answer.setDataSource(new ArrayList<>(
                productManagerService.findDistinctActiveProductsBySubCategory(targetCategory)).stream()
                .filter(distinctByKey(Product::getName))
                .toList());

        answer.setBackButtonCallback(serializer.serialize(dto.getPrevious()));
        answer.setButtonCreator(p -> InlineKeyboardButton.builder()
                .text(new ProductCaptionBuilder(p).createIconNameView())
                .callbackData(p.isAvailable() ?
                        serializer.serialize(new SetProductNameCommandDto(p.getName(), targetCategory, dto)) :
                        serializer.serialize(new ShowProductNotAvailableAlertDto(p.getName())))
                .build());

        publisher.publishEvent(new UpdateMessageEvent( this, answer.updatePrevious()));
    }
}
