package ru.alxstn.tastycoffeebulkpurchase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.alxstn.tastycoffeebulkpurchase.entity.ProductPackage;

@Repository
public interface ProductPackageRepository extends JpaRepository<ProductPackage, Long> {

    ProductPackage getProductPackageByDescription(String description);
}
