package ru.alxstn.tastycoffeebulkpurchase.util;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class StringUtil {
    public static String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

    public static String removeExtraSpaces(String input) {
        return input.replaceAll(" +", " ").trim();
    }

    public static String removeNonAlphanumeric(String str) {
        return str.replaceAll("[^a-zA-Z0-9ёА-я]", "");
    }

    public static boolean containsDate(String text) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        try {
            format.parse(text);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }
}
