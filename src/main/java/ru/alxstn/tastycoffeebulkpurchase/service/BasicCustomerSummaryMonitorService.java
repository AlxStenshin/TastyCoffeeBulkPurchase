package ru.alxstn.tastycoffeebulkpurchase.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.alxstn.tastycoffeebulkpurchase.entity.Customer;
import ru.alxstn.tastycoffeebulkpurchase.entity.Payment;
import ru.alxstn.tastycoffeebulkpurchase.entity.Purchase;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.event.CustomerSummaryCheckRequestEvent;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.PaymentManagerService;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.PurchaseManagerService;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.SessionManagerService;
import ru.alxstn.tastycoffeebulkpurchase.util.BigDecimalUtil;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BasicCustomerSummaryMonitorService implements CustomerPurchaseSummaryMonitorService {

    Logger logger = LogManager.getLogger(BasicCustomerSummaryMonitorService.class);
    private final PurchaseManagerService purchaseManagerService;
    private final PaymentManagerService paymentManagerService;
    private final SessionManagerService sessionManagerService;

    public BasicCustomerSummaryMonitorService(PurchaseManagerService purchaseManagerService,
                                              PaymentManagerService paymentManagerService,
                                              SessionManagerService sessionManagerService) {
        this.purchaseManagerService = purchaseManagerService;
        this.paymentManagerService = paymentManagerService;
        this.sessionManagerService = sessionManagerService;
    }

    @Async
    @EventListener
    public void handleCustomerSummaryCheckRequest(CustomerSummaryCheckRequestEvent event) {
        // ToDo: Remove payment info after order clear or last item removed from purchase list
        Customer customer = event.getCustomer();
        logger.info("Checking "  + customer + " Purchase Summary because of purchase " + event.getReason());
        updateCustomerSummary(customer);
    }

    @Override
    public void updateCustomerSummary(Customer customer) {
        Session session = sessionManagerService.getUnfinishedSession();
        Payment payment = paymentManagerService.getCustomerSessionPayment(session, customer)
                .orElse(new Payment(customer, session));
        List<Purchase> purchases = purchaseManagerService
                .findAllPurchasesInSessionByCustomer(session, customer);

        int discountValue = session.getDiscountPercentage();
        BigDecimal totalPrice = new BigDecimal(0);
        BigDecimal totalPriceWithDiscount = new BigDecimal(0);
        BigDecimal discountableTotalWithDiscount = new BigDecimal(0);
        BigDecimal discountableTotal = new BigDecimal(0);
        BigDecimal nonDiscountableTotal = new BigDecimal(0);

        if (purchases.size() > 0) {
            for (var purchase : purchases) {
                totalPrice = totalPrice.add(purchase.getTotalPrice());

                if (purchase.getProduct().isDiscountable()) {
                    discountableTotal = discountableTotal.add(purchase.getTotalPrice());
                } else {
                    nonDiscountableTotal = nonDiscountableTotal.add(purchase.getTotalPrice());
                }
            }

            discountableTotalWithDiscount = BigDecimalUtil.multiplyByDouble(
                    discountableTotal, ((double) (100 - discountValue) / 100));
            totalPriceWithDiscount = discountableTotalWithDiscount.add(nonDiscountableTotal);
        }

        payment.setTotalAmountNoDiscount(totalPrice);
        payment.setTotalAmountWithDiscount(totalPriceWithDiscount);
        payment.setDiscountableAmountWithDiscount(discountableTotalWithDiscount);
        payment.setDiscountableAmountNoDiscount(discountableTotal);
        payment.setNonDiscountableAmount(nonDiscountableTotal);

        paymentManagerService.save(payment);
    }
}
