package ru.alxstn.tastycoffeebulkpurchase.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.alxstn.tastycoffeebulkpurchase.entity.Customer;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.event.PurchasePlacementErrorEvent;
import ru.alxstn.tastycoffeebulkpurchase.event.bot.SendMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.model.ProductCaptionBuilder;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.PurchaseManagerService;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.SessionManagerService;

import java.util.List;

@Service
public class BasicUnfinishedPurchasesCustomerNotifierService implements UnfinishedPurchasesCustomerNotifierService {
    Logger logger = LogManager.getLogger(BasicUnfinishedPurchasesCustomerNotifierService.class);

    private final ApplicationEventPublisher publisher;
    private final PurchaseManagerService purchaseManagerService;
    private final SessionManagerService sessionManagerService;

    public BasicUnfinishedPurchasesCustomerNotifierService(ApplicationEventPublisher publisher,
                                                           PurchaseManagerService purchaseManagerService,
                                                           SessionManagerService sessionManagerService) {
        this.publisher = publisher;
        this.purchaseManagerService = purchaseManagerService;
        this.sessionManagerService = sessionManagerService;
    }

    @EventListener
    @Override
    public void handleUnfinishedPurchases(final PurchasePlacementErrorEvent event) {
        List<Product> skippedProducts = event.getUnsuccessfulPurchases();

        if (skippedProducts.size() > 0) {
            logger.warn("Failed to Complete Some Purchases: " + skippedProducts);
            Session unfinishedSession = sessionManagerService.getUnfinishedSession();

            for (var product : skippedProducts) {

                List<Customer> notificationReceivers = purchaseManagerService.getSessionCustomersWithProduct(
                        unfinishedSession, product);

                StringBuilder notificationMessage = new StringBuilder("Не удалось обработать позицию из вашего заказа:\n\n");
                notificationMessage.append(new ProductCaptionBuilder(product)
                        .createIconNameMarkPackagePriceCatSubcatView());
                notificationMessage.append("\n");
                notificationMessage.append("\nПожалуйста обратитесь к администратору сессии.");

                for (var notificationReceiver : notificationReceivers) {
                    logger.info("Message for: " + notificationReceiver + ": " + notificationMessage);

//                    publisher.publishEvent(new SendMessageEvent(this,
//                            SendMessage.builder()
//                                    .chatId(notificationReceiver.getChatId())
//                                    .text(notificationMessage.toString()).build()));
                }
            }
        }
    }
}

