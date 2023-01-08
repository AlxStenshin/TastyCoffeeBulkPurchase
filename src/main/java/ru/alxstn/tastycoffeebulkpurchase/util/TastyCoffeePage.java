package ru.alxstn.tastycoffeebulkpurchase.util;

import com.codeborne.selenide.*;
import com.codeborne.selenide.ex.ElementNotFound;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import ru.alxstn.tastycoffeebulkpurchase.configuration.TastyCoffeeConfigProperties;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.entity.ProductPackage;
import ru.alxstn.tastycoffeebulkpurchase.event.ProductFoundEvent;
import ru.alxstn.tastycoffeebulkpurchase.exception.webPage.WebPageElementException;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static ru.alxstn.tastycoffeebulkpurchase.util.TastyCoffeePageElementSelector.*;

@Component
public class TastyCoffeePage {
    Logger logger = LogManager.getLogger(TastyCoffeePage.class);

    private final ApplicationEventPublisher publisher;
    private final TastyCoffeeConfigProperties tastyCoffeeConfig;

    public TastyCoffeePage(TastyCoffeeConfigProperties properties,
                           ApplicationEventPublisher newProductPublisher) {
        this.publisher = newProductPublisher;
        this.tastyCoffeeConfig = properties;

        Configuration.timeout = 10;
        Configuration.browserSize = "1920x1080";
        Configuration.browserPosition = "2x2";
        Configuration.headless = true;
    }

    public List<Product> buildPriceList() {

        login();
        resetOrder();

        List<Product> allProducts = new ArrayList<>();
        try {
            List<String> acceptedProductTypes = List.of("Кофе", "Чай", "Шоколад", "Сиропы");
            ElementsCollection tabs = new TastyCoffeeWebPageElement()
                    .applySelector(PRODUCT_TYPE_BAR)
                    .applySelector(PRODUCT_TYPES)
                    .getElements();

            for (var tab : tabs) {
                SelenideElement link = new TastyCoffeeWebPageElement(tab).applySelector(LINK).getElement();
                String text = link.innerHtml();
                if (acceptedProductTypes.stream().anyMatch(text::contains)) {
                    logger.info("Now Parsing Tab: " + StringUtil.removeNonAlphanumeric(text));
                    clickWebElement(link.getWrappedElement());
                    expandAllProductSubCategories();
                    allProducts.addAll(parseAllProductsOfType());
                } else {
                    logger.info("Ignoring Price List Tab " + text.replace("\n", ""));
                }

            }
        } catch (RuntimeException e) {
            logger.error("WebPageElementError: " + e.getMessage());
            throw new WebPageElementException(e);
        }

        List<Product> grindable = allProducts.stream().filter(Product::isGrindable).toList();
        allProducts.removeAll(grindable);

        List<String> productForms = List.of("Зерно", "Крупный", "Средний", "Мелкий");
        for (Product p : grindable) {
            for (String productForm : productForms) {
                allProducts.add(p.createFormedProduct(productForm));
            }
        }

        return allProducts;
    }

    public List<Product> placeOrder(Map<Product, Integer> currentSessionPurchases) {
        try {
            login();
            resetOrder();

            SelenideElement tabsBar = $("ul.nav.tab-lk");
            ElementsCollection tabs = tabsBar.$$("li.nav-item");
            List<String> acceptedCategories = List.of("Кофе", "Чай", "Шоколад", "Сиропы");

            for (var tab : tabs) {
                SelenideElement link = tab.$("a");
                String productType = link.innerHtml();
                if (acceptedCategories.stream().anyMatch(productType::contains)) {
                    logger.info("Now Filling " + StringUtil.removeNonAlphanumeric(productType) + "-Type Purchases");
                    clickWebElement(link.getWrappedElement());
                    Selenide.sleep(500);
                    expandAllProductSubCategories();
                    fillPurchasesAvailableOnPage(currentSessionPurchases);
                } else {
                    logger.info("Ignoring Price List Tab " + StringUtil.removeNonAlphanumeric(productType));
                }
            }

        } catch (RuntimeException e) {
            logger.error("WebPageElementError: " + e.getMessage());
            throw new WebPageElementException(e);
        }
        return new ArrayList<>(currentSessionPurchases.keySet());
    }

