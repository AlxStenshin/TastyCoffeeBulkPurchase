package ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager;

import org.springframework.stereotype.Service;
import ru.alxstn.tastycoffeebulkpurchase.entity.Customer;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.entity.Purchase;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.repository.PurchaseRepository;

import java.util.List;
import java.util.Optional;

@Service
public class BasicPurchaseManagerService implements PurchaseManagerService {

    private final PurchaseRepository purchaseRepository;

    public BasicPurchaseManagerService(PurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    @Override
    public List<Purchase> findAllPurchasesInSession(Session session) {
        return purchaseRepository.findAllPurchasesInSession(session);
    }

    @Override
    public List<Purchase> findProductPurchasesInSession(Session session, Product product) {
        return purchaseRepository.findProductPurchasesInSession(session, product);
    }

    @Override
    public List<Purchase> findAllPurchasesInSessionByCustomer(Session session, Customer customer) {
        return purchaseRepository.findAllPurchasesInSessionByCustomer(session, customer);
    }

    @Override
    public List<Customer> getSessionCustomers(Session session) {
        return purchaseRepository.getSessionCustomers(session);
    }

    @Override
    public Optional<Purchase> getPurchaseIgnoringProductQuantity(Customer customer, Product product, Session session, String productForm) {
        return purchaseRepository.getPurchaseIgnoringProductQuantity(customer, product, session, productForm);
    }

    @Override
    public void removePurchaseForCustomerWithProductInSession(Customer customer, Session session, Product product) {
        purchaseRepository.removePurchaseForCustomerWithProductInSession(customer, session, product);
    }

    @Override
    public void replacePurchaseProductForCustomerInSession(Customer customer, Session session, Product oldProduct, Product newProduct) {
        purchaseRepository.replacePurchaseProductForCustomerInSession(customer, session, oldProduct, newProduct);
    }

    @Override
    public void deleteAll(List<Purchase> purchases) {
        purchaseRepository.deleteAll(purchases);
    }

    @Override
    public void deleteAll() {
        purchaseRepository.deleteAll();
    }

    @Override
    public void delete(Purchase purchase) {
        purchaseRepository.delete(purchase);
    }

    @Override
    public void save(Purchase purchase) {
        purchaseRepository.save(purchase);
    }
}
