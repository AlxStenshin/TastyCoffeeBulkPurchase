package ru.alxstn.tastycoffeebulkpurchase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.alxstn.tastycoffeebulkpurchase.entity.Customer;
import ru.alxstn.tastycoffeebulkpurchase.entity.Payment;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT p FROM payment p WHERE " +
            "p.session = :session AND " +
            "p.customer = :customer")
    Optional<Payment> getCustomerSessionPayment(@Param(value = "session") Session session,
                                        @Param(value = "customer") Customer customer);

    @Modifying
    @Transactional
    @Query("UPDATE payment p SET p.paymentStatus = true WHERE " +
            "p.session = :session AND " +
            "p.customer = :customer ")
    void registerPayment(@Param(value = "session") Session session,
                         @Param(value = "customer") Customer customer);

    @Query("SELECT COUNT(p) FROM payment p WHERE " +
            "p.session = :session AND " +
            "p.paymentStatus = true")
    Optional<Integer> getCompletePaymentsCount(@Param(value = "session") Session session);

    @Query("SELECT COUNT(p) FROM payment p WHERE" +
            " p.session = :session")
    Optional<Integer> getSessionCustomersCount(@Param(value = "session") Session session);

    @Query("SELECT SUM(p.totalAmountWithDiscount) FROM payment p WHERE " +
            "p.session = :session AND " +
            "p.paymentStatus = true")
    Optional<BigDecimal> getSessionTotalPaidAmount(@Param(value = "session") Session session);

    @Query("SELECT SUM(p.totalAmountWithDiscount) FROM payment p WHERE " +
            "p.session = :session AND " +
            "p.paymentStatus = false")
    Optional<BigDecimal> getSessionTotalUnpaidAmount(@Param(value = "session") Session session);

    @Query("SELECT SUM(p.totalAmountWithDiscount) FROM payment p WHERE " +
            "p.session = :session")
    Optional<BigDecimal> getSessionTotalPriceWithDiscount(@Param(value = "session") Session session);

    @Query("SELECT SUM(p.totalAmountNoDiscount) FROM payment p WHERE " +
            "p.session = :session")
    Optional<BigDecimal> getSessionTotalPrice(@Param(value = "session") Session session);

}
