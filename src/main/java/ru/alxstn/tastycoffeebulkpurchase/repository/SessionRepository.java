package ru.alxstn.tastycoffeebulkpurchase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;

import java.util.Optional;


public interface SessionRepository extends JpaRepository<Session, Long> {
    @Query("SELECT s FROM session s WHERE " +
            "s.dateTimeOpened < CURRENT_TIMESTAMP AND " +
            "s.finished = false")
    Optional<Session> getActiveSession();

    @Transactional
    @Modifying
    @Query("UPDATE session s SET s.discountPercentage = :discount WHERE " +
            "s.dateTimeOpened < CURRENT_TIMESTAMP")
    void setActiveSessionDiscountValue(@Param(value = "discount") int value);

    @Transactional
    @Modifying
    @Query("UPDATE session s SET s.discountableWeight = :weight WHERE " +
            "s.dateTimeOpened < CURRENT_TIMESTAMP")
    void setActiveSessionDiscountableWeight(@Param(value = "weight") Double currentSessionDiscountSensitiveWeight);


    @Query("SELECT s.discountPercentage FROM session s WHERE " +
            "s.dateTimeOpened < CURRENT_TIMESTAMP")
    int getActiveSessionDiscountValue();

}
