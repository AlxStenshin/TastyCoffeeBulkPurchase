package ru.alxstn.tastycoffeebulkpurchase.handler.update.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.alxstn.tastycoffeebulkpurchase.entity.Customer;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.SetOrderPaidCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.serialize.DtoDeserializer;
import ru.alxstn.tastycoffeebulkpurchase.event.SendMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.handler.update.CallbackUpdateHandler;
import ru.alxstn.tastycoffeebulkpurchase.repository.CustomerRepository;
import ru.alxstn.tastycoffeebulkpurchase.repository.PurchaseRepository;
import ru.alxstn.tastycoffeebulkpurchase.service.SessionManagerService;


@Component
public class SetOrderPaidStatusUpdateHandler extends CallbackUpdateHandler<SetOrderPaidCommandDto> {

    Logger logger = LogManager.getLogger(SetOrderPaidStatusUpdateHandler.class);
    private final ApplicationEventPublisher publisher;
    private final SessionManagerService sessionManager;
    private final PurchaseRepository purchaseRepository;
    private final CustomerRepository customerRepository;


    @Autowired
    private DtoDeserializer deserializer;

    public SetOrderPaidStatusUpdateHandler(ApplicationEventPublisher publisher,
                                           SessionManagerService sessionManager,
                                           PurchaseRepository purchaseRepository,
                                           CustomerRepository customerRepository) {
        this.publisher = publisher;
        this.sessionManager = sessionManager;
        this.purchaseRepository = purchaseRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    protected Class<SetOrderPaidCommandDto> getDtoType() {
        return SetOrderPaidCommandDto.class;
    }

    @Override
    protected SerializableInlineType getSerializableType() {
        return SerializableInlineType.PAYMENT_CONFIRMATION;
    }

    @Override
    protected void handleCallback(Update update, SetOrderPaidCommandDto dto) {
        Session currentSession = dto.getSession();
        Long eventEmitterId = update.getCallbackQuery().getMessage().getChatId();
        logger.info("Command received: Payment Confirmation from user " +
                eventEmitterId + ", session Id: " + currentSession.getId());

        Customer eventEmitter = customerRepository.getByChatId(eventEmitterId);
        currentSession.setCompletePaymentsCount(currentSession.getCompletePaymentsCount() + 1);
        sessionManager.saveSession(currentSession);

        for (Customer c : purchaseRepository.getSessionCustomers(currentSession)) {
            publisher.publishEvent(new SendMessageEvent(this, SendMessage.builder()
                    .text("Пользователь " + eventEmitter + " оплатил свой заказ.\n" +
                            "Оплачено заказов: " + currentSession.getCompletePaymentsCount() +
                            " из " + currentSession.getCustomersCount())
                    .chatId(c.getChatId().toString())
                    .build()));
        }
    }
}
