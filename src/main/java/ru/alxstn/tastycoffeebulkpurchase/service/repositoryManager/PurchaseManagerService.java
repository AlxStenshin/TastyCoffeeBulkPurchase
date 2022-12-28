package ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager;

import ru.alxstn.tastycoffeebulkpurchase.entity.Customer;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.entity.Purchase;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;

import java.util.List;
import java.util.Optional;

public interface PurchaseManagerService {

    List<Purchase> findAllPurchasesInSession(Session session);

    List<Purchase> findProductPurchasesInSession(Session session, Product product);

    List<Purchase> findAllPurchasesInSessionByCustomer(Session session, Customer customer);

    List<Customer> getSessionCustomers(Session session);

    List<Customer> getSessionCustomersWithProduct(Session session, Product product);

    Optional<Purchase> getPurchaseIgnoringProductQuantity(Customer customer, Product product, Session session, String productForm);

    void removePurchaseForCustomerWithProductInSession(Customer customer, Session session, Product product);

    void replacePurchaseProductForCustomerInSession(Customer customer, Session session, Product oldProduct, Product newProduct);

    void deleteAll(List<Purchase> purchases);

    void delete(Purchase purchase);

    void save(Purchase purchase);
}
