package ru.alxstn.tastycoffeebulkpurchase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.alxstn.tastycoffeebulkpurchase.entity.Purchase;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

}
