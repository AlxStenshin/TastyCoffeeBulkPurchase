package ru.alxstn.tastycoffeebulkpurchase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;

import java.util.List;
import java.util.Optional;

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

    @Query("SELECT p FROM Product p WHERE " +
            "p.name = ?1 AND" +
            " p.productCategory = ?2 AND" +
            " p.productSubCategory = ?3 AND" +
            " p.productPackage = ?4 AND" +
            " p.specialMark = ?5 AND" +
            " p.price = ?6")
    Optional<Product> productExists(String name, String cat, String subCat, String pack, String mark, Double price);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Product p WHERE " +
            "p.name = ?1 AND" +
            " p.productCategory = ?2 AND" +
            " p.productSubCategory = ?3 AND" +
            " p.productPackage = ?4 AND" +
            " p.specialMark = ?5 AND" +
            " p.price = ?6")
    boolean isExists(String name, String cat, String subCat, String pack, String mark, Double price);

    //:studentName
    //@Param("studentName") String studentName
}
