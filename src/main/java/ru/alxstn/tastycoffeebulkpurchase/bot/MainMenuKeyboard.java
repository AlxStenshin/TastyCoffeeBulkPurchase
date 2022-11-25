package ru.alxstn.tastycoffeebulkpurchase.bot;

import java.util.Arrays;
import java.util.Optional;

public enum MainMenuKeyboard {
    PLACE_ORDER("Собрать заказ"),
    INFORMATION("Информация \uD83D\uDCCA"),
    SETTING("Настройки \uD83D\uDEE0"),
    EDIT_ORDER("Изменить заказ");

    private final String label;

    MainMenuKeyboard(String label) {
        this.label = label;
    }

    public static Optional<MainMenuKeyboard> parse(String name) {
        return Arrays.stream(values())
                .filter(b -> b.name().equalsIgnoreCase(name) || b.label.equalsIgnoreCase(name))
                .findFirst();
    }

    public String getLabel() {
        return label;
    }
}
