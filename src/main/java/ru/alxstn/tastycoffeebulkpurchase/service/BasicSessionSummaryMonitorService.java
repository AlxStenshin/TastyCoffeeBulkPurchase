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
import ru.alxstn.tastycoffeebulkpurchase.event.SessionSummaryCheckRequestEvent;
import ru.alxstn.tastycoffeebulkpurchase.event.SendMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.exception.session.SessionNotFoundException;
import ru.alxstn.tastycoffeebulkpurchase.repository.PurchaseRepository;
import ru.alxstn.tastycoffeebulkpurchase.repository.SessionRepository;

import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class BasicSessionSummaryMonitorService implements SessionSummaryMonitorService {

    Logger logger = LogManager.getLogger(SessionSummaryMonitorService.class);
    private final ApplicationEventPublisher publisher;
    private final PurchaseRepository purchaseRepository;
    private final SessionRepository sessionRepository;
    private final TreeMap<Integer, Integer> discounts;

    public BasicSessionSummaryMonitorService(ApplicationEventPublisher publisher,
                                             PurchaseRepository purchaseRepository,
                                             SessionRepository sessionRepository) {
        this.publisher = publisher;
        this.purchaseRepository = purchaseRepository;
        this.sessionRepository = sessionRepository;

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
        Session currentSession = sessionRepository.getActiveSession().orElseThrow(SessionNotFoundException::new);
        List<Purchase> currentSessionPurchases = purchaseRepository
                .findAllPurchasesInSession(currentSession).stream()
                .filter(purchase -> purchase.getProduct().isAvailable() && purchase.getProduct().isActual())
                .collect(Collectors.toList());

        Double currentSessionDiscountSensitiveWeight = currentSessionPurchases.stream()
                .filter(purchase -> purchase.getProduct().isDiscountable())
                .map(purchase -> purchase.getCount() * purchase.getProduct().getProductPackage().getWeight())
                .reduce(0d, Double::sum);
        sessionRepository.setSessionDiscountableWeight(currentSession,
                currentSessionDiscountSensitiveWeight);

        Double currentSessionCoffeeProductsWeight = currentSessionPurchases.stream()
                .filter(purchase -> purchase.getProduct().isWeightableCoffee())
                .map(purchase -> purchase.getCount() * purchase.getProduct().getProductPackage().getWeight())
                .reduce(0d, Double::sum);
        sessionRepository.setSessionCoffeeWeight(currentSession,
                currentSessionCoffeeProductsWeight);

        Double currentSessionTeaProductsWeight = currentSessionPurchases.stream()
                .filter(purchase -> purchase.getProduct().isTea())
                .map(purchase -> purchase.getCount() * purchase.getProduct().getProductPackage().getWeight())
                .reduce(0d, Double::sum);
        sessionRepository.setSessionTeaWeight(currentSession,
                currentSessionTeaProductsWeight);

        int newDiscount = discounts.get(discounts.floorKey(currentSessionDiscountSensitiveWeight.intValue()));
        int previousDiscount = sessionRepository.getActiveSessionDiscountValue();

        if (previousDiscount != newDiscount) {
            sessionRepository.setSessionDiscountValue(currentSession, newDiscount);
            logger.info("Current Session Discount Changed. Previous value: " +
                    previousDiscount + " New Value: " + newDiscount);

            List<Customer> currentSessionSubscribedCustomers = purchaseRepository.getSessionCustomers(currentSession)
                    .stream()
                    .filter(c -> c.getNotificationSettings().isReceiveDiscountNotification())
                    .collect(Collectors.toList());

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
