package ru.alxstn.tastycoffeebulkpurchase.util;

public class StringUtil {
    public static String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

    public static String removeNonAlphanumeric(String str) {
        return str.replaceAll("[^a-zA-Z0-9ёА-я]", "");
    }

}
