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
import ru.alxstn.tastycoffeebulkpurchase.entity.Purchase;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.SetOrderPaidCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.serialize.DtoSerializer;
import ru.alxstn.tastycoffeebulkpurchase.event.PurchaseSummaryNotificationEvent;
import ru.alxstn.tastycoffeebulkpurchase.event.SendMessageEvent;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BasicCustomerPaymentRequestPublisherService implements CustomerPaymentRequestPublisherService {

    Logger logger = LogManager.getLogger(BasicCustomerPaymentRequestPublisherService.class);
    private final ApplicationEventPublisher publisher;
    private final DtoSerializer serializer;
    private final CustomerSummaryMessageCreatorService customerSummaryMessageCreatorService;

    public BasicCustomerPaymentRequestPublisherService(ApplicationEventPublisher publisher,
                                                       DtoSerializer serializer,
                                                       CustomerSummaryMessageCreatorService customerSummaryMessageCreatorService) {
        this.publisher = publisher;
        this.serializer = serializer;
        this.customerSummaryMessageCreatorService = customerSummaryMessageCreatorService;
    }

    @EventListener
    @Override
    public void createAndPublishSummary(PurchaseSummaryNotificationEvent event) {
        logger.info("Now building and publishing per-customer session summary");

        Session session = event.getCurrentSessionPurchases().get(0).getSession();
        List<Purchase> currentSessionPurchases = event.getCurrentSessionPurchases();
        Set<Customer> currentSessionCustomers = currentSessionPurchases.stream()
                .map(Purchase::getCustomer)
                .collect(Collectors.toSet());

        for (Customer c : currentSessionCustomers) {
            String message = customerSummaryMessageCreatorService.buildCustomerSummaryMessage(session, c) + "\n\n" +
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
