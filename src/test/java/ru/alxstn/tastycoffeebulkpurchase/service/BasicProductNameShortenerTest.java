package ru.alxstn.tastycoffeebulkpurchase.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;

import static org.junit.jupiter.api.Assertions.*;

class BasicProductNameShortenerTest {

    static ProductNameShortener shortener;

    @BeforeAll
    static void init() {
        shortener = new BasicProductNameShortener();
    }

    @Test
    void shouldShortenProductNameWithDuplicateInCategoryName() {
        Product testProduct = new Product("Кофейный концентрат Бэрри, 3 л",
                100d, "", "",
                "КОФЕЙНЫЙ КОНЦЕНТРАТ", "Упаковка 3 литра");
        assertEquals("Бэрри, 3 л", shortener.getShortName(testProduct));

    }

    @Test
    void shouldShortenProductNameWithAnotherDuplicateInCategoryName() {
        Product testProduct = new Product("Кофейный концентрат Колумбия Рейнальдо Лопес, 3 л",
                100d, "", "",
                "КОФЕЙНЫЙ КОНЦЕНТРАТ", "Упаковка 3 литра");
        assertEquals("Колумбия Рейнальдо Лопес, 3 л", shortener.getShortName(testProduct));
    }

    @Test
    void shouldShortenProductNameWithDuplicateInCategoryNameContainsSpecialSymbol() {
        Product testProduct = new Product("Кофе в банках \"Фильтр-кофе\"",
                100d, "", "",
                "КОФЕ В БАНКАХ", "");

        assertEquals("\"Фильтр-кофе\"", shortener.getShortName(testProduct));
    }

    @Test
    void shouldShortenProductNameRemovingParentheses() {
        Product testProduct = new Product("Клубника со сливками, зелёный (Германия)",
                100d, "", "",
                "Чай", "Зеленый ароматизированный чай");
        assertEquals("Клубника со сливками, зелёный", shortener.getShortName(testProduct));
    }

    @Test
    void shouldShortenProductNameUsingDictionary() {
        Product testProduct = new Product("Дрип-пакеты Эфиопия Дисалейн Хиджо",
                100d, "", "",
                "ДРИП-КОФЕ", "Упаковка 10 дрип-пакетов");
        assertEquals("Эфиопия Дисалейн Хиджо", shortener.getShortName(testProduct));
    }

}