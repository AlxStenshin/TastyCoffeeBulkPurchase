package ru.alxstn.tastycoffeebulkpurchase.handler.update.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.entity.Purchase;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.*;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.serialize.DtoSerializer;
import ru.alxstn.tastycoffeebulkpurchase.event.UpdateMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.handler.update.CallbackUpdateHandler;

import java.util.ArrayList;
import java.util.List;

@Component
public class EditPurchaseUpdateHandler extends CallbackUpdateHandler<EditPurchaseCommandDto> {

    Logger logger = LogManager.getLogger(EditPurchaseUpdateHandler.class);
    private final ApplicationEventPublisher publisher;
    private final DtoSerializer serializer;

    public EditPurchaseUpdateHandler(ApplicationEventPublisher publisher, DtoSerializer serializer) {
        this.publisher = publisher;
        this.serializer = serializer;
    }

    @Override
    protected Class<EditPurchaseCommandDto> getDtoType() {
        return EditPurchaseCommandDto.class;
    }

    @Override
    protected SerializableInlineType getSerializableType() {
        return SerializableInlineType.EDIT_PURCHASE;
    }

    @Override
    protected void handleCallback(Update update, EditPurchaseCommandDto dto) {
        Purchase purchase = dto.getPurchase();
        Product targetProduct = purchase.getProduct();

        logger.info("Edit Purchase Command Received: " + purchase);

        String title = "Измените заказ: \n" + targetProduct.getFullDisplayName();

        List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();
        List<InlineKeyboardButton> countButtonsRow = new ArrayList<>();

        countButtonsRow.add(InlineKeyboardButton.builder()
                .text("-")
                .callbackData(serializer.serialize(new EditPurchaseCommandDto(
                        new Purchase(purchase, purchase.getCount() > 1 ? purchase.getCount() - 1 : 1))))
                .build());

        countButtonsRow.add(InlineKeyboardButton.builder()
                .text(purchase.getProductCountAndTotalPrice())
                .callbackData(" ")
                .build());

        countButtonsRow.add(InlineKeyboardButton.builder()
                .text("+")
                .callbackData(serializer.serialize(new EditPurchaseCommandDto(
                        new Purchase(purchase, purchase.getCount() < Integer.MAX_VALUE ? purchase.getCount() + 1 : Integer.MAX_VALUE))))
                .build());

        countButtonsRow.add(InlineKeyboardButton.builder()
                .text("Сохранить")
                .callbackData(serializer.serialize(new UpdatePurchaseCommandDto(purchase)))
                .build());

        keyboardRows.add(countButtonsRow);

        if (targetProduct.isGrindable()) {
            String currentForm = purchase.getProductForm();

            List<InlineKeyboardButton> productFormButtons = new ArrayList<>();
            productFormButtons.add(InlineKeyboardButton.builder()
                    .text(currentForm.equals("Зерно") ? "Зерно *" : "Зерно")
                    .callbackData(serializer.serialize(new EditPurchaseCommandDto(new Purchase(purchase, "Зерно"))))
                    .build());

            productFormButtons.add(InlineKeyboardButton.builder()
                    .text(currentForm.equals("Крупный") ? "Крупный *" : "Крупный")
                    .callbackData(serializer.serialize(new EditPurchaseCommandDto(new Purchase(purchase, "Крупный"))))
                    .build());

            productFormButtons.add(InlineKeyboardButton.builder()
                    .text(currentForm.equals("Средний") ? "Средний *" : "Средний")
                    .callbackData(serializer.serialize(new EditPurchaseCommandDto(new Purchase(purchase, "Средний"))))
                    .build());

            productFormButtons.add(InlineKeyboardButton.builder()
                    .text(currentForm.equals("Мелкий") ? "Мелкий *" : "Мелкий")
                    .callbackData(serializer.serialize(new EditPurchaseCommandDto(new Purchase(purchase, "Мелкий"))))
                    .build());

            keyboardRows.add(productFormButtons);
        }

        List<InlineKeyboardButton> deletePurchaseButtons = new ArrayList<>();
        deletePurchaseButtons.add(InlineKeyboardButton.builder()
                .text("Удалить из заказа")
                .callbackData(serializer.serialize(new RemovePurchaseCommandDto(purchase)))
                .build());

        keyboardRows.add(deletePurchaseButtons);

        InlineKeyboardMarkup.InlineKeyboardMarkupBuilder builder = InlineKeyboardMarkup.builder();
        for (var row : keyboardRows) {
            builder.keyboardRow(row);
        }

        publisher.publishEvent(new UpdateMessageEvent(this,
                EditMessageText.builder()
                        .text(title)
                        .messageId(update.getCallbackQuery().getMessage().getMessageId())
                        .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
                        .replyMarkup(builder.build())
                        .build()));
    }

}
