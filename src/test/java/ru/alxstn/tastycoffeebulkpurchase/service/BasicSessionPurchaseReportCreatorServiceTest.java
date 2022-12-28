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
import java.util.Map;

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
            "",
            true);

    private final Product firstProductCoarse = new Product("Product One",
            new BigDecimal(1),
            "",
            firstPackage,
            "Group",
            "Subgroup",
            "Coarse",
            true);

    private final Product secondProduct = new Product("Product Two",
            new BigDecimal(2),
            "",
            secondPackage,
            "Group",
            "Subgroup",
            "",
            true);

    private final Product thirdProductFine = new Product("Product Three",
            new BigDecimal(3),
            "",
            thirdPackage,
            "Group",
            "Subgroup2",
            "Fine",
            true);

    private final Product thirdProductCoarse = new Product("Product Three",
            new BigDecimal(3),
            "",
            thirdPackage,
            "Group",
            "Subgroup2",
            "Coarse",
            true);

    private final Product unavailableProduct = new Product("Unavailable Product",
            new BigDecimal(0),
            "нет",
            firstPackage,
            "Group3",
            "Subgroup3",
            "",
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

                        new Purchase(firstCustomer, firstProductCoarse, session, 1),
                        new Purchase(firstCustomer, secondProduct, session, 2),
                        new Purchase(firstCustomer, thirdProductFine, session, 3),

                        new Purchase(secondCustomer, firstProduct, session, 3),
                        new Purchase(secondCustomer, secondProduct, session, 2),
                        new Purchase(secondCustomer, thirdProductCoarse, session, 1)));

        assertDoesNotThrow(() -> {
            Map<Product, Integer> results = sessionPurchaseReportCreator.createPerProductReport(session);

            assertTrue(results.entrySet().stream().noneMatch(pe -> pe.getKey().getName().equals("Unavailable Product")));

            assertTrue(results.entrySet().stream().anyMatch(pe ->
                    pe.getKey().getName().equals("Product Two")
                            && pe.getValue() == 4));

            assertTrue(results.entrySet().stream().anyMatch(pe ->
                    pe.getKey().getName().equals("Product One") &&
                            pe.getKey().getProductForm().equals("Coarse") &&
                            pe.getValue() == 1));

            assertTrue(results.entrySet().stream().anyMatch(pe ->
                    pe.getKey().getName().equals("Product One")
                            && pe.getValue() == 3));

            assertTrue(results.entrySet().stream().anyMatch(pe ->
                    pe.getKey().getName().equals("Product Three")
                            && pe.getValue() == 1));

            assertTrue(results.entrySet().stream().anyMatch(pe ->
                    pe.getKey().getName().equals("Product Three")
                            && pe.getValue() == 3));
        });
    }

    @Test
    void shouldCreateBlankReport() {
        when(purchaseManager.findAllPurchasesInSession(session))
                .thenReturn(List.of());

        assertDoesNotThrow(() -> {
            Map<Product, Integer> results = sessionPurchaseReportCreator.createPerProductReport(session);
            assertTrue(results.isEmpty());
        });
    }
}