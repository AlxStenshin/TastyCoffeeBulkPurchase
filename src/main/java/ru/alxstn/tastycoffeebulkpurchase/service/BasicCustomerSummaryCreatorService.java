package ru.alxstn.tastycoffeebulkpurchase.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import ru.alxstn.tastycoffeebulkpurchase.entity.Customer;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.entity.Purchase;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.repository.PurchaseRepository;

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
        double totalPrice = 0;
        double discountableTotal = 0;
        double nonDiscountableTotal = 0;
        String message;

        if (purchases.size() > 0) {
            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append("Ваш заказ:\n");
            messageBuilder.append("<code>");

            for (var p : purchases) {
                Product targetProduct = p.getProduct();
                totalPrice += p.getCount() * targetProduct.getPrice();

                if (targetProduct.isDiscountable()) {
                    discountableTotal += p.getCount() * targetProduct.getPrice();
                } else {
                    nonDiscountableTotal += p.getCount() * targetProduct.getPrice();
                }

                messageBuilder.append(p.getPurchaseSummary());
                messageBuilder.append("\n");
            }

            double discountableTotalWithDiscount = discountableTotal * ((double) (100 - discountValue) / 100);
            double totalPriceWithDiscount = discountableTotalWithDiscount + nonDiscountableTotal;

            messageBuilder.append("</code>");
            messageBuilder.append("\nИтог без скидки: ").append(totalPrice).append("₽");
            messageBuilder.append(discountableTotal > 0 ? "\nКофе: " + discountableTotal + "₽" : "");
            messageBuilder.append(discountValue > 0 ? "\nКофе с учетом скидки " + discountValue + "%: " + discountableTotalWithDiscount + "₽" : "");
            messageBuilder.append(nonDiscountableTotal > 0 ? "\nДругие товары: " + nonDiscountableTotal + "₽" : "");
            messageBuilder.append("\n\nВсего к оплате: ").append(totalPriceWithDiscount).append("₽");
            message = messageBuilder.toString();
        }
        else {
            message = "Ваш заказ пуст";
        }
        return message;
    }
}
