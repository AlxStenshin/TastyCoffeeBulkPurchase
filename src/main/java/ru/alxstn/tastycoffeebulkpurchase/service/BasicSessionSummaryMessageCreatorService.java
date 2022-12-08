package ru.alxstn.tastycoffeebulkpurchase.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.repository.PaymentRepository;

import java.math.BigDecimal;

@Service
public class BasicSessionSummaryMessageCreatorService implements SessionSummaryMessageCreatorService {

    Logger logger = LogManager.getLogger(BasicSessionSummaryMessageCreatorService.class);
    private final PaymentRepository paymentRepository;

    public BasicSessionSummaryMessageCreatorService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public String createSessionSummaryMessage(Session session) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append(session.isClosed() ? "\n<b>! Сессия закрыта. Заказы не принимаются. !</b>\n" : "");
        messageBuilder.append("<code>");
        messageBuilder.append("\nТекущая скидка: ").append(session.getDiscountPercentage()).append("%");
        messageBuilder.append("\nТоваров по скидке в закупке: ").append(session.getDiscountableWeight()).append("кг.");
        messageBuilder.append("\nКофе в закупке: ").append(session.getCoffeeWeight()).append("кг.");
        messageBuilder.append("\nЧая в закупке: ").append(session.getTeaWeight()).append("кг.");
        messageBuilder.append("\n");
        // ToDo: DateTime Formatter For Date Fields:
        messageBuilder.append("\nДата открытия: ").append(session.getDateTimeOpened());
        messageBuilder.append("\nДата закрытия: ").append(session.getDateTimeClosed());
        messageBuilder.append("\nОплата: ").append(session.getPaymentInstruction());
        messageBuilder.append("\n");
        messageBuilder.append("\nОбщая стоимость без скидки: ").append(
                paymentRepository.getSessionTotalPrice(session).orElse(new BigDecimal(0)))
                .append("₽");

        messageBuilder.append("\nОбщая стоимость со скидкой: ").append(
                paymentRepository.getSessionTotalPriceWithDiscount(session).orElse(new BigDecimal(0)))
                .append("₽");

        messageBuilder.append("\nКоличество участников: ").append(
                paymentRepository.getSessionCustomersCount(session).orElse(0));

        messageBuilder.append("\nОплачено заказов: ").append(
                paymentRepository.getCompletePaymentsCount(session).orElse(0));

        messageBuilder.append("\nНа сумму: ").append(
                paymentRepository.getSessionTotalPaidAmount(session).orElse(new BigDecimal(0)))
                .append("₽");

        messageBuilder.append("\nОстлась оплатить: ").append(
                paymentRepository.getSessionTotalUnpaidAmount(session).orElse(new BigDecimal(0)))
                .append("₽");

        messageBuilder.append("</code>");
        return messageBuilder.toString();
    }
}
