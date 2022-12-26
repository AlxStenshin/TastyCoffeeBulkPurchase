package ru.alxstn.tastycoffeebulkpurchase.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.alxstn.tastycoffeebulkpurchase.entity.Customer;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.event.NewSessionStartedEvent;
import ru.alxstn.tastycoffeebulkpurchase.event.SendMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.CustomerManagerService;

import java.util.List;

@Service
public class BasicNewSessionStartedNotifierService implements NewSessionStartedNotifierService {

    Logger logger = LogManager.getLogger(BasicNewSessionStartedNotifierService.class);
    private final ApplicationEventPublisher publisher;
    private final CustomerManagerService customerManagerService;

    public BasicNewSessionStartedNotifierService(ApplicationEventPublisher publisher,
                                                 CustomerManagerService customerManagerService) {
        this.publisher = publisher;
        this.customerManagerService = customerManagerService;
    }

    @EventListener
    public void handlePriceList(final NewSessionStartedEvent event) {
        notifySubscribedCustomers(event.getSession());
    }

    @Override
    public void notifySubscribedCustomers(Session session) {
        List<Customer> notificationReceivers = customerManagerService.findAll().stream()
                .filter(c -> c.getNotificationSettings().isReceiveNewSessionStartedNotification())
                .toList();
        logger.info("Sending New Session Opened Message for " + notificationReceivers.size() + " customers");
        String message = "Открыта новая оптовая закупка: " + session.getTitle();

        for (var c : notificationReceivers) {
            logger.info("Sending New Session Opened Message to " + c);
            publisher.publishEvent(new SendMessageEvent(this, SendMessage.builder()
                    .text(message)
                    .chatId(c.getChatId())
                    .build()));
        }
    }
}
