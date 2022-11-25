package ru.alxstn.tastycoffeebulkpurchase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.alxstn.tastycoffeebulkpurchase.entity.Customer;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.entity.Purchase;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;

import java.util.List;
import java.util.Optional;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    @Query("SELECT p FROM Purchase p WHERE p.session = :session")
    List<Purchase> findAllPurchasesInSession(@Param(value = "session") Session session);

    @Query("SELECT p FROM Purchase p WHERE p.session = :session AND p.customer = :customer")
    List<Purchase> findAllPurchasesInSessionByCustomer(
            @Param(value = "session") Session session,
            @Param(value = "customer") Customer customer);

    @Transactional
    @Modifying
    @Query("UPDATE Purchase p SET p.count = :count, p.productForm = :form WHERE p.id = :id")
    void updatePurchaseWithId(
            @Param(value = "id") long id,
            @Param(value = "count") int count,
            @Param(value = "form") String form);

    // ToDo: Use Product.isDiscountable()
    @Query("SELECT SUM (p.count * p.product.productPackage.weight) FROM Purchase p WHERE " +
            "p.session.dateTimeOpened < CURRENT_TIMESTAMP AND " +
            "p.session.dateTimeClosed > CURRENT_TIMESTAMP AND " +
            "p.product.specialMark <> 'Сорт недели' AND " +
            "(p.product.productCategory = 'КОФЕ ДЛЯ ФИЛЬТРА' OR " +
            "p.product.productCategory = 'КОФЕ ДЛЯ ЭСПРЕССО' OR " +
            "p.product.productCategory = 'КОФЕ ДЛЯ МОЛОЧНЫХ НАПИТКОВ')")
    Optional<Double> getTotalDiscountSensitiveWeightForCurrentSession();

    @Query("SELECT DISTINCT p.customer FROM Purchase p WHERE p.session = :session")
    List<Customer> getSessionCustomers(@Param(value = "session") Session session);

    @Query("SELECT p FROM Purchase p WHERE " +
            "p.customer = :customer AND " +
            "p.product = :product AND " +
            "p.session = :session AND " +
            "p.productForm = :productForm")
    Optional<Purchase> getPurchaseIgnoringProductQuantity(
            @Param(value = "customer") Customer customer,
            @Param(value = "product") Product product,
            @Param(value = "session") Session session,
            @Param(value = "productForm") String productForm);
}
