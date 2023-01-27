package ru.alxstn.tastycoffeebulkpurchase.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yy");
        try {
            format.parse(text);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}
