package ru.alxstn.tastycoffeebulkpurchase.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import ru.alxstn.tastycoffeebulkpurchase.entity.Customer;
import ru.alxstn.tastycoffeebulkpurchase.entity.Payment;
import ru.alxstn.tastycoffeebulkpurchase.entity.Purchase;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.exception.payment.CustomerPaymentException;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.PaymentManagerService;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.PurchaseManagerService;
import ru.alxstn.tastycoffeebulkpurchase.util.BigDecimalUtil;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BasicCustomerSummaryMessageCreatorService implements CustomerSummaryMessageCreatorService {

    Logger logger = LogManager.getLogger(BasicCustomerSummaryMessageCreatorService.class);
    private final PurchaseManagerService purchaseManagerService;
    private final PaymentManagerService paymentManagerService;

    public BasicCustomerSummaryMessageCreatorService(PurchaseManagerService purchaseManagerService,
                                                     PaymentManagerService paymentManagerService) {
        this.purchaseManagerService = purchaseManagerService;
        this.paymentManagerService = paymentManagerService;
    }

    @Override
    public String buildCustomerSummaryMessage(Session session, Customer customer) {
        logger.info("Building Customer Summary for " + customer);
        String message;
        try {
            List<Purchase> purchases = purchaseManagerService.findAllPurchasesInSessionByCustomer(session, customer);
            List<Purchase> availablePurchases = purchases.stream()
                    .filter(purchase -> purchase.getProduct().isAvailable() && purchase.getProduct().isActual())
                    .toList();

            Payment payment = paymentManagerService.getCustomerSessionPayment(session, customer)
                    .orElseThrow(() -> new CustomerPaymentException("Payment Not Found!"));

            if (availablePurchases.size() > 0) {
                int discountValue = session.getDiscountPercentage();
                BigDecimal totalPrice = payment.getTotalAmountNoDiscount();
                BigDecimal totalPriceWithDiscount = payment.getTotalAmountWithDiscount();
                BigDecimal discountableTotal = payment.getDiscountableAmountNoDiscount();
                BigDecimal discountableTotalWithDiscount = payment.getDiscountableAmountWithDiscount();
                BigDecimal nonDiscountableTotal = payment.getNonDiscountableAmount();

                StringBuilder messageBuilder = new StringBuilder();
                messageBuilder.append("Ваш заказ:\n");
                messageBuilder.append("<code>");

                for (var purchase : availablePurchases) {
                    messageBuilder.append(purchase.getPurchaseSummary());
                    messageBuilder.append("\n");
                }

                List<Purchase> unavailablePurchases = purchases.stream()
                        .filter(purchase -> !purchase.getProduct().isAvailable() || !purchase.getProduct().isActual())
                        .toList();

                if (!unavailablePurchases.isEmpty()) {
                    messageBuilder.append("⛔ Недоступные товары:\n");
                    for (var unavailablePurchase : unavailablePurchases) {
                        messageBuilder.append("❌").append(unavailablePurchase.getPurchaseSummary());
                        messageBuilder.append("\n");
                    }
                    messageBuilder.append("Пожалуйста удалите или замените недоступные товары из своего заказа.");
                }

                messageBuilder.append("</code>");
                if (discountValue > 0) {
                    messageBuilder.append("\nИтог без скидки: ").append(totalPrice).append("₽");
                    messageBuilder.append(BigDecimalUtil.greaterThanZero(discountableTotal) ?
                            "\nАкционные товары без скидки " + discountableTotal + "₽" : "");

                    messageBuilder.append("\nАкционные товары со скидкой ").append(discountValue).append("%: ")
                            .append(discountableTotalWithDiscount).append(" ₽");

                    messageBuilder.append(BigDecimalUtil.greaterThanZero(nonDiscountableTotal) ?
                            "\nОстальные товары: " + nonDiscountableTotal + " ₽" : "");

                    messageBuilder.append("\nРазмер скидки: ")
                            .append(totalPrice.subtract(totalPriceWithDiscount)).append(" ₽");
                }
                messageBuilder.append("\n\n<b>Всего к оплате: ").append(totalPriceWithDiscount).append(" ₽</b>");
                message = messageBuilder.toString();
            } else {
                message = "Ваш заказ пуст";
            }
        } catch (CustomerPaymentException e) {
            message = "Ваш заказ пуст";
        }
        return message;
    }
}
