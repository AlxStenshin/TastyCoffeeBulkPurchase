package ru.alxstn.tastycoffeebulkpurchase.entity;


import org.junit.jupiter.api.Test;
import ru.alxstn.tastycoffeebulkpurchase.model.ProductBuilder;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void shouldBeDiscountableWithFirstCoffeeCategory() {
        Product discountableProduct = new ProductBuilder()
                .setCategory("КОФЕ ДЛЯ ФИЛЬТРА")
                .setSpecialMark("Товар месяца")
                .build();

        assertTrue(discountableProduct.isDiscountable());
    }

    @Test
    void shouldBeDiscountableWithSecondCoffeeCategory() {
        Product discountableProduct = new ProductBuilder()
                .setCategory("КОФЕ ДЛЯ ЭСПРЕССО")
                .setSpecialMark("Товар месяца")
                .build();

        assertTrue(discountableProduct.isDiscountable());
    }

    @Test
    void shouldBeDiscountableWithThirdCoffeeCategory() {
        Product discountableProduct = new ProductBuilder()
                .setCategory("КОФЕ ДЛЯ МОЛОЧНЫХ НАПИТКОВ")
                .setSpecialMark("Товар месяца")
                .build();

        assertTrue(discountableProduct.isDiscountable());
    }

    @Test
    void shouldThrowExceptionWithNullProductMark() {
        Product discountableProduct = new ProductBuilder()
                .setCategory("КОФЕ ДЛЯ МОЛОЧНЫХ НАПИТКОВ")
                .build();

        assertThrows(NullPointerException.class, discountableProduct::isDiscountable);
    }

    @Test
    void shouldNotBeDiscountableWithNonCoffeeCategoryAndNotGoodOfTheWeekMark() {
        Product discountableProduct = new ProductBuilder()
                .setCategory("Чай")
                .setSpecialMark("Новый")
                .build();

        assertFalse(discountableProduct.isDiscountable());
    }

    @Test
    void shouldNotBeDiscountableWithCoffeeCategoryAndGoodOfTheWeekMark() {
        Product discountableProduct = new ProductBuilder()
                .setCategory("КОФЕ ДЛЯ ЭСПРЕССО")
                .setSpecialMark("Сорт недели")
                .build();

        assertFalse(discountableProduct.isDiscountable());
    }

    @Test
    void shouldBeAvailable() {
        Product discountableProduct = new ProductBuilder()
                .setCategory("КОФЕ ДЛЯ ЭСПРЕССО")
                .setSpecialMark("Сорт недели")
                .build();

        assertTrue(discountableProduct.isAvailable());
    }

    @Test
    void shouldNotBeAvailable() {
        Product discountableProduct = new ProductBuilder()
                .setSpecialMark("нет")
                .build();

        assertFalse(discountableProduct.isAvailable());
    }
}