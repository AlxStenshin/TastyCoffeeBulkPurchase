package ru.alxstn.tastycoffeebulkpurchase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;

import java.time.LocalDateTime;

public interface SessionRepository extends JpaRepository<Session, Long> {
    @Query("SELECT s FROM session s WHERE s.dateTimeOpened < :currentDate AND s.dateTimeClosed > :currentDate")
    Session getCurrentSession(@Param(value = "currentDate") LocalDateTime date);

}