    public void login() {
        try {
            open(tastyCoffeeConfig.getUrl());

            SelenideElement bulkPurchaseClientLoginButton = new TastyCoffeeWebPageElement()
                    .applySelector(BULK_PURCHASE_SECTION)
                    .getElement();

            bulkPurchaseClientLoginButton.click();
            Selenide.sleep(1_000);

            try {
                SelenideElement goToAccountButton = new TastyCoffeeWebPageElement()
                        .applySelector(PERSONAL_ACCOUNT_SECTION)
                        .getElement();
                goToAccountButton.click();
                logger.info("User already logged in, login routine skipped");
            } catch (ElementNotFound ignored) {
                SelenideElement usernameInput = new TastyCoffeeWebPageElement()
                        .applySelector(USERNAME_INPUT)
                        .getElement();
                usernameInput.setValue(tastyCoffeeConfig.getUserName());

                SelenideElement passwordInput = new TastyCoffeeWebPageElement()
                        .applySelector(PASSWORD_INPUT)
                        .getElement();
                passwordInput.setValue(tastyCoffeeConfig.getPassword());

                SelenideElement loginButton = new TastyCoffeeWebPageElement()
                        .applySelector(ACCOUNT_LOGIN_BUTTON)
                        .getElement();
                loginButton.click();
            }
            Selenide.sleep(10_000);
        } catch (RuntimeException e) {
            logger.error("WebPageElementError: " + e.getMessage());
            throw new WebPageElementException(e);
        }
    }

    private void resetOrder() {
        try {
            SelenideElement resetOrderButton = new TastyCoffeeWebPageElement()
                    .applySelector(RESET_ORDER_BUTTON_TEXT)
                    .applySelector(ONE_LEVEL_UP)
                    .getElement();
            clickWebElement(resetOrderButton);
            try {
                SelenideElement confirmButton = new TastyCoffeeWebPageElement()
                        .applySelector(CONFIRM_BUTTON_TEXT)
                        .applySelector(ONE_LEVEL_UP)
                        .getElement();
                clickWebElement(confirmButton);
            } catch (ElementNotFound ignored) {
                logger.info("Confirm Reset Order button not found, is order empty already?");
            }
        } catch (RuntimeException e) {
            logger.error("WebPageElementError: " + e.getMessage());
            throw new WebPageElementException(e);
        }
    }

    public void expandAllProductSubCategories() {
        try {
            clickElements(new TastyCoffeeWebPageElement()
                    .applySelector(PRODUCT_SUBCATEGORY_EXPAND_BUTTON)
                    .getElements());
            Selenide.sleep(1_000);
        } catch (RuntimeException e) {
            logger.error("WebPageElementError: " + e.getMessage());
            throw new WebPageElementException(e);
        }
    }

    // https://stackoverflow.com/questions/52720560/selenide-removing-displayedfalse-not-working
    public void clickElements(ElementsCollection collection) {
        try {
            for (SelenideElement element : collection) {
                clickWebElement(element);
            }
        } catch (RuntimeException e) {
            logger.error("WebPageElementError: " + e.getMessage());
            throw new WebPageElementException(e);
        }
    }

    private void clickWebElement(WebElement element) {
        try {
            String jsClickCode = "arguments[0].scrollIntoView(true); arguments[0].click();";
            var driver = getWebDriver();
            ((JavascriptExecutor) driver).executeScript(jsClickCode, element);
        } catch (Exception e) {
            logger.warn("Element could not be clicked.. " + e.getMessage());
        }
    }

