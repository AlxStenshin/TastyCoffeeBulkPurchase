package ru.alxstn.tastycoffeebulkpurchase.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import ru.alxstn.tastycoffeebulkpurchase.entity.Customer;
import ru.alxstn.tastycoffeebulkpurchase.entity.Purchase;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.repository.PurchaseRepository;
import ru.alxstn.tastycoffeebulkpurchase.util.BigDecimalUtil;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BasicCustomerSummaryCreatorService implements CustomerSummaryCreatorService {

    Logger logger = LogManager.getLogger(BasicCustomerSummaryCreatorService.class);
    private final PurchaseRepository purchaseRepository;

    public BasicCustomerSummaryCreatorService(PurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    @Override
    public String buildCustomerSummary(Customer customer, Session session) {
        List<Purchase> currentSessionCustomerPurchases = purchaseRepository
                .findAllPurchasesInSessionByCustomer(session, customer);
        return buildMessage(currentSessionCustomerPurchases, session.getDiscountPercentage());
    }

    private String buildMessage(List<Purchase> purchases, int discountValue) {
        BigDecimal totalPrice = new BigDecimal(0);
        BigDecimal discountableTotal = new BigDecimal(0);
        BigDecimal nonDiscountableTotal = new BigDecimal(0);
        String message;

        if (purchases.size() > 0) {
            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append("Ваш заказ:\n");
            messageBuilder.append("<code>");

            for (var purchase : purchases) {
                totalPrice = totalPrice.add(purchase.getTotalPrice());

                if (purchase.getProduct().isDiscountable()) {
                    discountableTotal = discountableTotal.add(purchase.getTotalPrice());
                } else {
                    nonDiscountableTotal = nonDiscountableTotal.add(purchase.getTotalPrice());
                }

                messageBuilder.append(purchase.getPurchaseSummary());
                messageBuilder.append("\n");
            }

            BigDecimal discountableTotalWithDiscount = BigDecimalUtil.multiplyByDouble(discountableTotal, ((double) (100 - discountValue) / 100));
            BigDecimal totalPriceWithDiscount = discountableTotalWithDiscount.add(nonDiscountableTotal);

            messageBuilder.append("</code>");
            messageBuilder.append("\nИтог без скидки: ").append(totalPrice).append("₽");
            messageBuilder.append(BigDecimalUtil.greaterThanZero(discountableTotal) ? "\nИз ни скидка применяется к  " + discountableTotal + "₽" : "");
            messageBuilder.append(discountValue > 0 ? "\nТовары по скидке со скидкой " + discountValue + "%: " + discountableTotalWithDiscount + "₽" : "");
            messageBuilder.append(BigDecimalUtil.greaterThanZero(nonDiscountableTotal) ? "\nДругие товары: " + nonDiscountableTotal + "₽" : "");
            // ToDo: Calculate Discount Value
            messageBuilder.append("\n\nВсего к оплате: ").append(totalPriceWithDiscount).append("₽");
            message = messageBuilder.toString();
        }
        else {
            message = "Ваш заказ пуст";
        }
        return message;
    }
}
