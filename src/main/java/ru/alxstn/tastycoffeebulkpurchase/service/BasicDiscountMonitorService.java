package ru.alxstn.tastycoffeebulkpurchase.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.alxstn.tastycoffeebulkpurchase.entity.Settings;
import ru.alxstn.tastycoffeebulkpurchase.event.DiscountCheckRequestEvent;
import ru.alxstn.tastycoffeebulkpurchase.event.SendMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.repository.CustomerRepository;
import ru.alxstn.tastycoffeebulkpurchase.repository.PurchaseRepository;
import ru.alxstn.tastycoffeebulkpurchase.repository.SessionRepository;
import ru.alxstn.tastycoffeebulkpurchase.repository.SettingsRepository;

import java.util.TreeMap;

@Component
public class BasicDiscountMonitorService implements DiscountMonitorService {

    Logger logger = LogManager.getLogger(DiscountMonitorService.class);
    private final ApplicationEventPublisher publisher;
    private final PurchaseRepository purchaseRepository;
    private final CustomerRepository customerRepository;
    private final SettingsRepository settingsRepository;
    private final SessionRepository sessionRepository;
    private final TreeMap<Integer, Integer> discounts;

    public BasicDiscountMonitorService(ApplicationEventPublisher publisher,
                                       PurchaseRepository purchaseRepository,
                                       SessionRepository sessionRepository,
                                       CustomerRepository customerRepository,
                                       SettingsRepository settingsRepository) {
        this.publisher = publisher;
        this.purchaseRepository = purchaseRepository;
        this.sessionRepository = sessionRepository;
        this.customerRepository = customerRepository;
        this.settingsRepository = settingsRepository;

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
        int newDiscount = discounts.get(discounts.floorKey(currentSessionDiscountSensitiveWeight.intValue()));
        logger.debug("Discount value: " + newDiscount + " Purchase Weight: " + currentSessionDiscountSensitiveWeight);
        int previousDiscount = sessionRepository.getCurrentSessionDiscountValue();


        if (previousDiscount!= newDiscount) {
            sessionRepository.setCurrentSessionDiscountValue(newDiscount);
            logger.info("Current Session Discount Changed. Previous value: " +
                    previousDiscount + " New Value: " + newDiscount);

            for (Settings settings : settingsRepository.findDiscountNotificationSubscribedUsers()) {
                publisher.publishEvent(new SendMessageEvent(this,
                        SendMessage.builder()
                                .chatId(settings.getId())
                                .text("Изменился размер скидки на текущий заказ. " +
                                        "\nБыло: " + previousDiscount + "%" +
                                        "\nСтало: " + newDiscount + "%")
                                .build()));
            }
        }
    }

}
