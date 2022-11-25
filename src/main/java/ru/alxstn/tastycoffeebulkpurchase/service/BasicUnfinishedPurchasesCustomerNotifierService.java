package ru.alxstn.tastycoffeebulkpurchase.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.alxstn.tastycoffeebulkpurchase.entity.Customer;
import ru.alxstn.tastycoffeebulkpurchase.entity.Purchase;
import ru.alxstn.tastycoffeebulkpurchase.event.PurchasePlacementErrorEvent;
import ru.alxstn.tastycoffeebulkpurchase.event.SendMessageEvent;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BasicUnfinishedPurchasesCustomerNotifierService implements UnfinishedPurchasesCoustomerNotifierService {
    Logger logger = LogManager.getLogger(BasicUnfinishedPurchasesCustomerNotifierService.class);

    private final ApplicationEventPublisher publisher;

    public BasicUnfinishedPurchasesCustomerNotifierService(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @EventListener
    @Override
    public void handleUnfinishedPurchases(final PurchasePlacementErrorEvent event) {
        List<Purchase> unfinishedPurchases = event.getUnsuccessfulPurchases();
        if (unfinishedPurchases.size() > 0) {
            logger.warn("Failed to Complete Some Purchases: " + unfinishedPurchases);

            Set<Customer> notificationReceivers = unfinishedPurchases.stream()
                    .map(Purchase::getCustomer)
                    .collect(Collectors.toSet());

            for (Customer customer : notificationReceivers) {
                List<Purchase> customerPurchases = unfinishedPurchases.stream()
                        .filter(p -> p.getCustomer() == customer)
                        .collect(Collectors.toList());

                StringBuilder notificationMessage = new StringBuilder("Не удалось обработать позиции из вашего заказа:\n\n");
                for (Purchase p : customerPurchases) {
                    notificationMessage.append(p.getProduct().getFullDisplayName());
                    notificationMessage.append("\n");
                    notificationMessage.append(p.getProductCountAndTotalPrice());
                    notificationMessage.append("\n");
                }
                notificationMessage.append("\nПожалуйста обратитесь к администратору сессии.");

                publisher.publishEvent(new SendMessageEvent(this,
                        SendMessage.builder()
                                .chatId(customer.getChatId())
                                .text(notificationMessage.toString()).build()));
            }
        }
    }
}
