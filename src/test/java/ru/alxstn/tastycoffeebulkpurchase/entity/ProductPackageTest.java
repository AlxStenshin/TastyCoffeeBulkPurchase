package ru.alxstn.tastycoffeebulkpurchase.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ProductPackageTest {

    @Test
    void shouldHave100gWeight() {
        assertEquals(0.1d, new ProductPackage("Упаковка 100 г").getWeight());
    }

    @Test
    void shouldHave250gWeight() {
        assertEquals(0.25d, new ProductPackage("Упаковка 250 г").getWeight());
    }

    @Test
    void shouldHave1000gWeight() {
        assertEquals(1d, new ProductPackage("Упаковка 1 кг").getWeight());
    }

    @Test
    void shouldHave2000gWeight() {
        assertEquals(2d, new ProductPackage("Упаковка 2 кг").getWeight());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Упаковка 100г", "Упаковка100г", "100г", "100кг",
            "Упаковка 250г", "Упаковка250г", "250г", "200кг",
            "Упаковка 1000г", "Упаковка100г", "100г",  "1000кг",
            "Упаковка 2000г", "Упаковка100г", "100г",  "2000кг", "", "Упаковка"})
    void shouldNotHaveWeight(String productPackageDescription) {
        assertEquals(0.0d, new ProductPackage(productPackageDescription).getWeight());
    }

}