package ru.alxstn.tastycoffeebulkpurchase.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.alxstn.tastycoffeebulkpurchase.entity.Customer;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.dto.impl.SetOrderPaidCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.dto.serialize.DtoSerializer;
import ru.alxstn.tastycoffeebulkpurchase.event.ActiveSessionClosedNotificationEvent;
import ru.alxstn.tastycoffeebulkpurchase.event.bot.SendMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.PurchaseManagerService;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
public class BasicCustomerPaymentRequestPublisherService implements CustomerPaymentRequestPublisherService {

    Logger logger = LogManager.getLogger(BasicCustomerPaymentRequestPublisherService.class);
    private final ApplicationEventPublisher publisher;
    private final DtoSerializer serializer;
    private final PurchaseManagerService purchaseManagerService;
    private final CustomerSummaryMessageCreatorService customerSummaryMessageCreatorService;

    public BasicCustomerPaymentRequestPublisherService(ApplicationEventPublisher publisher,
                                                       DtoSerializer serializer,
                                                       PurchaseManagerService purchaseManagerService,
                                                       CustomerSummaryMessageCreatorService customerSummaryMessageCreatorService) {
        this.publisher = publisher;
        this.serializer = serializer;
        this.purchaseManagerService = purchaseManagerService;

        this.customerSummaryMessageCreatorService = customerSummaryMessageCreatorService;
    }

    @EventListener
    @Override
    public void createAndPublishPaymentRequest(ActiveSessionClosedNotificationEvent event) {
        logger.info("Now building and publishing per-customer payment request");

        Session session = event.getSession();
        Set<Customer> currentSessionCustomers = new HashSet<>(purchaseManagerService.getSessionCustomers(session));

        for (Customer c : currentSessionCustomers) {
            String message = "Сессия закрыта!\n\n" +
                    customerSummaryMessageCreatorService.buildCustomerSummaryMessage(session, c) + "\n\n" +
                    "Внесите оплату и нажмите кнопку \"Оплачено\"\n" +
                    "Оплата: " + session.getPaymentInstruction() + "\n";

            publisher.publishEvent(new SendMessageEvent(this,
                    SendMessage.builder()
                            .chatId(c.getChatId())
                            .parseMode("html")
                            .text(message)
                            .replyMarkup(InlineKeyboardMarkup.builder()
                                    .keyboard(Collections.singleton(Collections.singletonList(
                                            buildConfirmationButton(session))))
                                    .build())
                            .build()));
        }
    }

    private InlineKeyboardButton buildConfirmationButton(Session session) {
        return InlineKeyboardButton.builder()
                .text("Оплачено")
                .callbackData(serializer.serialize(new SetOrderPaidCommandDto(session)))
                .build();
    }
}
