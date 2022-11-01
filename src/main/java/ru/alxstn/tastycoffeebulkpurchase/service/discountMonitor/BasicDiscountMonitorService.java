package ru.alxstn.tastycoffeebulkpurchase.service.discountMonitor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ru.alxstn.tastycoffeebulkpurchase.event.DiscountCheckRequestEvent;
import ru.alxstn.tastycoffeebulkpurchase.repository.PurchaseRepository;
import ru.alxstn.tastycoffeebulkpurchase.repository.SessionRepository;
import ru.alxstn.tastycoffeebulkpurchase.service.DiscountMonitorService;

import java.util.TreeMap;

@Component
public class BasicDiscountMonitorService implements DiscountMonitorService {

    Logger logger = LogManager.getLogger(DiscountMonitorService.class);
    private final ApplicationEventPublisher publisher;
    private final PurchaseRepository purchaseRepository;
    private final SessionRepository sessionRepository;
    private final TreeMap<Integer, Integer> discounts;

    public BasicDiscountMonitorService(ApplicationEventPublisher publisher,
                                       PurchaseRepository purchaseRepository,
                                       SessionRepository sessionRepository) {
        this.publisher = publisher;
        this.purchaseRepository = purchaseRepository;
        this.sessionRepository = sessionRepository;

        this.discounts =  new TreeMap<>();
        discounts.put(0, 0);
        discounts.put(10, 10);
        discounts.put(25, 20);
        discounts.put(50, 30);
        discounts.put(350, 35);
    }

    @Async
    @EventListener
    public void handleDiscountEventRequest(DiscountCheckRequestEvent event) {
        logger.info("Checking Discount Value because of purchase " + event.getReason());
        checkDiscountSize();
    }

    @Override
    public void checkDiscountSize() {
        Double currentSessionDiscountSensitiveWeight = purchaseRepository.getTotalDiscountSensitiveWeightForCurrentSession();
        int discount = discounts.get(discounts.floorKey(currentSessionDiscountSensitiveWeight.intValue()));
        logger.debug("Discount value: " + discount + " Purchase Weight: " + currentSessionDiscountSensitiveWeight);

        if (sessionRepository.getCurrentSessionDiscountValue() != discount) {
            logger.info("Current Session Discount Changed: " + discount);
            sessionRepository.setCurrentSessionDiscountValue(discount);
        }
    }

}
