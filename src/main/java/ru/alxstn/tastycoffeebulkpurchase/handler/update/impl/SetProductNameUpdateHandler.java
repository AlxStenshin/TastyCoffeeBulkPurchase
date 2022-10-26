package ru.alxstn.tastycoffeebulkpurchase.handler.update.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.alxstn.tastycoffeebulkpurchase.bot.MenuNavigationBotMessage;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.PlaceOrderCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.SetProductNameCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.SetProductQuantityCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.serialize.DtoDeserializer;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.serialize.DtoSerializer;
import ru.alxstn.tastycoffeebulkpurchase.event.UpdateMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.handler.update.CallbackUpdateHandler;
import ru.alxstn.tastycoffeebulkpurchase.repository.ProductRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Component
public class SetProductNameUpdateHandler extends CallbackUpdateHandler<SetProductNameCommandDto> {

    Logger logger = LogManager.getLogger(SetProductNameUpdateHandler.class);
    private final ApplicationEventPublisher publisher;
    private final ProductRepository productRepository;
    private final DtoSerializer serializer;

    @Autowired
    private DtoDeserializer deserializer;

    public SetProductNameUpdateHandler(ApplicationEventPublisher publisher,
                                       ProductRepository productRepository,
                                       DtoSerializer serializer) {
        super();
        this.publisher = publisher;
        this.productRepository = productRepository;
        this.serializer = serializer;
    }

    @Override
    protected Class<SetProductNameCommandDto> getDtoType() {
        return SetProductNameCommandDto.class;
    }

    @Override
    protected SerializableInlineType getSerializableType() {
        return SerializableInlineType.SET_PRODUCT_NAME;
    }

    @Override
    protected void handleCallback(Update update, SetProductNameCommandDto dto) {
        String productName = dto.getName();
        logger.info("Set Product Name Command Received: " + productName);

        List<Product> availablePackages = productRepository.findAllActiveProductsByProductNameAndSubcategory(
                dto.getName(), dto.getSubCategory());
        Product targetProduct = availablePackages.get(0);
        String title = "Выберите параметры для \n" + targetProduct.getName();

        title += targetProduct.getProductCategory().isEmpty() ? "" : "\nИз категории " + targetProduct.getProductCategory();
        title += targetProduct.getProductSubCategory().isEmpty() ? "" : "\nПодкатегории " + targetProduct.getProductSubCategory();

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        for (Product p : availablePackages) {
            String callback = serializer.serialize(new SetProductQuantityCommandDto(p, 1, dto));

            String packaging = p.getProductPackage().isEmpty() ? "" : p.getProductPackage() + ", ";
            String buttonText = packaging + p.getPrice() + "₽";

            buttons.add(Collections.singletonList(
                    InlineKeyboardButton.builder()
                            .text(buttonText)
                            .callbackData(callback)
                            .build()));
        }

        MenuNavigationBotMessage answer = new MenuNavigationBotMessage(update);
        answer.setTitle(title);
        answer.setBackButtonCallback(serializer.serialize(dto.getPrevious()));
        answer.setSelectProductCategoryButtonCallback(serializer.serialize(new PlaceOrderCommandDto("PlaceOrder")));
        answer.setButtons(buttons);

        publisher.publishEvent(new UpdateMessageEvent(this, answer.updatePrevious()));
    }
}
