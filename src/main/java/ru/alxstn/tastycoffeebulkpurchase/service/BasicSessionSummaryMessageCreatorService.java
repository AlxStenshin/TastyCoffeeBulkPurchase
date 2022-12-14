package ru.alxstn.tastycoffeebulkpurchase.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.PaymentManagerService;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Service
public class BasicSessionSummaryMessageCreatorService implements SessionSummaryMessageCreatorService {

    Logger logger = LogManager.getLogger(BasicSessionSummaryMessageCreatorService.class);
    private final PaymentManagerService paymentManagerService;

    public BasicSessionSummaryMessageCreatorService(PaymentManagerService paymentManagerService) {
        this.paymentManagerService = paymentManagerService;
    }

    @Override
    public String createSessionSummaryMessage(Session session) {
        logger.info("Building session summary.");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return (session.isClosed() ? "\n<b>! Сессия закрыта. Заказы не принимаются. !</b>\n" : "") +
                "<code>" +
                "\nТекущая скидка: " + session.getDiscountPercentage() + "%" +
                "\nТоваров по скидке в закупке: " + session.getDiscountableWeight() + "кг." +
                "\nКофе в закупке: " + session.getCoffeeWeight() + "кг." +
                "\nЧая в закупке: " + session.getTeaWeight() + "кг." + "\n" +
                "\nДата открытия: " + session.getDateTimeOpened().format(formatter) +
                "\nДата закрытия: " + session.getDateTimeClosed().format(formatter) +
                "\nОплата: " + session.getPaymentInstruction() + "\n" +
                "\nОбщая стоимость без скидки: " +  paymentManagerService.getSessionTotalPrice(session).orElse(new BigDecimal(0)) +  "₽" +
                "\nОбщая стоимость со скидкой: " + paymentManagerService.getSessionTotalPriceWithDiscount(session).orElse(new BigDecimal(0)) + "₽" +
                "\nКоличество участников: " + paymentManagerService.getSessionCustomersCount(session).orElse(0) +
                "\nОплачено заказов: " + paymentManagerService.getCompletePaymentsCount(session).orElse(0) +
                "\nНа сумму: " +  paymentManagerService.getSessionTotalPaidAmount(session).orElse(new BigDecimal(0)) + "₽" +
                "\nОстлась оплатить: " +paymentManagerService.getSessionTotalUnpaidAmount(session).orElse(new BigDecimal(0)) + "₽" +
                "</code>";
    }
}
