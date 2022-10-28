package ru.alxstn.tastycoffeebulkpurchase.util;

public class StringUtil {
    public static String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }
}
