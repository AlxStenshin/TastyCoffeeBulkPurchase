package ru.alxstn.tastycoffeebulkpurchase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

}