    private void addProducts(List<Product> categoryProducts, Product.ProductBuilder productBuilder, List<SelenideElement> products) {
        for (var product : products) {
            try {
                productBuilder.setPrice(getProductPrice(product));
                productBuilder.setPackage(new ProductPackage(getProductPackage(product)));

                Product newProduct = productBuilder.build();
                publisher.publishEvent(new ProductFoundEvent(this, newProduct));
                categoryProducts.add(newProduct);
            } catch (RuntimeException e) {
                logger.warn("Warning parsing product " + productBuilder.build().toString() + " Message: " + e.getMessage() +
                        " Product will be skipped.");
            }
        }
    }

    private void fillPurchasesAvailableOnPage(Map<Product, Integer> currentSessionPurchases) {

        for (SelenideElement productCategory : getProductCategories()) {
            for (SelenideElement productSubgroup : getProductSubCategoriesFromCategory(productCategory)) {

                String subgroupTitle = getSubGroupTitle(productSubgroup);
                Map<Product, Integer> currentSubcategoryPurchases = currentSessionPurchases.entrySet().stream()
                        .filter(p -> p.getKey().getProductSubCategory().equals(subgroupTitle))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                for (var purchase : currentSubcategoryPurchases.entrySet()) {
                    Product product = purchase.getKey();
                    int productCountIncrement = purchase.getValue();

                    try {
                        SelenideElement counter = findProductCounter(product);
                        SelenideElement incrementButton = getIncrementButtonFromQuantityCounter(counter);
                        int currentProductCount = getCurrentProductCountValue(counter);

                        logger.info(
                                "Starting product count increase for product: " + product.getShortName() +
                                ". Current count: " + currentProductCount +
                                "  increments: " + productCountIncrement);
                        int retries = 0;
                        while (productCountIncrement > 0) {
                            clickWebElement(incrementButton);
                            if (getCurrentProductCountValue(locateProductCounter(product)) == currentProductCount + 1) {
                                logger.info(product.getShortName() + " Increment Succeed!");
                                productCountIncrement--;
                                currentProductCount++;
                            } else {
                                logger.warn(product.getShortName() + " Increment Failed. Retries: " + retries);
                                retries++;
                            }
                            if (retries > 5)
                                break;
                        }
                    } catch (ElementNotFound e) {
                        logger.warn("Could Not Find Element By Text: " + e.getMessage() + "\nFor product" + product);
                    }

                    currentSessionPurchases.remove(purchase.getKey());
                }
            }
        }
    }

    private SelenideElement getQuantityControlElement(SelenideElement productTableRow, String productPackage, String productForm) {

        // Product Table Row:
        //
        // | <productName>          | <productPackage>                                                   | ... |
        // | <productName>          | <productPrice>    | <quantitySelector>                             | ... |
        // | td: searchTab count-n  | td: price-count-m | td: quantity quantity-custom quantity-count-m  | ... |
        //
        // searchTab count-n : n is available product Packages count (productPrice + productQuantitySelector columns)
        // price-count-m : m is productQuantitySelector's count for current product package
        //
        // Basic product contains one quantity selector:
        // | <productName> | ... | <productPrice | countSelector1 | ... |
        //
        // Grindable product contains two quantity selectors:
        // | <productName> | ... | <productPrice> | <countSelector1> | <countSelector2> | ... |
        // First selector is for Beans. Second - grindable.

        ElementsCollection availableElements = productTableRow.findAll(By.xpath("./*"));
        ElementsCollection pricesForPackage = productTableRow.findAll(By.cssSelector("samp.mob"));
        Optional<SelenideElement> targetProductPrice = Optional.empty();

        for (var price : pricesForPackage) {
            if (price.innerHtml().contains(productPackage))
                targetProductPrice = Optional.of(price.find(By.xpath("./../..")));
        }

        int priceElementIndex = availableElements
                .indexOf(targetProductPrice.orElseThrow(() -> new RuntimeException("product not found")));

        if (productForm.isEmpty() || productForm.equals("Зерно"))
            // Beans or other non-grindable package
            return availableElements.get(priceElementIndex + 1);
        else {
            // Grindable Package
            return availableElements.get(priceElementIndex + 2);
        }
    }

