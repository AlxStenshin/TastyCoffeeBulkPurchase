package ru.alxstn.tastycoffeebulkpurchase.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.alxstn.tastycoffeebulkpurchase.entity.*;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.RemoveProductFromCustomerPurchaseCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.ReplaceProductForCustomerPurchaseCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.serialize.DtoSerializer;
import ru.alxstn.tastycoffeebulkpurchase.event.ProductUpdateEvent;
import ru.alxstn.tastycoffeebulkpurchase.event.SendMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.exception.session.SessionNotFoundException;
import ru.alxstn.tastycoffeebulkpurchase.repository.SessionRepository;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.PurchaseManagerService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class BasicProductChangedCustomerNotifierService implements ProductChangedCustomerNotifierService{

    Logger logger = LogManager.getLogger(BasicProductChangedCustomerNotifierService.class);
    private final DtoSerializer serializer;
    private final SessionRepository sessionRepository;
    private final PurchaseManagerService purchaseManagerService;
    private final ApplicationEventPublisher publisher;

    public BasicProductChangedCustomerNotifierService(DtoSerializer serializer, SessionRepository sessionRepository,
                                                      PurchaseManagerService purchaseManagerService,
                                                      ApplicationEventPublisher publisher) {
        this.serializer = serializer;
        this.sessionRepository = sessionRepository;
        this.purchaseManagerService = purchaseManagerService;
        this.publisher = publisher;
    }

    @Override
    public void handleChangedProducts(ProductUpdateEvent event) {
        Product oldProduct = event.getOldProduct();
        Product newProduct = event.getNewProduct();

        Session currentSession = sessionRepository.getActiveSession().orElseThrow(SessionNotFoundException::new);
        List<Purchase> currentSessionProductPurchases = purchaseManagerService.
                findProductPurchasesInSession(currentSession, oldProduct);

        for (Purchase purchase : currentSessionProductPurchases) {
            Customer customer = purchase.getCustomer();

            logger.info("Sending Product Changed Notification to " + purchase.getCustomer());
            publisher.publishEvent(new SendMessageEvent(this,
                    SendMessage.builder()
                            .chatId(customer.getChatId())
                            .parseMode("html")
                            .text("Обновился прайс-лист." + event.getUpdateMessage())
                            .replyMarkup(InlineKeyboardMarkup.builder()
                                    .keyboard(buildActionButtons(oldProduct, newProduct))
                                    .build())
                            .build()));
        }
    }

    private List<List<InlineKeyboardButton>> buildActionButtons(Product oldProduct, Product newProduct) {

        List<List<InlineKeyboardButton>> actionButtons = new ArrayList<>();

        actionButtons.add(Collections.singletonList(InlineKeyboardButton.builder()
                .text("Удалить")
                .callbackData(serializer.serialize(
                        new RemoveProductFromCustomerPurchaseCommandDto(oldProduct)))
                .build()));

        actionButtons.add(Collections.singletonList(InlineKeyboardButton.builder()
                .text("Сохранить")
                .callbackData(serializer.serialize(
                        new ReplaceProductForCustomerPurchaseCommandDto(oldProduct, newProduct)))
                .build()));

        return actionButtons;
    }
}
