package ru.alxstn.tastycoffeebulkpurchase.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.alxstn.tastycoffeebulkpurchase.entity.Customer;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.entity.Purchase;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.event.ProductUpdateEvent;
import ru.alxstn.tastycoffeebulkpurchase.event.SendMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.exception.session.SessionNotFoundException;
import ru.alxstn.tastycoffeebulkpurchase.repository.PurchaseRepository;
import ru.alxstn.tastycoffeebulkpurchase.repository.SessionRepository;

import java.util.List;

@Service
public class BasicProductChangedCustomerNotifierService implements ProductChangedCustomerNotifierService{

    Logger logger = LogManager.getLogger(BasicProductChangedCustomerNotifierService.class);
    private final SessionRepository sessionRepository;
    private final PurchaseRepository purchaseRepository;
    private final ApplicationEventPublisher publisher;

    public BasicProductChangedCustomerNotifierService(SessionRepository sessionRepository,
                                                      PurchaseRepository purchaseRepository,
                                                      ApplicationEventPublisher publisher) {
        this.sessionRepository = sessionRepository;
        this.purchaseRepository = purchaseRepository;
        this.publisher = publisher;
    }

    @Override
    public void handleChangedProducts(ProductUpdateEvent event) {
        Product oldProduct = event.getOldProduct();

        Session currentSession = sessionRepository.getActiveSession().orElseThrow(SessionNotFoundException::new);
        List<Purchase> currentSessionProductPurchases = purchaseRepository.
                findProductPurchasesInSession(currentSession, oldProduct);

        for (Purchase purchase : currentSessionProductPurchases) {
            Customer customer = purchase.getCustomer();

            logger.info("Sending Product Changed Notification to " + purchase.getCustomer());
            publisher.publishEvent(new SendMessageEvent(this,
                    SendMessage.builder()
                            .chatId(customer.getChatId())
                            .text("Обновился прайс-лист.\n" +
                                    "Продукт из вашего заказа более не доступен:\n" +
                                    oldProduct.getFullDisplayName()
                            + "\nПожалуйста выберите замену.")
                            .build()));
        }
    }
}
