package ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager;

import ru.alxstn.tastycoffeebulkpurchase.entity.Customer;
import ru.alxstn.tastycoffeebulkpurchase.entity.Payment;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;

import java.math.BigDecimal;
import java.util.Optional;

public interface PaymentManagerService {

    Optional<Payment> getCustomerSessionPayment(Session session, Customer customer);

    void registerPayment(Session session, Customer customer);

    Optional<Integer> getCompletePaymentsCount(Session session);

    Optional<Integer> getSessionCustomersCount(Session session);

    Optional<BigDecimal> getSessionTotalPaidAmount(Session session);

    Optional<BigDecimal> getSessionTotalUnpaidAmount(Session session);

    Optional<BigDecimal> getSessionTotalPriceWithDiscount(Session session);

    Optional<BigDecimal> getSessionTotalPrice(Session session);

    void save(Payment payment);
}
