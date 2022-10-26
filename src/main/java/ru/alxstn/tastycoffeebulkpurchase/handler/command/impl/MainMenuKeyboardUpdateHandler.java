package ru.alxstn.tastycoffeebulkpurchase.handler.command.impl;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.alxstn.tastycoffeebulkpurchase.bot.MainMenuKeyboard;
import ru.alxstn.tastycoffeebulkpurchase.bot.MenuNavigationBotMessage;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.entity.Purchase;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.PlaceOrderCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.SetProductCategoryCommandDto;
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
import java.util.stream.Collectors;

@Component
public class MainMenuKeyboardUpdateHandler implements UpdateHandler {
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
        if (!update.hasMessage()) {
            return false;
        }

        Message message = update.getMessage();
        if (!message.hasText()) {
            return false;
        }

        String messageText = message.getText();
        Optional<MainMenuKeyboard> keyboardButton = MainMenuKeyboard.parse(messageText);
        if (keyboardButton.isPresent()) {
            String chatId = message.getChatId().toString();
            MenuNavigationBotMessage answer = new MenuNavigationBotMessage(update);
            switch (keyboardButton.get()) {

                case PLACE_ORDER:
                    answer.setTitle("Выберите категорию: ");
                    answer.setDataSource(productRepository.findAllActiveCategories());
                    answer.setButtonCreator(s -> InlineKeyboardButton.builder()
                            .text(s)
                            .callbackData(serializer.serialize(new SetProductCategoryCommandDto(s,
                                    new PlaceOrderCommandDto("PlaceOrder"))))
                            .build());

                    publisher.publishEvent(new SendMessageEvent( this, answer.newMessage()));
                    break;

                case EDIT_ORDER:
                    List<Purchase> purchases = purchaseRepository.findAllPurchasesInCurrentSessionByCustomerId(
                            sessionRepository.getCurrentSession(dateTimeProvider.getCurrentTimestamp()),
                            customerRepository.getReferenceById(Long.parseLong(chatId)));

                    answer.setTitle("Выберите продукт из заказа: ");
                    answer.setDataSource(purchases.stream()
                                    .map(p -> {
                                        Product product = p.getProduct();
                                        return product.getName() + " " +
                                                product.getProductPackage() + " " +
                                                p.getProductForm() + " x " +
                                        p.getCount() + " шт.";
                                    })
                                    .collect(Collectors.toList()));

                    answer.setButtonCreator(s -> InlineKeyboardButton.builder()
                            .text(s)
                            .callbackData(serializer.serialize(
                                    new SetProductCategoryCommandDto(s, new PlaceOrderCommandDto("PlaceOrder"))))
                            .build());

                    publisher.publishEvent(new SendMessageEvent( this, answer.newMessage()));
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
