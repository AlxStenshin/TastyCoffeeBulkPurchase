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
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.MainMenuCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.SavePurchaseCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.SetProductQuantityCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.serialize.DtoSerializer;
import ru.alxstn.tastycoffeebulkpurchase.event.UpdateMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.handler.update.CallbackUpdateHandler;

import java.util.ArrayList;
import java.util.List;

@Component
public class SetProductQuantityUpdateHandler extends CallbackUpdateHandler<SetProductQuantityCommandDto> {

    Logger logger = LogManager.getLogger(SetProductQuantityUpdateHandler.class);
    private final ApplicationEventPublisher publisher;
    private final DtoSerializer serializer;

    public SetProductQuantityUpdateHandler(ApplicationEventPublisher publisher,
                                           DtoSerializer serializer) {
        this.publisher = publisher;
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
        Product targetProduct = dto.getTargetProduct();

        logger.info("Set Product Quantity Command Received: " + targetProduct.getName());

        String title = "Выберите количество: " + targetProduct.getName();
        title += targetProduct.getProductPackage().isEmpty() ? " " : ", " + targetProduct.getProductPackage() + " ";
        title += targetProduct.getPrice() + "₽";

        List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();
        List<InlineKeyboardButton> countButtonsRow = new ArrayList<>();

        countButtonsRow.add(InlineKeyboardButton.builder()
                .text("-")
                .callbackData(serializer.serialize(new SetProductQuantityCommandDto(dto,
                        dto.getProductQuantity() > 1 ? dto.getProductQuantity() - 1 : 1)))
                .build());

        countButtonsRow.add(InlineKeyboardButton.builder()
                .text(dto.getProductQuantity() + " шт, " + targetProduct.getPrice() * dto.getProductQuantity() + "₽")
                .callbackData(" ")
                .build());

        countButtonsRow.add(InlineKeyboardButton.builder()
                .text("+")
                .callbackData(serializer.serialize(new SetProductQuantityCommandDto(dto,
                        dto.getProductQuantity() < Integer.MAX_VALUE ? dto.getProductQuantity() + 1 : Integer.MAX_VALUE)))
                .build());

        long chatId = update.getCallbackQuery().getMessage().getChatId();

        countButtonsRow.add(InlineKeyboardButton.builder()
                .text("Добавить")
                .callbackData(serializer.serialize(new SavePurchaseCommandDto(
                        chatId,
                        dto.getTargetProduct(),
                        dto.getProductQuantity(),
                        dto.getProductForm())))
                .build());

        keyboardRows.add(countButtonsRow);

        if (targetProduct.isGrindable()) {
            String currentForm = dto.getProductForm();

            List<InlineKeyboardButton> productFormButtons = new ArrayList<>();
            productFormButtons.add(InlineKeyboardButton.builder()
                    .text(currentForm.equals("") ? "Зерно *" : "Зерно")
                    .callbackData(serializer.serialize(new SetProductQuantityCommandDto(dto, "")))
                    .build());

            productFormButtons.add(InlineKeyboardButton.builder()
                    .text(currentForm.equals("Coarse") ? "Крупный *" : "Крупный")
                    .callbackData(serializer.serialize(new SetProductQuantityCommandDto(dto, "Coarse")))
                    .build());

            productFormButtons.add(InlineKeyboardButton.builder()
                    .text(currentForm.equals("Medium") ? "Средний *" : "Средний")
                    .callbackData(serializer.serialize(new SetProductQuantityCommandDto(dto, "Medium")))
                    .build());

            productFormButtons.add(InlineKeyboardButton.builder()
                    .text(currentForm.equals("Fine") ? "Мелкий *" : "Мелкий")
                    .callbackData(serializer.serialize(new SetProductQuantityCommandDto(dto, "Fine")))
                    .build());

            keyboardRows.add(productFormButtons);
        }

        List<InlineKeyboardButton> menuNavigationButtons = new ArrayList<>();
        menuNavigationButtons.add(InlineKeyboardButton.builder()
                .text("< Назад")
                .callbackData(serializer.serialize(dto.getPrevious()))
                .build());

        menuNavigationButtons.add(InlineKeyboardButton.builder()
                .text("Выбрать категорию")
                .callbackData(serializer.serialize(new MainMenuCommandDto("PlaceOrder")))
                .build());

        menuNavigationButtons.add(InlineKeyboardButton.builder()
                .text("Главное меню")
                .callbackData(serializer.serialize(new MainMenuCommandDto("PlaceOrder")))
                .build());

        keyboardRows.add(menuNavigationButtons);

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
