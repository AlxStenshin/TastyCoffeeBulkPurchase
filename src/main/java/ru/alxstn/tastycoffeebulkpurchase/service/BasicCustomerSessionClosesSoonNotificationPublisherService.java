package ru.alxstn.tastycoffeebulkpurchase.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.alxstn.tastycoffeebulkpurchase.entity.Customer;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.event.ActiveSessionClosesSoonNotificationEvent;
import ru.alxstn.tastycoffeebulkpurchase.event.bot.SendMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.PurchaseManagerService;

import java.util.HashSet;
import java.util.Set;

@Service
public class BasicCustomerSessionClosesSoonNotificationPublisherService implements CustomerSessionClosesSoonNotificationPublisherService {

    Logger logger = LogManager.getLogger(BasicCustomerSessionClosesSoonNotificationPublisherService.class);
    private final ApplicationEventPublisher publisher;
    private final PurchaseManagerService purchaseManagerService;

    public BasicCustomerSessionClosesSoonNotificationPublisherService(ApplicationEventPublisher publisher,
                                                                      PurchaseManagerService purchaseManagerService) {
        this.publisher = publisher;
        this.purchaseManagerService = purchaseManagerService;
    }

    @EventListener
    @Override
    public void createAndPublishPaymentRequest(ActiveSessionClosesSoonNotificationEvent event) {
        logger.info("Now building and publishing per-customer session closes notification");

        Session session = event.getSession();
        Set<Customer> currentSessionCustomers = new HashSet<>(purchaseManagerService.getSessionCustomers(session));

        for (Customer c : currentSessionCustomers) {
            String message = """
                    Групповая закупка будет автоматически закрыта через 1 час.

                    Если есть необходимость, внесите изменения в свой заказ сейчас.
                    После закрытия сессии изменить заказ не получится.""";

            publisher.publishEvent(new SendMessageEvent(this,
                    SendMessage.builder()
                            .chatId(c.getChatId())
                            .parseMode("html")
                            .text(message)
                            .build()));
        }
    }
}
