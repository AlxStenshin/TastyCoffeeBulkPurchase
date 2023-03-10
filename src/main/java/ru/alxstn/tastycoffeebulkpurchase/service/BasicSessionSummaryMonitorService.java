package ru.alxstn.tastycoffeebulkpurchase.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.alxstn.tastycoffeebulkpurchase.entity.Customer;
import ru.alxstn.tastycoffeebulkpurchase.entity.Purchase;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.event.CustomerSummaryCheckRequestEvent;
import ru.alxstn.tastycoffeebulkpurchase.event.SessionSummaryCheckRequestEvent;
import ru.alxstn.tastycoffeebulkpurchase.event.bot.SendMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.PurchaseManagerService;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.SessionManagerService;

import java.util.List;
import java.util.TreeMap;

@Service
public class BasicSessionSummaryMonitorService implements SessionSummaryMonitorService {

    Logger logger = LogManager.getLogger(SessionSummaryMonitorService.class);
    private final ApplicationEventPublisher publisher;
    private final PurchaseManagerService purchaseManagerService;
    private final SessionManagerService sessionManagerService;
    private final TreeMap<Integer, Integer> discounts;

    public BasicSessionSummaryMonitorService(ApplicationEventPublisher publisher,
                                             PurchaseManagerService purchaseManagerService,
                                             SessionManagerService sessionManagerService) {
        this.publisher = publisher;
        this.purchaseManagerService = purchaseManagerService;
        this.sessionManagerService = sessionManagerService;

        this.discounts = new TreeMap<>();
        discounts.put(0, 0);
        discounts.put(10, 10);
        discounts.put(25, 20);
        discounts.put(50, 30);
        discounts.put(350, 35);
    }

    @Async
    @EventListener
    public void handleSessionSummaryCheckRequest(SessionSummaryCheckRequestEvent event) {
        logger.info("Checking Session Summary because of purchase " + event.getReason());
        updateSessionSummary();
    }

    @Override
    public void updateSessionSummary() {
        Session currentSession = sessionManagerService.getActiveSession();
        List<Purchase> currentSessionPurchases = purchaseManagerService
                .findAllPurchasesInSession(currentSession).stream()
                .filter(purchase -> purchase.getProduct().isAvailable() && purchase.getProduct().isActual())
                .toList();

        Double currentSessionCoffeeProductsWeight = currentSessionPurchases.stream()
                .filter(purchase -> purchase.getProduct().isWeightableCoffee())
                .map(purchase -> purchase.getCount() * purchase.getProduct().getProductPackage().getWeight())
                .reduce(0d, Double::sum);
        currentSession.setCoffeeWeight(currentSessionCoffeeProductsWeight);

        Double currentSessionTeaProductsWeight = currentSessionPurchases.stream()
                .filter(purchase -> purchase.getProduct().isTea())
                .map(purchase -> purchase.getCount() * purchase.getProduct().getProductPackage().getWeight())
                .reduce(0d, Double::sum);
        currentSession.setTeaWeight(currentSessionTeaProductsWeight);
        sessionManagerService.saveSession(currentSession);

        int newDiscount = discounts.get(discounts.floorKey(
                currentSessionCoffeeProductsWeight.intValue() + currentSessionTeaProductsWeight.intValue()));
        int previousDiscount = currentSession.getDiscountPercentage();

        if (previousDiscount != newDiscount) {
            currentSession.setDiscountPercentage(newDiscount);
            logger.info("Current Session Discount Changed. Previous value: " +
                    previousDiscount + " New Value: " + newDiscount);

            sessionManagerService.saveSession(currentSession);

            List<Customer> currentSessionCustomers = purchaseManagerService.getSessionCustomers(currentSession);
            for (final Customer c : currentSessionCustomers) {
                publisher.publishEvent(new CustomerSummaryCheckRequestEvent(this, c, "Discount Change"));
            }

            List<Customer> currentSessionSubscribedCustomers = currentSessionCustomers
                    .stream()
                    .filter(c -> c.getNotificationSettings().isReceiveDiscountNotification())
                    .toList();

            for (var c : currentSessionSubscribedCustomers) {
                logger.info("Sending New Discount Notification to " + c);
                publisher.publishEvent(new SendMessageEvent(this,
                        SendMessage.builder()
                                .chatId(c.getChatId())
                                .text("Изменился размер скидки на текущий заказ. " +
                                        "\nБыло: " + previousDiscount + "%" +
                                        "\nСтало: " + newDiscount + "%")
                                .build()));
            }
        }
    }
}
