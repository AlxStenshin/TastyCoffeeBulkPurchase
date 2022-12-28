package ru.alxstn.tastycoffeebulkpurchase.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.alxstn.tastycoffeebulkpurchase.entity.*;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.PurchaseManagerService;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
class BasicSessionPurchaseReportCreatorServiceTest {

    @Mock
    PurchaseManagerService purchaseManager;

    @InjectMocks
    BasicSessionPurchaseReportCreatorService sessionPurchaseReportCreator;

    private final Session session = new Session();
    { session.setId(1L); }

    private final Customer firstCustomer = new Customer(1L, "First", "Customer", null);
    private final Customer secondCustomer = new Customer(2L, "Second", "Customer", null);

    private final ProductPackage firstPackage = new ProductPackage("Упаковка 250 г");
    private final ProductPackage secondPackage = new ProductPackage("Упаковка 100 г");
    private final ProductPackage thirdPackage = new ProductPackage("Упаковка 1 кг");

    private final Product firstProduct = new Product("Product One",
            new BigDecimal(1),
            "",
            firstPackage,
            "Group",
            "Subgroup",
            true);

    private final Product secondProduct = new Product("Product Two",
            new BigDecimal(2),
            "",
            secondPackage,
            "Group",
            "Subgroup",
            true);

    private final Product thirdProduct = new Product("Product Three",
            new BigDecimal(3),
            "",
            thirdPackage,
            "Group",
            "Subgroup2",
            true);

    private final Product unavailableProduct = new Product("Unavailable Product",
            new BigDecimal(0),
            "нет",
            firstPackage,
            "Group3",
            "Subgroup3",
            true);

    @AfterEach
    void cleanup() {
        try {
            Files.deleteIfExists(Path.of(session.getId() + ".sessionReport.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldCreateCorrectReport() {
        when(purchaseManager.findAllPurchasesInSession(session))
                .thenReturn(List.of(
                        new Purchase(firstCustomer, unavailableProduct, session, 1),

                        new Purchase(firstCustomer, firstProduct, session, "Крупный", 1),
                        new Purchase(firstCustomer, secondProduct, session, 2),
                        new Purchase(firstCustomer, thirdProduct, session, "Мелкий", 3),

                        new Purchase(secondCustomer, firstProduct, session, 3),
                        new Purchase(secondCustomer, secondProduct, session, 2),
                        new Purchase(secondCustomer, thirdProduct, session, "Крупный", 1)));

        assertDoesNotThrow(() -> {
            List<PurchaseEntry> results = sessionPurchaseReportCreator.createPerProductReport(session);

            assertTrue(results.stream().noneMatch(pe -> pe.getProduct().getName().equals("Unavailable Product")));

            assertTrue(results.stream().anyMatch(pe ->
                    pe.getProduct().getName().equals("Product Two")
                            && pe.getCount() == 4));

            assertTrue(results.stream().anyMatch(pe ->
                    pe.getProduct().getName().equals("Product One")
                            && pe.getCount() == 3));

            assertTrue(results.stream().anyMatch(pe ->
                    pe.getProduct().getName().equals("Product One")
                            && pe.getProductForm().equals("Крупный")
                            && pe.getCount() == 1));

            assertTrue(results.stream().anyMatch(pe ->
                    pe.getProduct().getName().equals("Product Three")
                            && pe.getProductForm().equals("Крупный")
                            && pe.getCount() == 1));

            assertTrue(results.stream().anyMatch(pe ->
                    pe.getProduct().getName().equals("Product Three")
                            && pe.getProductForm().equals("Мелкий")
                            && pe.getCount() == 3));
        });
    }

    @Test
    void shouldCreateBlankReport() {
        when(purchaseManager.findAllPurchasesInSession(session))
                .thenReturn(List.of());

        assertDoesNotThrow(() -> {
            List<PurchaseEntry> results = sessionPurchaseReportCreator.createPerProductReport(session);
            assertTrue(results.isEmpty());
        });
    }
}