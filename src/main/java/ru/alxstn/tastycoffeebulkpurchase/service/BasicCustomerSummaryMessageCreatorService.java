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
                messageBuilder.append("?????? ??????????:\n");
                messageBuilder.append("<code>");

                for (var purchase : availablePurchases) {
                    messageBuilder.append(purchase.getPurchaseSummary());
                    messageBuilder.append("\n");
                }

                List<Purchase> unavailablePurchases = purchases.stream()
                        .filter(purchase -> !purchase.getProduct().isAvailable() || !purchase.getProduct().isActual())
                        .toList();

                if (!unavailablePurchases.isEmpty()) {
                    messageBuilder.append("??? ?????????????????????? ????????????:\n");
                    for (var unavailablePurchase : unavailablePurchases) {
                        messageBuilder.append("???").append(unavailablePurchase.getPurchaseSummary());
                        messageBuilder.append("\n");
                    }
                    messageBuilder.append("???????????????????? ?????????????? ?????? ???????????????? ?????????????????????? ???????????? ???? ???????????? ????????????.");
                }

                messageBuilder.append("</code>");
                if (discountValue > 0) {
                    messageBuilder.append("\n???????? ?????? ????????????: ").append(totalPrice).append("???");
                    messageBuilder.append(BigDecimalUtil.greaterThanZero(discountableTotal) ?
                            "\n?????????????????? ???????????? ?????? ???????????? " + discountableTotal + "???" : "");

                    messageBuilder.append("\n?????????????????? ???????????? ???? ?????????????? ").append(discountValue).append("%: ")
                            .append(discountableTotalWithDiscount).append(" ???");

                    messageBuilder.append(BigDecimalUtil.greaterThanZero(nonDiscountableTotal) ?
                            "\n?????????????????? ????????????: " + nonDiscountableTotal + " ???" : "");

                    messageBuilder.append("\n???????????? ????????????: ")
                            .append(totalPrice.subtract(totalPriceWithDiscount)).append(" ???");
                }
                messageBuilder.append("\n\n<b>?????????? ?? ????????????: ").append(totalPriceWithDiscount).append(" ???</b>");
                message = messageBuilder.toString();
            } else {
                message = "?????? ?????????? ????????";
            }
        } catch (CustomerPaymentException e) {
            message = "?????? ?????????? ????????";
        }
        return message;
    }
}
