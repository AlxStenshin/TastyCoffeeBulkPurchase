package ru.alxstn.tastycoffeebulkpurchase.handler.update.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.SavePurchaseCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.SetProductQuantityCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.serialize.DtoSerializer;
import ru.alxstn.tastycoffeebulkpurchase.event.UpdateMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.handler.update.CallbackUpdateHandler;
import ru.alxstn.tastycoffeebulkpurchase.repository.CustomerRepository;
import ru.alxstn.tastycoffeebulkpurchase.repository.ProductRepository;
import ru.alxstn.tastycoffeebulkpurchase.repository.SessionRepository;

import java.util.ArrayList;
import java.util.List;

@Component
public class SetProductQuantityUpdateHandler extends CallbackUpdateHandler<SetProductQuantityCommandDto> {

    Logger logger = LogManager.getLogger(SetProductQuantityUpdateHandler.class);
    private final ApplicationEventPublisher publisher;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final SessionRepository sessionRepository;
    private final DtoSerializer serializer;

    public SetProductQuantityUpdateHandler(ApplicationEventPublisher publisher,
                                           ProductRepository productRepository,
                                           CustomerRepository customerRepository,
                                           SessionRepository sessionRepository,
                                           DtoSerializer serializer) {
        this.publisher = publisher;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.sessionRepository = sessionRepository;
        this.serializer = serializer;
    }

    @Override
    protected Class<SetProductQuantityCommandDto> getDtoType() {
        return SetProductQuantityCommandDto.class;
    }

    @Override
    protected SerializableInlineType getSerializableType() {
        return SerializableInlineType.EDIT_PRODUCT_QUANTITY;
    }

    @Override
    protected void handleCallback(Update update, SetProductQuantityCommandDto dto) {
        String title = "Выберите количество:";

        List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();
        List<InlineKeyboardButton> countButtonsRow = new ArrayList<>();

        countButtonsRow.add(InlineKeyboardButton.builder()
                .text("-")
                .callbackData(serializer.serialize(new SetProductQuantityCommandDto(dto.getProductId(),
                        dto.getProductQuantity() > 1 ? dto.getProductQuantity() - 1 : 1)))
                .build());

        countButtonsRow.add(InlineKeyboardButton.builder()
                .text(String.valueOf(dto.getProductQuantity()))
                .callbackData("empty")
                .build());

        countButtonsRow.add(InlineKeyboardButton.builder()
                .text("+")
                .callbackData(serializer.serialize(new SetProductQuantityCommandDto(dto.getProductId(),
                        dto.getProductQuantity() + 1)))
                .build());


        long chatId = update.getCallbackQuery().getMessage().getChatId();

        countButtonsRow.add(InlineKeyboardButton.builder()
                .text("Добавить")
                .callbackData(serializer.serialize(new SavePurchaseCommandDto(
                        chatId,
                        dto.getProductId(),
                        dto.getProductQuantity(),
                        dto.getProductForm())))
                .build());

        keyboardRows.add(countButtonsRow);

        InlineKeyboardMarkup.InlineKeyboardMarkupBuilder builder = InlineKeyboardMarkup.builder();
        for (var row : keyboardRows) {
            builder.keyboardRow(row);
        }

        publisher.publishEvent(new UpdateMessageEvent( this,
                EditMessageText.builder()
                        .text(title)
                        .messageId(update.getCallbackQuery().getMessage().getMessageId())
                        .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
                        .replyMarkup(builder.build())
                        .build()));
    }
}
