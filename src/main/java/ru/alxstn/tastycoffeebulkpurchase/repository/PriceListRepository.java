package ru.alxstn.tastycoffeebulkpurchase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;

@Repository
public interface PriceListRepository extends JpaRepository<Product, Long> {

}
