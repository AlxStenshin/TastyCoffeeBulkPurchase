package ru.alxstn.tastycoffeebulkpurchase.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit4.SpringRunner;
import ru.alxstn.tastycoffeebulkpurchase.entity.Customer;
import ru.alxstn.tastycoffeebulkpurchase.entity.CustomerNotificationSettings;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.event.SendMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.CustomerManagerService;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@RunWith(SpringRunner.class)
@SpringBootTest
class BasicNewSessionStartedNotifierServiceTest {

    @Mock
    ApplicationEventPublisher publisher;

    @Mock
    CustomerManagerService customerManagerService;

    @InjectMocks
    BasicNewSessionStartedNotifierService notifierService;

    @BeforeEach
    void init() {
        CustomerNotificationSettings defaultSettings = new CustomerNotificationSettings();
        CustomerNotificationSettings unsubscribedCustomerSettings = new CustomerNotificationSettings();
        unsubscribedCustomerSettings.setReceiveNewSessionStartedNotification(false);

        Customer subscribedByDefaultFirstCustomer = new Customer(
                1L,
                "SubscribedCustomer",
                null,
                null);
        subscribedByDefaultFirstCustomer.setNotificationSettings(defaultSettings);


        Customer subscribedByDefaultSecondCustomer = new Customer(
                2L,
                "SubscribedCustomer",
                null,
                null);
        subscribedByDefaultSecondCustomer.setNotificationSettings(defaultSettings);

        Customer unSubscribedCustomer = new Customer(
                3L,
                "UnSubscribedCustomer",
                null,
                null);
        unSubscribedCustomer.setNotificationSettings(unsubscribedCustomerSettings);

        when(customerManagerService.findAll()).thenReturn(List.of(
            subscribedByDefaultFirstCustomer,
                subscribedByDefaultSecondCustomer,
                unSubscribedCustomer));
    }

    @Test
    void shouldInvokeTwoNotificationsMessages() {
        notifierService.notifySubscribedCustomers(new Session());

        ArgumentCaptor<ApplicationEvent> argumentCaptor = ArgumentCaptor.forClass(ApplicationEvent.class);
        doAnswer(invocation -> {
            ApplicationEvent value = argumentCaptor.getValue();
            assertTrue(value instanceof SendMessageEvent);
            return null;
        }).when(publisher).publishEvent(argumentCaptor.capture());

        verify(publisher, times(2)).publishEvent(isA(SendMessageEvent.class));
    }

}