package ru.alxstn.tastycoffeebulkpurchase.util;

public enum TastyCoffeePageElementSelector {

    PRODUCT_TYPE_BAR("css", "ul.nav.tab-lk"),
    PRODUCT_TYPES("css", "li.nav-item", true),
    LINK("css", "a"),

    PRODUCT_CATEGORIES("css", "div.main-accordion", true),
    PRODUCT_CATEGORY_TITLE("css", "h3.title"),
    PRODUCT_SUBCATEGORIES("css", "div.accordion.accordion_product", true),
    PRODUCT_SUBCATEGORY_TITLE("css", "span"),
    PRODUCT_SUBCATEGORY_EXPAND_BUTTON("css", "button.status.active", true),

    PRODUCT_TABLE("css", "table.table-lk"),
    PRODUCT_ROW("css", "tr.searchTab", true),

    PRODUCT_TITLE("css", "button.font-13.p-0.selectable.btn.pointer"),
    PRODUCT_MARK_TITLE("css", "div.prefixdiv.empty-ic"),

    PRODUCT_REGULAR_PACKAGE("css", "td.price-count-1", true),
    PRODUCT_GRINDABLE_PACKAGE("css", "td.price-count-2", true),

    BULK_PURCHASE_SECTION("text", "Вход для оптовых клиентов"),
    PERSONAL_ACCOUNT_SECTION("text", "Личный кабинет"),
    ACCOUNT_LOGIN_BUTTON("text", "Войти"),
    USERNAME_INPUT("name", "login"),
    PASSWORD_INPUT("name", "password"),


    RESET_ORDER_BUTTON_TEXT("text", "Сбросить заказ"),
    CONFIRM_BUTTON_TEXT("text", "Да"),
    ONE_LEVEL_UP("xpath", "./.."),
    ;

    private final String selector;
    private final String selectorType;
    private boolean  expectingList;

    TastyCoffeePageElementSelector(String selectorType, String selector) {
        this.selectorType = selectorType;
        this.selector = selector;
    }

    TastyCoffeePageElementSelector(String selectorType, String selector, boolean expectingList) {
        this.selectorType = selectorType;
        this.selector = selector;
        this.expectingList = expectingList;
    }

    public String getSelector() {
        return selector;
    }

    public String getSelectorType() {
        return selectorType;
    }

    public boolean isExpectingList() {
        return expectingList;
    }
}
