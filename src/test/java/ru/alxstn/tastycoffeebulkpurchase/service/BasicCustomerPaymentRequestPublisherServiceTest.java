package ru.alxstn.tastycoffeebulkpurchase.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit4.SpringRunner;
import ru.alxstn.tastycoffeebulkpurchase.entity.Customer;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.dto.serialize.DtoSerializer;
import ru.alxstn.tastycoffeebulkpurchase.event.ActiveSessionClosedNotificationEvent;
import ru.alxstn.tastycoffeebulkpurchase.event.bot.SendMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.PurchaseManagerService;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class BasicCustomerPaymentRequestPublisherServiceTest {

    @Mock
    ApplicationEventPublisher publisher;

    @Mock
    BasicCustomerSummaryMessageCreatorService messageCreatorService;

    @Mock
    PurchaseManagerService purchaseManagerService;

    @Autowired
    DtoSerializer serializer;

    BasicCustomerPaymentRequestPublisherService publisherService;

    @BeforeEach
    void init() {
        publisherService = new BasicCustomerPaymentRequestPublisherService(
                publisher, serializer, purchaseManagerService, messageCreatorService);
    }

    @Test
    void shouldInvokeSendMessageEvent() {
        Session session = new Session();
        Customer customer = new Customer();
        customer.setChatId(1L);

        when(messageCreatorService.buildCustomerSummaryMessage(session, customer))
                .thenReturn("message");

        when(purchaseManagerService.getSessionCustomers(session))
                .thenReturn(List.of(customer));

        publisherService.createAndPublishPaymentRequest(
                new ActiveSessionClosedNotificationEvent(this, session));
        ArgumentCaptor<ApplicationEvent> argumentCaptor = ArgumentCaptor.forClass(ApplicationEvent.class);

        doAnswer(invocation -> {
            ApplicationEvent value = argumentCaptor.getValue();
            assertTrue(value instanceof SendMessageEvent);
            return null;
        }).when(publisher).publishEvent(argumentCaptor.capture());

        verify(publisher, times(1)).publishEvent(isA(SendMessageEvent.class));
    }

}