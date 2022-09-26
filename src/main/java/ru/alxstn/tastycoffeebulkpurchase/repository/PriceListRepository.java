package ru.alxstn.tastycoffeebulkpurchase.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;


@Repository
public interface PriceListRepository extends CrudRepository<Product, Long> {

}
