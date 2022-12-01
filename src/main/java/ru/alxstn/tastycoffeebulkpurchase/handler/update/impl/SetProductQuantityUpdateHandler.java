package ru.alxstn.tastycoffeebulkpurchase.handler.update.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.alxstn.tastycoffeebulkpurchase.entity.Customer;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.PlaceOrderCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.SavePurchaseCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.SetProductQuantityCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.serialize.DtoSerializer;
import ru.alxstn.tastycoffeebulkpurchase.event.UpdateMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.exception.session.SessionNotFoundException;
import ru.alxstn.tastycoffeebulkpurchase.handler.update.CallbackUpdateHandler;
import ru.alxstn.tastycoffeebulkpurchase.repository.CustomerRepository;
import ru.alxstn.tastycoffeebulkpurchase.repository.SessionRepository;
import ru.alxstn.tastycoffeebulkpurchase.util.BigDecimalUtil;

import java.util.ArrayList;
import java.util.List;

@Component
public class SetProductQuantityUpdateHandler extends CallbackUpdateHandler<SetProductQuantityCommandDto> {

    Logger logger = LogManager.getLogger(SetProductQuantityUpdateHandler.class);
    private final ApplicationEventPublisher publisher;
    private final DtoSerializer serializer;
    private final SessionRepository sessionRepository;
    private final CustomerRepository customerRepository;

    public SetProductQuantityUpdateHandler(ApplicationEventPublisher publisher,
                                           DtoSerializer serializer,
                                           SessionRepository sessionRepository,
                                           CustomerRepository customerRepository) {
        this.publisher = publisher;
        this.serializer = serializer;
        this.sessionRepository = sessionRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    protected Class<SetProductQuantityCommandDto> getDtoType() {
        return SetProductQuantityCommandDto.class;
    }

    @Override
    protected SerializableInlineType getSerializableType() {
        return SerializableInlineType.SET_PRODUCT_QUANTITY;
    }

    @Override
    protected void handleCallback(Update update, SetProductQuantityCommandDto dto) {
        Product targetProduct = dto.getTargetProduct();

        logger.info("Set Product Quantity Command Received: " + targetProduct.getName());

        String title = "Выберите количество: " + targetProduct.getName();
        title += targetProduct.getProductPackage().getDescription().isEmpty() ? " " : ", " + targetProduct.getProductPackage() + " ";
        title += targetProduct.getPrice() + "₽";

        List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();
        List<InlineKeyboardButton> countButtonsRow = new ArrayList<>();

        countButtonsRow.add(InlineKeyboardButton.builder()
                .text("-")
                .callbackData(serializer.serialize(new SetProductQuantityCommandDto(dto,
                        dto.getProductQuantity() > 1 ? dto.getProductQuantity() - 1 : 1)))
                .build());

        countButtonsRow.add(InlineKeyboardButton.builder()
                .text(dto.getProductQuantity() + " шт, " +
                        BigDecimalUtil.multiplyByInt(targetProduct.getPrice(), dto.getProductQuantity()) + "₽")
                .callbackData(" ")
                .build());

        countButtonsRow.add(InlineKeyboardButton.builder()
                .text("+")
                .callbackData(serializer.serialize(new SetProductQuantityCommandDto(dto,
                        dto.getProductQuantity() < Integer.MAX_VALUE ? dto.getProductQuantity() + 1 : Integer.MAX_VALUE)))
                .build());

        long chatId = update.getCallbackQuery().getMessage().getChatId();
        Customer customer = customerRepository.getByChatId(chatId);
        Session session = sessionRepository.getActiveSession().orElseThrow(SessionNotFoundException::new);

        countButtonsRow.add(InlineKeyboardButton.builder()
                .text("Добавить")
                .callbackData(serializer.serialize(new SavePurchaseCommandDto(
                        customer,
                        dto.getTargetProduct(),
                        session,
                        dto.getProductQuantity(),
                        dto.getProductForm())))
                .build());

        keyboardRows.add(countButtonsRow);

        if (targetProduct.isGrindable()) {
            String currentForm = dto.getProductForm();

            // ToDo: Avoid Code Duplication with Edit Product Entity
            List<InlineKeyboardButton> productFormButtons = new ArrayList<>();
            productFormButtons.add(InlineKeyboardButton.builder()
                    .text(currentForm.equals("Зерно") ? "Зерно ✅" : "Зерно")
                    .callbackData(serializer.serialize(new SetProductQuantityCommandDto(dto, "Зерно")))
                    .build());

            productFormButtons.add(InlineKeyboardButton.builder()
                    .text(currentForm.equals("Крупный") ? "Крупный ✅" : "Крупный")
                    .callbackData(serializer.serialize(new SetProductQuantityCommandDto(dto, "Крупный")))
                    .build());

            productFormButtons.add(InlineKeyboardButton.builder()
                    .text(currentForm.equals("Средний") ? "Средний ✅" : "Средний")
                    .callbackData(serializer.serialize(new SetProductQuantityCommandDto(dto, "Средний")))
                    .build());

            productFormButtons.add(InlineKeyboardButton.builder()
                    .text(currentForm.equals("Мелкий") ? "Мелкий ✅" : "Мелкий")
                    .callbackData(serializer.serialize(new SetProductQuantityCommandDto(dto, "Мелкий")))
                    .build());

            keyboardRows.add(productFormButtons);
        }

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
