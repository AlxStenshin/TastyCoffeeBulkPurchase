package ru.alxstn.tastycoffeebulkpurchase.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.alxstn.tastycoffeebulkpurchase.entity.Customer;
import ru.alxstn.tastycoffeebulkpurchase.entity.PurchaseEntry;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.event.PurchasePlacementErrorEvent;
import ru.alxstn.tastycoffeebulkpurchase.event.SendMessageEvent;
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
        List<PurchaseEntry> unfinishedPurchases = event.getUnsuccessfulPurchases();

        if (unfinishedPurchases.size() > 0) {
            logger.warn("Failed to Complete Some Purchases: " + unfinishedPurchases);
            Session unfinishedSession = sessionManagerService.getUnfinishedSession();

            for (var unfinishedPurchase : unfinishedPurchases) {

                List<Customer> notificationReceivers = purchaseManagerService.getSessionCustomersWithProduct(
                        unfinishedSession,
                        unfinishedPurchase.getProduct());

                StringBuilder notificationMessage = new StringBuilder("Не удалось обработать позицию из вашего заказа:\n\n");
                notificationMessage.append(unfinishedPurchase.getProduct().getFullDisplayName());
                notificationMessage.append("\n");
                notificationMessage.append("\nПожалуйста обратитесь к администратору сессии.");

                for (var notificationReceiver : notificationReceivers) {

                publisher.publishEvent(new SendMessageEvent(this,
                        SendMessage.builder()
                                .chatId(notificationReceiver.getChatId())
                        .text(notificationMessage.toString()).build()));
            }
            }
        }
    }
}