    private List<Product> parseAllProductsOfType() {
        List<Product> currentTypeProducts = new ArrayList<>();
        Product.ProductBuilder productBuilder = new Product.ProductBuilder();
        for (SelenideElement productCategory : new TastyCoffeeWebPageElement()
                .applySelector(PRODUCT_CATEGORIES)
                .getElements()) {
            productBuilder.setCategory(getCategoryTitle(productCategory));

            for (SelenideElement productSubCategory : new TastyCoffeeWebPageElement(productCategory)
                    .applySelector(PRODUCT_SUBCATEGORIES)
                    .getElements()) {
                productBuilder.setSubCategory(getSubGroupTitle(productSubCategory));

                for (SelenideElement product : new TastyCoffeeWebPageElement(productSubCategory)
                        .applySelector(PRODUCT_TABLE)
                        .applySelector(PRODUCT_ROW)
                        .getElements()) {
                    productBuilder.setName(getProductTitle(product));
                    try {
                        productBuilder.setSpecialMark(getSpecialMarkTitle(product));
                    } catch (ElementNotFound ignored) {
                        productBuilder.setSpecialMark("");
                    }

                    productBuilder.setProductForm("");

                    productBuilder.setGrindable(false);
                    addProducts(currentTypeProducts, productBuilder, getRegularProductPacks(product));

                    productBuilder.setGrindable(true);
                    addProducts(currentTypeProducts, productBuilder, getGrindableProductPacks(product));
                }
            }
        }

        logger.info("Price List Tab Parsing Complete! Found: " + currentTypeProducts.size() + " items.");
        return currentTypeProducts;
    }

    private SelenideElement locateProductCounter(Product product) {

        for (SelenideElement productGroup : getProductCategories()) {
            for (SelenideElement productSubgroup : getProductSubCategoriesFromCategory(productGroup)) {
                try {
                    SelenideElement correspondingProduct = productSubgroup.find(byText(product.getName()));
                    SelenideElement correspondingProductTableRow = correspondingProduct.find(By.xpath("./../../.."));

                    return getQuantityControlElement(correspondingProductTableRow,
                            product.getProductPackage().getDescription(),
                            product.getProductForm());
                } catch (ElementNotFound ignored) {}
            }
        }
        return null;
    }

    private SelenideElement locateIncrementButton(Product product) {
        for (SelenideElement productGroup : getProductCategories()) {
            for (SelenideElement productSubgroup : getProductSubCategoriesFromCategory(productGroup)) {
                try {
                    SelenideElement correspondingProduct = productSubgroup.find(byText(product.getName()));
                    SelenideElement correspondingProductTableRow = correspondingProduct.find(By.xpath("./../../.."));
                    SelenideElement counter = getQuantityControlElement(correspondingProductTableRow,
                            product.getProductPackage().getDescription(),
                            product.getProductForm());
                    return getIncrementButtonFromQuantityCounter(counter);
                } catch (ElementNotFound ignored) {}
            }
        }
        return null;
    }

    private SelenideElement findProductCounter(Product product) {

        SelenideElement correspondingProduct = findProductSubcategory(product)
                .find(byText(product.getName()))
                .find(By.xpath("./../../.."));

        return getQuantityControlElement(correspondingProduct,
                product.getProductPackage().getDescription(),
                product.getProductForm());
    }

    private SelenideElement findProductSubcategory(Product product) {
        return findProductCategory(product)
                .findAll(getProductSubgroupsFromGroupLocator())
                .findBy(Condition.name(product.getProductSubCategory()));
    }

