package ru.alxstn.tastycoffeebulkpurchase.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilTest {

    @Test
    void shouldCorrectlyCapitalizeLowRegistryWord() {
        assertEquals("Capital", StringUtil.capitalize("capital"));
    }

    @Test
    void shouldCorrectlyCapitalizeHighRegistryWord() {
        assertEquals("CAPITAL", StringUtil.capitalize("CAPITAL"));
    }
}