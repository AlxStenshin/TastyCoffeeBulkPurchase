package ru.alxstn.tastycoffeebulkpurchase.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BigDecimalUtilTest {

    @Test
    void shouldCorrectlyMultiplyByInt() {
        BigDecimal inputValue = new BigDecimal("0.1");
        int multiplier = 3;
        BigDecimal expected = new BigDecimal("0.3");

        assertEquals(expected, BigDecimalUtil.multiplyByInt(inputValue, multiplier));
    }

    @Test
    void shouldCorrectlyMultiplyByNegativeInt() {
        BigDecimal inputValue = new BigDecimal("0.1");
        int multiplier = -3;
        BigDecimal expected = new BigDecimal("-0.3");

        assertEquals(expected, BigDecimalUtil.multiplyByInt(inputValue, multiplier));
    }

    @Test
    void shouldCorrectlyMultiplyByZeroInt() {
        BigDecimal inputValue = new BigDecimal("0.1");
        int multiplier = 0;
        BigDecimal expected = new BigDecimal("0.0");

        assertEquals(expected, BigDecimalUtil.multiplyByInt(inputValue, multiplier));
    }

    @Test
    void shouldCorrectlyMultiplyByDouble() {
        BigDecimal inputValue = new BigDecimal("0.1");
        double multiplier = 3.0;
        BigDecimal expected = new BigDecimal("0.30");

        assertEquals(expected, BigDecimalUtil.multiplyByDouble(inputValue, multiplier));
    }

    @Test
    void shouldCorrectlyMultiplyByNegativeDouble() {
        BigDecimal inputValue = new BigDecimal("0.1");
        double multiplier = -3.0;
        BigDecimal expected = new BigDecimal("-0.30");

        assertEquals(expected, BigDecimalUtil.multiplyByDouble(inputValue, multiplier));
    }

    @Test
    void shouldCorrectlyMultiplyByZeroDouble() {
        BigDecimal inputValue = new BigDecimal("0.1");
        double multiplier = 0;
        BigDecimal expected = new BigDecimal("0.00");

        assertEquals(expected, BigDecimalUtil.multiplyByDouble(inputValue, multiplier));
    }

    @Test
    void shouldCorrectlyCompareSubZeroValueWithZero() {
        BigDecimal inputValue = new BigDecimal("-0.1");
        assertFalse(BigDecimalUtil.greaterThanZero(inputValue));
    }

    @Test
    void shouldCorrectlyComparePositiveNonZeroValueWithZero() {
        BigDecimal inputValue = new BigDecimal("0.1");
        assertTrue(BigDecimalUtil.greaterThanZero(inputValue));
    }

    @Test
    void shouldCorrectlyCompareZeroValueWithZero() {
        BigDecimal inputValue = new BigDecimal("0.0");
        assertFalse(BigDecimalUtil.greaterThanZero(inputValue));
    }

    @Test
    void shouldBeEqualsWithRegularValues() {
        BigDecimal firstValue = new BigDecimal("200.0");
        BigDecimal secondValue = new BigDecimal("200.00");
        assertTrue(BigDecimalUtil.equals(firstValue, secondValue));
    }

    @Test
    void shouldBeEqualsWithZeros() {
        BigDecimal firstValue = new BigDecimal("0.0");
        BigDecimal secondValue = new BigDecimal("0.00");
        assertTrue(BigDecimalUtil.equals(firstValue, secondValue));
    }

    @Test
    void shouldNotBeEquals() {
        BigDecimal firstValue = new BigDecimal("0.1");
        BigDecimal secondValue = new BigDecimal("0.00");
        assertFalse(BigDecimalUtil.equals(firstValue, secondValue));
    }

    @Test
    void shouldNotBeEqualsWithVerySmallDiff() {
        BigDecimal firstValue = new BigDecimal("0.1");
        BigDecimal secondValue = new BigDecimal("0.1000000001");
        assertFalse(BigDecimalUtil.equals(firstValue, secondValue));
    }

    @Test
    void shouldNotBeEqualsWithVeryCloseValues() {
        BigDecimal firstValue = new BigDecimal("1.0000001");
        BigDecimal secondValue = new BigDecimal("1.0000002");
        assertFalse(BigDecimalUtil.equals(firstValue, secondValue));
    }

}