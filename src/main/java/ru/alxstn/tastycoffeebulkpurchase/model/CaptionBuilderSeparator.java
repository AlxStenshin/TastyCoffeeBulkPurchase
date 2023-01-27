package ru.alxstn.tastycoffeebulkpurchase.model;

public enum CaptionBuilderSeparator {
    COMMA_SPACE(", "),
    COMMA(","),
    SPACE(" "),
    NONE(""),
    ;

    private final String value;

    CaptionBuilderSeparator(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
