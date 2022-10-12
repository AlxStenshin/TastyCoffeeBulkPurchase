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
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.SetProductPackageCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.SetProductNameCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.serialize.DtoDeserializer;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.serialize.DtoSerializer;
import ru.alxstn.tastycoffeebulkpurchase.event.SendMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.handler.update.CallbackUpdateHandler;
import ru.alxstn.tastycoffeebulkpurchase.repository.ProductRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Component
public class SetProductNameCommandHandler extends CallbackUpdateHandler<SetProductNameCommandDto> {

    Logger logger = LogManager.getLogger(SetProductNameCommandHandler.class);
    private final ApplicationEventPublisher publisher;
    private final ProductRepository productRepository;
    private final DtoSerializer serializer;

    @Autowired
    private DtoDeserializer deserializer;

    public SetProductNameCommandHandler(ApplicationEventPublisher publisher,
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
        String message = dto.getName();
        logger.info("Command Received: " + message);

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        String title = "Выберите упаковку для " + message + ":";
        List<Product> availablePackages = productRepository.findAllProductsByProductNameAndSubcategory(dto.getName(), dto.getSubCategory());

        for (Product p : availablePackages) {
            String callback = serializer.serialize(
                    new SetProductPackageCommandDto(message, p.getProductPackage(), p.getPrice().toString()));
            assert (callback.length() < 64);

            String buttonText = p.getProductPackage() + ", " + p.getPrice() + "₽";
            logger.debug("Trying to set button text: " + buttonText);
            assert (buttonText.length() < 21);

            logger.debug("Trying to set callback (" + callback.length() + "b) to button: " + callback);
            buttons.add(Collections.singletonList(
                    InlineKeyboardButton.builder()
                            .text(buttonText)
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
