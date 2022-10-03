package ru.alxstn.tastycoffeebulkpurchase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.alxstn.tastycoffeebulkpurchase.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

}
