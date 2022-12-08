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
            "s.closed = false AND " +
            "s.finished = false")
    Optional<Session> getActiveSession();

    @Query("SELECT s FROM session s WHERE " +
            "s.finished = false")
    Optional<Session> getUnfinishedSession();

    @Transactional
    @Modifying
    @Query("UPDATE session s SET s.discountPercentage = :discount WHERE " +
            "s = :session")
    void setSessionDiscountValue(
            @Param(value = "session") Session session,
            @Param(value = "discount") int value);

    @Transactional
    @Modifying
    @Query("UPDATE session s SET s.discountableWeight = :weight WHERE " +
            "s = :session")
    void setSessionDiscountableWeight(
            @Param(value = "session") Session session,
            @Param(value = "weight") Double currentSessionDiscountSensitiveWeight);

    @Transactional
    @Modifying
    @Query("UPDATE session s SET s.coffeeWeight = :weight WHERE " +
            "s = :session")
    void setSessionCoffeeWeight(
            @Param(value = "session") Session session,
            @Param(value = "weight") Double currentSessionCoffeeWeight);

    @Transactional
    @Modifying
    @Query("UPDATE session s SET s.teaWeight = :weight WHERE " +
            "s = :session")
    void setSessionTeaWeight(
            @Param(value = "session") Session session,
            @Param(value = "weight") Double currentSessionTeaWeight);

    @Query("SELECT s.discountPercentage FROM session s WHERE " +
            "s.finished = false")
    int getActiveSessionDiscountValue();

}
