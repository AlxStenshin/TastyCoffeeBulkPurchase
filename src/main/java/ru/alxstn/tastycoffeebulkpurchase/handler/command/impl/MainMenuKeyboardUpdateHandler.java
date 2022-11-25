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
import ru.alxstn.tastycoffeebulkpurchase.exception.SessionNotFoundException;
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

                // ToDo: Separate DTO: PlaceOrder (Purchase)
                case PLACE_ORDER:
                    try {
                        Session currentSession = sessionManager.getCurrentSession();
                        if (!currentSession.isClosed()) {
                            MenuNavigationBotMessage<String> placeOrderAnswer = new MenuNavigationBotMessage<>(update);
                            placeOrderAnswer.setTitle("Выберите категорию: ");
                            placeOrderAnswer.setDataSource(productRepository.findAllActiveCategories());
                            placeOrderAnswer.setButtonCreator(s -> InlineKeyboardButton.builder()
                                    .text(s)
                                    .callbackData(serializer.serialize(new SetProductCategoryCommandDto(s,
                                            new PlaceOrderCommandDto("PlaceOrder"))))
                                    .build());

                            publisher.publishEvent(new SendMessageEvent(this, placeOrderAnswer.newMessage()));
                        }
                    } catch (SessionNotFoundException e) {
                        sendNoActiveSessionFoundMessage(update);
                    }
                    break;

                // ToDo: Separate DTO: EditOrder (Purchase)
                case EDIT_ORDER:
                    try {
                        Session currentSession = sessionManager.getCurrentSession();
                        if (!currentSession.isClosed()) {
                            Customer customer = customerRepository.getByChatId(Long.parseLong(chatId));
                            List<Purchase> purchases = purchaseRepository
                                    .findAllPurchasesInSessionByCustomer(currentSession, customer);
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
                    } catch (SessionNotFoundException e) {
                        sendNoActiveSessionFoundMessage(update);
                    }
                    break;


                case SETTING:
                    List<List<InlineKeyboardButton>> settingsButtons = new ArrayList<>();
                    CustomerNotificationSettings customerSettings = customerRepository
                            .getByChatId(message.getChatId())
                            .getNotificationSettings();

                    settingsButtons.add(Collections.singletonList(InlineKeyboardButton.builder()
                            .text("Настройки уведомлений")
                            .callbackData(serializer.serialize(
                                    new SetCustomerNotificationSettingsDto(customerSettings)))
                            .build()));

                    publisher.publishEvent(new SendMessageEvent(this,
                            SendMessage.builder()
                                    .text("Выбрерите категорию")
                                    .chatId(message.getChatId().toString())
                                    .replyMarkup(InlineKeyboardMarkup.builder().keyboard(settingsButtons).build())
                                    .build()));
                    break;

                case INFORMATION:
                    List<List<InlineKeyboardButton>> informationButtons = new ArrayList<>();

                    informationButtons.add(Collections.singletonList(InlineKeyboardButton.builder()
                            .text("Скачать PriceList")
                            .url("https://storage.yandexcloud.net/coffee-opt-static/media/TastyCoffee_pricelist.pdf")
                            .build()));

                    informationButtons.add(Collections.singletonList(InlineKeyboardButton.builder()
                            .text("Информация о текущей сессии")
                            .callbackData(serializer.serialize(new RequestSessionSummaryCommandDto()))
                            .build()));

                    informationButtons.add(Collections.singletonList(InlineKeyboardButton.builder()
                            .text("Информация о заказе")
                            .callbackData(serializer.serialize(new RequestCustomerPurchaseSummaryCommandDto()))
                            .build()));

                    informationButtons.add(Collections.singletonList(InlineKeyboardButton.builder()
                            .text("Закрыть")
                            // ToDo: Pass correct message id to delete (this)
                            .callbackData(serializer.serialize(new RemoveMessageCommandDto(
                                    update.getMessage().getMessageId(),
                                    update.getMessage().getChatId())))
                            .build()));

                    publisher.publishEvent(new SendMessageEvent(this,
                            SendMessage.builder()
                                    .text("Выбрерите категорию")
                                    .chatId(message.getChatId().toString())
                                    .replyMarkup(InlineKeyboardMarkup.builder().keyboard(informationButtons).build())
                                    .build()));
                    break;
            }
            return true;
        }
        return false;
    }

    void sendNoActiveSessionFoundMessage(Update update) {
        publisher.publishEvent(new SendMessageEvent(this, SendMessage.builder()
                // ToDo: add polling
                .text(sessionManager.getSessionNotFoundMessage())
                .chatId(update.getMessage().getChatId().toString())
                .build()));
    }

    @Override
    public UpdateHandlerStage getStage() {
        return UpdateHandlerStage.REPLY_BUTTON;
    }

}
