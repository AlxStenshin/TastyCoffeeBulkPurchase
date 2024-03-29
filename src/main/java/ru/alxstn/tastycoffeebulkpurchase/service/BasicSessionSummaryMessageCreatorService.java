package ru.alxstn.tastycoffeebulkpurchase.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.PaymentManagerService;

import java.math.BigDecimal;
import java.text.DecimalFormat;
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

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        DecimalFormat decimalFormat = new DecimalFormat("#.00");

        return (session.isClosed() ? "\n<b>! Сессия закрыта. Заказы не принимаются. !</b>\n" : "") +
                "<code>" +
                "\nНаименование: " + session.getTitle() +
                "\nТекущая скидка: " + session.getDiscountPercentage() + "%\n" +
                "\nКофе в закупке: " + decimalFormat.format(session.getCoffeeWeight()) + "кг." +
                "\nЧая в закупке: " + decimalFormat.format(session.getTeaWeight()) + "кг." + "\n" +
                "\nДата открытия: " + session.getDateTimeOpened().format(dateFormatter) +
                "\nДата закрытия: " + session.getDateTimeClosed().format(dateFormatter) +
                "\nОплата: " + session.getPaymentInstruction() + "\n" +
                "\nОбщая стоимость без скидки: " +  paymentManagerService.getSessionTotalPrice(session).orElse(new BigDecimal(0)) +  "₽" +
                "\nОбщая стоимость со скидкой: " + paymentManagerService.getSessionTotalPriceWithDiscount(session).orElse(new BigDecimal(0)) + "₽" +
                "\nКоличество участников: " + paymentManagerService.getSessionCustomersCount(session).orElse(0) +
                "\nОплачено заказов: " + paymentManagerService.getCompletePaymentsCount(session).orElse(0) +
                "\nНа сумму: " +  paymentManagerService.getSessionTotalPaidAmount(session).orElse(new BigDecimal(0)) + "₽" +
                "\nОсталось оплатить: " + paymentManagerService.getSessionTotalUnpaidAmount(session).orElse(new BigDecimal(0)) + "₽" +
                "</code>";
    }
}
