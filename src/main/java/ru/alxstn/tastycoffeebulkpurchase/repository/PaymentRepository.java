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

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // ToDo: remove payment info if customer cleans order or removes last record from order

    @Query("SELECT p FROM payment p WHERE  p.session = :session AND p.customer = :customer")
    Optional<Payment> paymentRegistered(@Param(value = "session") Session session,
                                        @Param(value = "customer") Customer customer);

    @Modifying
    @Transactional
    @Query("UPDATE payment p SET p.paymentStatus = true")
    void registerPayment(@Param(value = "session") Session session,
                         @Param(value = "customer") Customer customer);

    @Query("SELECT COUNT(p) FROM payment p WHERE p.session = :session AND p.paymentStatus = true")
    int getCompletePaymentsCount(@Param(value = "session") Session session);

    @Query("SELECT COUNT(p) FROM payment p WHERE p.session = :session")
    int getSessionCustomersCount(@Param(value = "session") Session session);

}