    private SelenideElement findProductCategory(Product product) {
        return $$(getAllProductCategoriesLocator())
                .findBy(Condition.name(product.getProductCategory()));
    }

    private ElementsCollection getProductCategories() {
        return $$(getAllProductCategoriesLocator());
    }

    private String getAllProductCategoriesLocator() {
        return "div.main-accordion";
    }

    private ElementsCollection getProductSubCategoriesFromCategory(SelenideElement productGroup) {
        return productGroup.$$(getProductSubgroupsFromGroupLocator());
    }

    private String getProductSubgroupsFromGroupLocator() {
        return "div.accordion.accordion_product";
    }


    private String getProductsTableLocator() {
        return "table.table-lk";
    }

    private String getProductRowLocator() {
        return "tr.searchTab";
    }

    private List<SelenideElement> getRegularProductPacks(SelenideElement product) {
        return new TastyCoffeeWebPageElement(product)
                .applySelector(PRODUCT_REGULAR_PACKAGE)
                .getElements();
    }

    private String getRegularProductPackagesLocator() {
        return "td.price-count-1";
    }

    private List<SelenideElement> getGrindableProductPacks(SelenideElement product) {
        return new TastyCoffeeWebPageElement(product)
                .applySelector(PRODUCT_GRINDABLE_PACKAGE)
                .getElements();
    }

    private String getGrindableProductPackagesLocator() {
        return "td.price-count-2";
    }

    private SelenideElement getIncrementButtonFromQuantityCounter(SelenideElement counter) {
        return counter.find(By.cssSelector("div.input-group-append")).find(By.tagName("button"));
    }

    private SelenideElement getProductCountInput(SelenideElement counter) {
        return counter.find(By.cssSelector("div.input-wrap")).find(By.name("count"));
    }

    private int getCurrentProductCountValue(SelenideElement counter) {
        String value = getProductCountInput(counter).getDomProperty("value");
        return Integer.parseInt(Objects.requireNonNull(value));
    }

    private void clearInput(SelenideElement input) {
        input.sendKeys(Keys.CONTROL + "a");
        input.sendKeys(Keys.BACK_SPACE);
    }

    private void setInputValue(SelenideElement input, int amount) {
        String newValue = Integer.toString(amount);
        input.sendKeys(newValue);
    }

    private String getCategoryTitle(SelenideElement groupElement) {
        SelenideElement groupTitle = new TastyCoffeeWebPageElement(groupElement)
                .applySelector(PRODUCT_CATEGORY_TITLE)
                .getElement();
        return groupTitle.getAttribute("innerHTML");
    }

    private String getSubGroupTitle(SelenideElement subgroupElement) {
        SelenideElement subgroupTitle = new TastyCoffeeWebPageElement(subgroupElement)
                .applySelector(PRODUCT_SUBCATEGORY_TITLE)
                .getElement();
        return subgroupTitle.getAttribute("innerHTML");
    }

    private String getProductTitle(SelenideElement product) {
        SelenideElement productNameTableCell = new TastyCoffeeWebPageElement(product)
                .applySelector(PRODUCT_TITLE)
                .getElement();
        return productNameTableCell.getAttribute("innerHTML");
    }

    private String getSpecialMarkTitle(SelenideElement product) {
        SelenideElement specialMark = new TastyCoffeeWebPageElement(product)
                .applySelector(PRODUCT_MARK_TITLE)
                .getElement();
        return specialMark.getAttribute("data-div");
    }

    private BigDecimal getProductPrice(SelenideElement product) {
        return getPriceFromTableCell(product.find("div.coffee-week-price").getAttribute("innerHTML"));
    }

    private String getProductPackage(SelenideElement product) {
        return product.find("samp.mob").getAttribute("innerHTML");
    }

    private BigDecimal getPriceFromTableCell(String src) {
        src = src.replace("&nbsp;", "");
        src = src.replace(",", ".");
        return BigDecimal.valueOf(Double.parseDouble(src));
    }

}