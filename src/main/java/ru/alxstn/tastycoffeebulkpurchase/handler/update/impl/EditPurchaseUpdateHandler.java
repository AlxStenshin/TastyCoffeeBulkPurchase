package ru.alxstn.tastycoffeebulkpurchase.handler.update.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.alxstn.tastycoffeebulkpurchase.dto.impl.*;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.entity.Purchase;
import ru.alxstn.tastycoffeebulkpurchase.dto.SerializableInlineType;
import ru.alxstn.tastycoffeebulkpurchase.dto.serialize.DtoSerializer;
import ru.alxstn.tastycoffeebulkpurchase.event.bot.UpdateMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.handler.update.CallbackUpdateHandler;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.ProductManagerService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class EditPurchaseUpdateHandler extends CallbackUpdateHandler<EditPurchaseCommandDto> {

    Logger logger = LogManager.getLogger(EditPurchaseUpdateHandler.class);
    private final ApplicationEventPublisher publisher;
    private final ProductManagerService productManagerService;
    private final DtoSerializer serializer;

    public EditPurchaseUpdateHandler(ApplicationEventPublisher publisher,
                                     ProductManagerService productManagerService,
                                     DtoSerializer serializer) {
        this.publisher = publisher;
        this.productManagerService = productManagerService;
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

        String title = "Измените заказ: \n" + targetProduct.getFullDisplayNameWithPackage();

        List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();
        List<InlineKeyboardButton> countButtonsRow = new ArrayList<>();

        countButtonsRow.add(InlineKeyboardButton.builder()
                .text("-")
                .callbackData(serializer.serialize(new EditPurchaseCommandDto(
                        new Purchase(purchase,purchase.getCount() > 1 ? purchase.getCount() - 1 : 1),
                        dto.getPrevious())))
                .build());

        countButtonsRow.add(InlineKeyboardButton.builder()
                .text(purchase.getProductCountAndTotalPrice())
                .callbackData(" ")
                .build());

        countButtonsRow.add(InlineKeyboardButton.builder()
                .text("+")
                .callbackData(serializer.serialize(new EditPurchaseCommandDto(
                        new Purchase(purchase,  purchase.getCount() < Integer.MAX_VALUE ? purchase.getCount() + 1 : Integer.MAX_VALUE),
                        dto.getPrevious())))
                .build());

        countButtonsRow.add(InlineKeyboardButton.builder()
                .text("Сохранить")
                .callbackData(serializer.serialize(new UpdatePurchaseCommandDto(purchase)))
                .build());

        keyboardRows.add(countButtonsRow);

        if (targetProduct.isGrindable()) {
            String currentForm = purchase.getProduct().getProductForm();

            List<InlineKeyboardButton> productFormButtons = new ArrayList<>();

            Optional<Product> beansProduct = productManagerService.findProductWithForm(purchase.getProduct(), "Зерно");
            beansProduct.ifPresent(product -> productFormButtons.add(InlineKeyboardButton.builder()
                    .text(currentForm.equals("Зерно") || currentForm.isEmpty() ? "Зерно ✅" : "Зерно")
                    .callbackData(serializer.serialize(new EditPurchaseCommandDto(
                            new Purchase(purchase, product))))
                    .build()));

            Optional<Product> coarseProduct = productManagerService.findProductWithForm(purchase.getProduct(), "Крупный");
            coarseProduct.ifPresent(product -> productFormButtons.add(InlineKeyboardButton.builder()
                    .text(currentForm.equals("Крупный") ? "Крупный ✅" : "Крупный")
                    .callbackData(serializer.serialize(new EditPurchaseCommandDto(
                            new Purchase(purchase, product))))
                    .build()));

            Optional<Product> mediumProduct = productManagerService.findProductWithForm(purchase.getProduct(), "Средний");
            mediumProduct.ifPresent(product -> productFormButtons.add(InlineKeyboardButton.builder()
                    .text(currentForm.equals("Средний") ? "Средний ✅" : "Средний")
                    .callbackData(serializer.serialize(new EditPurchaseCommandDto(
                            new Purchase(purchase, product))))
                    .build()));

            Optional<Product> fineProduct = productManagerService.findProductWithForm(purchase.getProduct(), "Мелкий");
            fineProduct.ifPresent(product -> productFormButtons.add(InlineKeyboardButton.builder()
                    .text(currentForm.equals("Мелкий") ? "Мелкий ✅" : "Мелкий")
                    .callbackData(serializer.serialize(new EditPurchaseCommandDto(
                            new Purchase(purchase, product))))
                    .build()));

            keyboardRows.add(productFormButtons);
        }

        // Editing existing purchase
        if (purchase.getId() != null) {
            List<InlineKeyboardButton> deletePurchaseButtons = new ArrayList<>();
            deletePurchaseButtons.add(InlineKeyboardButton.builder()
                    .text("Удалить из заказа")
                    .callbackData(serializer.serialize(new RemovePurchaseCommandDto(purchase)))
                    .build());

            RemoveMessageCommandDto removeMessage = new RemoveMessageCommandDto(
                    update.getCallbackQuery().getMessage().getMessageId(),
                    update.getCallbackQuery().getMessage().getChatId());
            deletePurchaseButtons.add(InlineKeyboardButton.builder()
                    .text("Отмена")
                    .callbackData(serializer.serialize(removeMessage))
                    .build());

            keyboardRows.add(deletePurchaseButtons);

        } else {
            List<InlineKeyboardButton> menuNavigationButtons = new ArrayList<>();
            if (dto.getPrevious() != null)
                menuNavigationButtons.add(InlineKeyboardButton.builder()
                        .text("< Назад")
                        .callbackData(serializer.serialize(dto.getPrevious()))
                        .build());

            menuNavigationButtons.add(InlineKeyboardButton.builder()
                    .text("<< Категории")
                    .callbackData(serializer.serialize(new PlaceOrderCommandDto("PlaceOrder")))
                    .build());

            keyboardRows.add(menuNavigationButtons);
        }

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
