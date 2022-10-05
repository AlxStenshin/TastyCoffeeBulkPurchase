package ru.alxstn.tastycoffeebulkpurchase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT DISTINCT productCategory FROM Product")
    List<String> findAllCategories();

    @Query("SELECT DISTINCT productSubCategory FROM Product WHERE productCategory = ?1")
    List<String> findAllSubCategories(String category);

    @Query("SELECT DISTINCT name FROM Product  WHERE productSubCategory = ?1")
    List<String> findAllProductsBySubCategory(String subCategory);

    @Query("SELECT p FROM Product p WHERE p.name = ?1")
    List<Product> findAllProductPrices(String productName);
}
