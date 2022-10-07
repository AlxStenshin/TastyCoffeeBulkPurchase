package ru.alxstn.tastycoffeebulkpurchase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;

import java.time.LocalDateTime;
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
            " p.name = :name AND" +
            " p.productCategory = :cat AND" +
            " p.productSubCategory = :subCat AND" +
            " p.productPackage = :pack AND" +
            " p.specialMark = :mark AND" +
            " p.price = :price")
    Optional<Product> productExists(@Param(value = "name") String name,
                                    @Param(value = "cat") String cat,
                                    @Param(value = "subCat") String subCat,
                                    @Param(value = "pack") String pack,
                                    @Param(value = "mark") String mark,
                                    @Param(value = "price") Double price);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Product p WHERE " +
            " p.name = :name AND" +
            " p.productCategory = :cat AND" +
            " p.productSubCategory = :subCat AND" +
            " p.productPackage = :pack AND" +
            " p.specialMark = :mark AND" +
            " p.price = :price")
    boolean isExists(@Param(value = "name") String name,
                     @Param(value = "cat") String cat,
                     @Param(value = "subCat") String subCat,
                     @Param(value = "pack") String pack,
                     @Param(value = "mark") String mark,
                     @Param(value = "price") Double price);

    @Transactional
    @Modifying
    @Query("UPDATE Product p SET p.dateUpdated = :updateDateTime, p.actual = true WHERE" +
            " p.name = :name AND" +
            " p.productCategory = :cat AND" +
            " p.productSubCategory = :subCat AND" +
            " p.productPackage = :pack AND" +
            " p.specialMark = :mark AND" +
            " p.price = :price")
    void update(@Param(value = "name") String name,
                @Param(value = "cat") String cat,
                @Param(value = "subCat") String subCat,
                @Param(value = "pack") String pack,
                @Param(value = "mark") String mark,
                @Param(value = "price") Double price,
                @Param(value = "updateDateTime") LocalDateTime updateDateTime);

    @Transactional
    @Modifying
    @Query("UPDATE Product p SET p.actual = false")
    void markAllNotActual();

}
