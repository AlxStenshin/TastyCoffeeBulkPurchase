package ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager;

import org.springframework.stereotype.Service;
import ru.alxstn.tastycoffeebulkpurchase.entity.Customer;
import ru.alxstn.tastycoffeebulkpurchase.entity.Payment;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.repository.PaymentRepository;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class BasicPaymentManagerService implements PaymentManagerService {

    private final PaymentRepository paymentRepository;

    public BasicPaymentManagerService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Optional<Payment> getCustomerSessionPayment(Session session, Customer customer) {
        return paymentRepository.getCustomerSessionPayment(session, customer);
    }

    @Override
    public void registerPayment(Session session, Customer customer) {
        paymentRepository.registerPayment(session, customer);
    }

    @Override
    public Optional<Integer> getCompletePaymentsCount(Session session) {
        return paymentRepository.getCompletePaymentsCount(session);
    }

    @Override
    public Optional<Integer> getSessionCustomersCount(Session session) {
        return paymentRepository.getSessionCustomersCount(session);
    }

    @Override
    public Optional<BigDecimal> getSessionTotalPaidAmount(Session session) {
        return paymentRepository.getSessionTotalPaidAmount(session);
    }

    @Override
    public Optional<BigDecimal> getSessionTotalUnpaidAmount(Session session) {
        return paymentRepository.getSessionTotalUnpaidAmount(session);
    }

    @Override
    public Optional<BigDecimal> getSessionTotalPriceWithDiscount(Session session) {
        return paymentRepository.getSessionTotalPriceWithDiscount(session);
    }

    @Override
    public Optional<BigDecimal> getSessionTotalPrice(Session session) {
        return paymentRepository.getSessionTotalPrice(session);
    }

    @Override
    public void save(Payment payment) {
        paymentRepository.save(payment);
    }

}
