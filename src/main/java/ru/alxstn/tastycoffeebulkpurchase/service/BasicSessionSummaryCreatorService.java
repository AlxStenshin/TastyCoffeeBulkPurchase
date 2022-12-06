package ru.alxstn.tastycoffeebulkpurchase.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.repository.PaymentRepository;

@Component
public class BasicSessionSummaryCreatorService implements SessionSummaryCreatorService {

    Logger logger = LogManager.getLogger(BasicSessionSummaryCreatorService.class);
    private final PaymentRepository paymentRepository;

    public BasicSessionSummaryCreatorService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public String createSessionSummary(Session session) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append(session.isClosed() ? "\n<b>! Сессия закрыта для редактирования !</b>\n" : "");
        messageBuilder.append("<code>");
        messageBuilder.append("\nТекущая скидка на кофе: ").append(session.getDiscountPercentage()).append("%");
        messageBuilder.append("\nТоваров со скидкой в закупке: ").append(session.getDiscountableWeight()).append("кг.");
        messageBuilder.append("\nКофе в закупке: ").append(session.getCoffeeWeight()).append("кг.");
        messageBuilder.append("\nЧая в закупке: ").append(session.getTeaWeight()).append("кг.");

        messageBuilder.append("\nДата открытия: ").append(session.getDateTimeOpened());
        messageBuilder.append("\nДата закрытия: ").append(session.getDateTimeClosed());
        messageBuilder.append("\nОплата: ").append(session.getPaymentInstruction());

        //messageBuilder.append("\n\nОбщая стоимость без скидки: ").append(paymentRepository.getSessionTotalAmountPrice(session).orElse(0d));
        //messageBuilder.append("\nОбщая стоимость со скидкой: ").append(getCustomersCount());
        messageBuilder.append("\nКоличество участников: ").append(paymentRepository.getSessionCustomersCount(session).orElse(0));
        messageBuilder.append("\nОплачено заказов: ").append(paymentRepository.getCompletePaymentsCount(session).orElse(0));
        messageBuilder.append("</code>");

        logger.debug(messageBuilder.toString());
        return messageBuilder.toString();
    }
}
