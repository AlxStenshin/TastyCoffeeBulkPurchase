package ru.alxstn.tastycoffeebulkpurchase.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.alxstn.tastycoffeebulkpurchase.entity.Customer;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.entity.Purchase;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;

import java.util.List;
import java.util.Optional;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    @Query("SELECT p FROM Purchase p WHERE p.session = :session")
    List<Purchase> findAllPurchasesInSession(
            @Param(value = "session") Session session);

    @Query("SELECT p FROM Purchase p WHERE p.session = :session AND " +
            "p.product = :product")
    List<Purchase> findProductPurchasesInSession(
            @Param(value = "session") Session session,
            @Param(value = "product") Product product);

    @Query("SELECT p FROM Purchase p WHERE p.session = :session AND p.customer = :customer")
    List<Purchase> findAllPurchasesInSessionByCustomer(
            @Param(value = "session") Session session,
            @Param(value = "customer") Customer customer);

    @Query("SELECT DISTINCT p.customer FROM Purchase p WHERE p.session = :session")
    List<Customer> getSessionCustomers(@Param(value = "session") Session session);

    @Query("SELECT DISTINCT p.customer FROM Purchase p WHERE " +
            "p.session = :session AND " +
            "p.product = :product")
    List<Customer> getSessionCustomersWithProduct(
            @Param(value = "session") Session session,
            @Param(value = "product") Product product);

    @Query("SELECT p FROM Purchase p WHERE " +
            "p.customer = :customer AND " +
            "p.product = :product AND " +
            "p.session = :session")
    Optional<Purchase> getPurchaseIgnoringProductQuantity(
            @Param(value = "customer") Customer customer,
            @Param(value = "product") Product product,
            @Param(value = "session") Session session);

    @Transactional
    @Modifying
    @Query("DELETE FROM Purchase p WHERE " +
            "p.customer = :customer AND " +
            "p.product = :product AND " +
            "p.session = :session")
    void removePurchaseForCustomerWithProductInSession(
            @Param(value = "customer") Customer customer,
            @Param(value = "session") Session session,
            @Param(value = "product") Product product);

    @Transactional
    @Modifying
    @Query("UPDATE Purchase p " +
            "SET p.product = :newProduct WHERE " +
            "p.customer = :customer AND " +
            "p.product = :oldProduct AND " +
            "p.session = :session")
    void replacePurchaseProductForCustomerInSession(
            @Param(value = "customer") Customer customer,
            @Param(value = "session") Session session,
            @Param(value = "oldProduct") Product oldProduct,
            @Param(value = "newProduct") Product newProduct);


}
