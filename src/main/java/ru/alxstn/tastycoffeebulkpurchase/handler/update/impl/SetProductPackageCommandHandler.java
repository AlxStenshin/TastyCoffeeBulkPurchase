package ru.alxstn.tastycoffeebulkpurchase.handler.update.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.SetProductPackageCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.serialize.DtoDeserializer;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.serialize.DtoSerializer;
import ru.alxstn.tastycoffeebulkpurchase.event.UpdateMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.handler.update.CallbackUpdateHandler;
import ru.alxstn.tastycoffeebulkpurchase.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;


@Component
public class SetProductPackageCommandHandler extends CallbackUpdateHandler<SetProductPackageCommandDto> {

    Logger logger = LogManager.getLogger(SetProductPackageCommandHandler.class);
    private final ApplicationEventPublisher publisher;
    private final ProductRepository productRepository;
    private final DtoSerializer serializer;

    @Autowired
    private DtoDeserializer deserializer;

    public SetProductPackageCommandHandler(ApplicationEventPublisher publisher, ProductRepository productRepository, DtoSerializer serializer) {
        this.publisher = publisher;
        this.productRepository = productRepository;
        this.serializer = serializer;
    }

    @Override
    protected Class<SetProductPackageCommandDto> getDtoType() {
        return SetProductPackageCommandDto.class;
    }

    @Override
    protected SerializableInlineType getSerializableType() {
        return SerializableInlineType.SET_PACKAGING;
    }

    @Override
    protected void handleCallback(Update update, SetProductPackageCommandDto dto) {
        String title = "Выберите количество:";

        List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();
        List<InlineKeyboardButton> countButtonsRow = new ArrayList<>();
        countButtonsRow.add(InlineKeyboardButton.builder()
                .text("-")
                .callbackData("-")
                .build());
        countButtonsRow.add(InlineKeyboardButton.builder()
                .text("1")
                .callbackData("1")
                .build());
        countButtonsRow.add(InlineKeyboardButton.builder()
                .text("+")
                .callbackData("+")
                .build());
        countButtonsRow.add(InlineKeyboardButton.builder()
                .text("Добавить")
                .callbackData("Save")
                .build());

        keyboardRows.add(countButtonsRow);

        List<InlineKeyboardButton> productFormButtons = new ArrayList<>();
        productFormButtons.add(InlineKeyboardButton.builder()
                .text("Зерно")
                .callbackData("-")
                .build());
        productFormButtons.add(InlineKeyboardButton.builder()
                .text("Крупный")
                .callbackData("1")
                .build());
        productFormButtons.add(InlineKeyboardButton.builder()
                .text("Средний")
                .callbackData("+")
                .build());
        productFormButtons.add(InlineKeyboardButton.builder()
                .text("Мелкий")
                .callbackData("Save")
                .build());

        keyboardRows.add(productFormButtons);

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
