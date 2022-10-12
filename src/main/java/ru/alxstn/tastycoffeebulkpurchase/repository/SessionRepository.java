package ru.alxstn.tastycoffeebulkpurchase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;

public interface SessionRepository extends JpaRepository<Session, Long> {

}
