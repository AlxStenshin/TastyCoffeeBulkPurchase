package ru.alxstn.tastycoffeebulkpurchase.util;

import java.math.BigDecimal;

public class BigDecimalUtil {
    public static BigDecimal multiplyByInt(BigDecimal inputValue, int multiplier) {
        return inputValue.multiply(BigDecimal.valueOf(multiplier));
    }

    public static BigDecimal multiplyByDouble(BigDecimal inputValue, double multiplier) {
        return inputValue.multiply(new BigDecimal(String.valueOf(multiplier)));
    }

    public static boolean greaterThanZero(BigDecimal inputValue) {
        return inputValue.compareTo(new BigDecimal(0)) > 0;
    }
}
