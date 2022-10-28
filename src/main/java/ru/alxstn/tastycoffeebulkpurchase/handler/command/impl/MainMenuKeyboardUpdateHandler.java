package ru.alxstn.tastycoffeebulkpurchase.handler.command.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.alxstn.tastycoffeebulkpurchase.bot.MainMenuKeyboard;
import ru.alxstn.tastycoffeebulkpurchase.bot.MenuNavigationBotMessage;
import ru.alxstn.tastycoffeebulkpurchase.entity.Customer;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.entity.Purchase;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.*;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.serialize.DtoSerializer;
import ru.alxstn.tastycoffeebulkpurchase.event.SendMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.handler.UpdateHandler;
import ru.alxstn.tastycoffeebulkpurchase.handler.update.UpdateHandlerStage;
import ru.alxstn.tastycoffeebulkpurchase.repository.CustomerRepository;
import ru.alxstn.tastycoffeebulkpurchase.repository.ProductRepository;
import ru.alxstn.tastycoffeebulkpurchase.repository.PurchaseRepository;
import ru.alxstn.tastycoffeebulkpurchase.repository.SessionRepository;
import ru.alxstn.tastycoffeebulkpurchase.util.DateTimeProvider;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Component
public class MainMenuKeyboardUpdateHandler implements UpdateHandler {

    Logger logger = LogManager.getLogger(MainMenuKeyboardUpdateHandler.class);
    private final ApplicationEventPublisher publisher;
    private final ProductRepository productRepository;
    private final PurchaseRepository purchaseRepository;
    private final SessionRepository sessionRepository;
    private final CustomerRepository customerRepository;
    private final DateTimeProvider dateTimeProvider;
    private final DtoSerializer serializer;

    public MainMenuKeyboardUpdateHandler(ApplicationEventPublisher publisher,
                                         ProductRepository productRepository,
                                         PurchaseRepository purchaseRepository,
                                         SessionRepository sessionRepository,
                                         CustomerRepository customerRepository,
                                         DateTimeProvider dateTimeProvider,
                                         DtoSerializer serializer) {
        this.publisher = publisher;
        this.productRepository = productRepository;
        this.purchaseRepository = purchaseRepository;
        this.sessionRepository = sessionRepository;
        this.customerRepository = customerRepository;
        this.dateTimeProvider = dateTimeProvider;
        this.serializer = serializer;
    }

    @Override
    public boolean handleUpdate(Update update) {
        Message message = update.getMessage();
        logger.info("Main Menu Button Click Command Received: " + message);

        if (!update.hasMessage()) {
            return false;
        }

        if (!message.hasText()) {
            return false;
        }

        String messageText = message.getText();
        Optional<MainMenuKeyboard> keyboardButton = MainMenuKeyboard.parse(messageText);
        if (keyboardButton.isPresent()) {
            String chatId = message.getChatId().toString();

            switch (keyboardButton.get()) {

                case PLACE_ORDER:
                    MenuNavigationBotMessage<String> placeOrderAnswer = new MenuNavigationBotMessage<>(update);
                    placeOrderAnswer.setTitle("Выберите категорию: ");
                    placeOrderAnswer.setDataSource(productRepository.findAllActiveCategories());
                    placeOrderAnswer.setButtonCreator(s -> InlineKeyboardButton.builder()
                            .text(s)
                            .callbackData(serializer.serialize(new SetProductCategoryCommandDto(s,
                                    new PlaceOrderCommandDto("PlaceOrder"))))
                            .build());

                    publisher.publishEvent(new SendMessageEvent(this, placeOrderAnswer.newMessage()));
                    break;

                case EDIT_ORDER:
                    Session session = sessionRepository.getCurrentSession(dateTimeProvider.getCurrentTimestamp());
                    Customer customer = customerRepository.getReferenceById(Long.parseLong(chatId));

                    List<Purchase> purchases = purchaseRepository
                            .findAllPurchasesInCurrentSessionByCustomerId(session, customer);

                    if (purchases.size() > 0) {
                        MenuNavigationBotMessage<Purchase> editOrderAnswer = new MenuNavigationBotMessage<>(update);
                        editOrderAnswer.setTitle("Выберите продукт из заказа: ");
                        editOrderAnswer.setDataSource(purchases);

                        Function<Purchase, String> buttonTitleCreator = purchase -> {
                            Product product = purchase.getProduct();
                            String buttonTitle = product.getName();
                            buttonTitle += product.getProductPackage().isEmpty() ? "" : ", " + product.getProductPackage();
                            buttonTitle += purchase.getProductForm().isEmpty() ? "" : ", " + purchase.getProductForm();
                            buttonTitle += " x " + purchase.getCount() + " шт.";
                            return buttonTitle;
                        };

                        editOrderAnswer.setButtonCreator(p -> InlineKeyboardButton.builder()
                                .text(buttonTitleCreator.apply(p))
                                .callbackData(serializer.serialize(new EditPurchaseCommandDto(p)))
                                .build());

                        editOrderAnswer.addAdditionalButtons(List.of(InlineKeyboardButton.builder()
                                .text("Очистить заказ")
                                .callbackData(serializer.serialize(new ClearPurchasesCommandDto(purchases)))
                                .build()));

                        publisher.publishEvent(new SendMessageEvent(this, editOrderAnswer.newMessage()));

                    } else {
                        publisher.publishEvent(new SendMessageEvent(this, SendMessage.builder()
                                .text("Ваш заказ пуст")
                                .chatId(update.getMessage().getChatId().toString())
                                .build()));
                    }
                    break;


                case SETTING:
                    publisher.publishEvent(new SendMessageEvent(this,
                            SendMessage.builder().chatId(chatId).text("Settings Action").build()));
                    break;

                case STATISTIC:
                    publisher.publishEvent(new SendMessageEvent(this,
                            SendMessage.builder().chatId(chatId).text("Statistics Action").build()));
                    break;
            }
            return true;
        }
        return false;
    }

    @Override
    public UpdateHandlerStage getStage() {
        return UpdateHandlerStage.REPLY_BUTTON;
    }

}
