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

    @Test
    void shouldCorrectlyRemoveAllSpecialSymbols() {
        assertEquals("OnlyAlphabetical", StringUtil.removeNonAlphanumeric("!Only_+Alpha%!<!---->^bet*()i$cal"));
    }

    @Test
    void shouldLeftStringUnchangedIfItDoesNotContainsAnySpecialSymbol() {
        assertEquals("justregularstring", StringUtil.removeNonAlphanumeric("justregularstring"));
    }

    @Test
    void shouldReturnCoffee() {
        assertEquals("Кофе", StringUtil.removeNonAlphanumeric( "Кофе    <!---->"));
    }

    @Test
    void shouldContainDate() {
        String input = "01.01.01";
        assertTrue(StringUtil.containsDate(input));
    }

    @Test
    void shouldNotContainDateWithTextInput() {
        String input = "regular string";
        assertFalse(StringUtil.containsDate(input));
    }

    @Test
    void shouldCorrectlyRemoveExtraSpaces() {
        String input = " Some    extra spaces  here!";
        assertEquals("Some extra spaces here!", StringUtil.removeExtraSpaces(input));
    }

    @Test
    void shouldNotRemoveAnySpaces() {
        String input = "There is no extra spaces here";
        assertEquals(input, StringUtil.removeExtraSpaces(input));
    }

    @Test
    void shouldNotRemoveNewLineSymbol() {
        String input = "There is no extra\n spaces here";
        assertEquals(input, StringUtil.removeExtraSpaces(input));
    }
}