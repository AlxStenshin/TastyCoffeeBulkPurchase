package ru.alxstn.tastycoffeebulkpurchase.handler.command.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.alxstn.tastycoffeebulkpurchase.bot.MainMenuKeyboard;
import ru.alxstn.tastycoffeebulkpurchase.bot.MenuNavigationBotMessage;
import ru.alxstn.tastycoffeebulkpurchase.entity.*;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.*;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.serialize.DtoSerializer;
import ru.alxstn.tastycoffeebulkpurchase.event.SendMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.handler.UpdateHandler;
import ru.alxstn.tastycoffeebulkpurchase.handler.update.UpdateHandlerStage;
import ru.alxstn.tastycoffeebulkpurchase.repository.CustomerRepository;
import ru.alxstn.tastycoffeebulkpurchase.repository.ProductRepository;
import ru.alxstn.tastycoffeebulkpurchase.repository.PurchaseRepository;
import ru.alxstn.tastycoffeebulkpurchase.service.SessionManagerService;

import java.util.*;
import java.util.function.Function;

@Component
public class MainMenuKeyboardUpdateHandler implements UpdateHandler {

    Logger logger = LogManager.getLogger(MainMenuKeyboardUpdateHandler.class);
    private final ApplicationEventPublisher publisher;
    private final ProductRepository productRepository;
    private final PurchaseRepository purchaseRepository;
    private final SessionManagerService sessionManager;
    private final CustomerRepository customerRepository;
    private final DtoSerializer serializer;

    public MainMenuKeyboardUpdateHandler(ApplicationEventPublisher publisher,
                                         ProductRepository productRepository,
                                         PurchaseRepository purchaseRepository,
                                         SessionManagerService sessionManager,
                                         CustomerRepository customerRepository,
                                         DtoSerializer serializer) {
        this.publisher = publisher;
        this.productRepository = productRepository;
        this.purchaseRepository = purchaseRepository;
        this.sessionManager = sessionManager;
        this.customerRepository = customerRepository;
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
                    if (sessionManager.activeSessionAvailable()) {
                        // ToDo: Separate DTO: PlaceOrder (Purchase)
                        MenuNavigationBotMessage<String> placeOrderAnswer = new MenuNavigationBotMessage<>(update);
                        placeOrderAnswer.setTitle("Выберите категорию: ");
                        placeOrderAnswer.setDataSource(productRepository.findAllActiveCategories());
                        placeOrderAnswer.setButtonCreator(s -> InlineKeyboardButton.builder()
                                .text(s)
                                .callbackData(serializer.serialize(new SetProductCategoryCommandDto(s,
                                        new PlaceOrderCommandDto("PlaceOrder"))))
                                .build());

                        publisher.publishEvent(new SendMessageEvent(this, placeOrderAnswer.newMessage()));
                    } else {
                        sendNoActiveSessionFoundMessage(update);
                    }
                    break;

                case EDIT_ORDER:
                    // ToDo: Separate DTO: EditOrder (Purchase)
                    if (sessionManager.activeSessionAvailable()) {
                        Session currentSession = sessionManager.getCurrentSession();
                        Customer customer = customerRepository.getByChatId(Long.parseLong(chatId));

                        List<Purchase> purchases = purchaseRepository
                                .findAllPurchasesInSessionByCustomerId(currentSession, customer);

                        if (purchases.size() > 0) {
                            MenuNavigationBotMessage<Purchase> editOrderAnswer = new MenuNavigationBotMessage<>(update);
                            editOrderAnswer.setTitle("Выберите продукт из заказа: ");
                            editOrderAnswer.setDataSource(purchases);

                            Function<Purchase, String> buttonTitleCreator = purchase -> {
                                Product product = purchase.getProduct();
                                String buttonTitle = product.getName();
                                buttonTitle += product.getProductPackage().getDescription().isEmpty() ? "" : ", " + product.getProductPackage();
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
                    } else {
                        sendNoActiveSessionFoundMessage(update);
                    }
                    break;


                case SETTING:
                    List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
                    CustomerNotificationSettings customerSettings = customerRepository
                            .getByChatId(message.getChatId())
                            .getNotificationSettings();

                    buttons.add(Collections.singletonList(InlineKeyboardButton.builder()
                                            .text("Настройки уведомлений")
                                            .callbackData(serializer.serialize(
                                                    new SetCustomerNotificationSettingsDto(customerSettings)))
                                            .build()));

                    publisher.publishEvent(new SendMessageEvent(this,
                            SendMessage.builder()
                            .text("Выбрерите категорию")
                            .parseMode("html")
                            .chatId(message.getChatId().toString())
                            .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                            .build()));

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

    void sendNoActiveSessionFoundMessage(Update update) {
        publisher.publishEvent(new SendMessageEvent(this, SendMessage.builder()
                .text("Активная сессия не обнаружена. \n" +
                        "Заказы не принимаются.\n" +
                        "Для открытия новой сессии обратитесь к администратору бота или запустите <Голосование>")
                .chatId(update.getMessage().getChatId().toString())
                .build()));
    }

    @Override
    public UpdateHandlerStage getStage() {
        return UpdateHandlerStage.REPLY_BUTTON;
    }

}
