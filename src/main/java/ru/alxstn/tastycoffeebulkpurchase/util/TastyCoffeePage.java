package ru.alxstn.tastycoffeebulkpurchase.util;

import com.codeborne.selenide.*;
import com.codeborne.selenide.ex.ElementNotFound;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import ru.alxstn.tastycoffeebulkpurchase.configuration.TastyCoffeeConfigProperties;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.entity.ProductPackage;
import ru.alxstn.tastycoffeebulkpurchase.event.ProductFoundEvent;
import ru.alxstn.tastycoffeebulkpurchase.exception.webPage.WebPageElementException;
import ru.alxstn.tastycoffeebulkpurchase.model.ProductBuilder;
import ru.alxstn.tastycoffeebulkpurchase.model.ProductCaptionBuilder;
import ru.alxstn.tastycoffeebulkpurchase.model.TastyCoffeeWebPageElement;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static ru.alxstn.tastycoffeebulkpurchase.model.TastyCoffeePageElementSelector.*;

@Component
public class TastyCoffeePage {
    Logger logger = LogManager.getLogger(TastyCoffeePage.class);

    private final ApplicationEventPublisher publisher;
    private final TastyCoffeeConfigProperties tastyCoffeeConfig;
    private final List<String> acceptedProductTypes = List.of("Кофе", "Чай", "Шоколад", "Сиропы");

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
        //resetOrder();
        List<Product> allProducts = new ArrayList<>();
        try {
            ElementsCollection tabs = new TastyCoffeeWebPageElement()
                    .applySelector(PRODUCT_TYPE_BAR)
                    .applySelector(PRODUCT_TYPES)
                    .getElements();

            for (var tab : tabs) {
                SelenideElement productTypeLink = new TastyCoffeeWebPageElement(tab).applySelector(LINK).getElement();
                String text = productTypeLink.innerHtml();
                if (acceptedProductTypes.stream().anyMatch(text::contains)) {
                    logger.info("Now Parsing Tab: " + StringUtil.removeNonAlphanumeric(text));
                    clickWebElementWithJS(productTypeLink.getWrappedElement());
                    expandAllProductSubCategories();
                    allProducts.addAll(parseAllProductsOfType());
                } else {
                    logger.info("Ignoring Price List Tab " + StringUtil.removeNonAlphanumeric(text));
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
        login();
        resetOrder();
        try {
            ElementsCollection tabs = new TastyCoffeeWebPageElement()
                    .applySelector(PRODUCT_TYPE_BAR)
                    .applySelector(PRODUCT_TYPES)
                    .getElements();

            for (var tab : tabs) {
                SelenideElement productTypeLink = new TastyCoffeeWebPageElement(tab).applySelector(LINK).getElement();
                String productType = productTypeLink.innerHtml();
                if (acceptedProductTypes.stream().anyMatch(productType::contains)) {
                    logger.info("Now Filling Tab: " + StringUtil.removeNonAlphanumeric(productType));
                    clickWebElementWithJS(productTypeLink.getWrappedElement());
                    expandAllProductSubCategories();
                    fillPurchasesAvailableOnPage(currentSessionPurchases);
                } else {
                    logger.info("Ignoring Price List Tab " + productType.replace("\n", ""));
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
            Selenide.sleep(15_000);
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
            clickWebElementWithJS(resetOrderButton);
            try {
                SelenideElement confirmButton = new TastyCoffeeWebPageElement()
                        .applySelector(CONFIRM_BUTTON_TEXT)
                        .applySelector(ONE_LEVEL_UP)
                        .getElement();
                clickWebElementWithJS(confirmButton);
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
                clickWebElementWithJS(element);
            }
        } catch (RuntimeException e) {
            logger.error("WebPageElementError: " + e.getMessage());
            throw new WebPageElementException(e);
        }
    }

    private void clickWebElementWithJS(WebElement element) {
        try {
            String jsClickCode = "arguments[0].scrollIntoView(true); arguments[0].click();";
            var driver = getWebDriver();
            ((JavascriptExecutor) driver).executeScript(jsClickCode, element);
        } catch (Exception e) {
            logger.warn("Element could not be clicked.. " + e.getMessage());
        }
    }

    private void addProducts(List<Product> categoryProducts, ProductBuilder productBuilder, List<SelenideElement> products) {
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

        List<String> productSubCategoriesAvailableOnPage = new ArrayList<>();
        for (SelenideElement productCategory : new TastyCoffeeWebPageElement()
                .applySelector(PRODUCT_CATEGORIES)
                .getElements()) {
            for (SelenideElement productSubCategory : new TastyCoffeeWebPageElement(productCategory)
                    .applySelector(PRODUCT_SUBCATEGORIES)
                    .getElements()) {
                productSubCategoriesAvailableOnPage.add(getSubGroupTitle(productSubCategory));
            }
        }

        Map<Product, Integer> currentProductTypePurchases = currentSessionPurchases.entrySet().stream()
                .filter(p -> productSubCategoriesAvailableOnPage.stream()
                        .anyMatch(p.getKey().getProductSubCategory()::contentEquals))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        for (var purchase : currentProductTypePurchases.entrySet()) {
            Product product = purchase.getKey();
            int productCountIncrement = purchase.getValue();
            try {
               if (setCountWithIncrementButton(product, productCountIncrement)) {
                   currentSessionPurchases.remove(product);
               } else logger.warn("Could Not Increment Count for " +
                       new ProductCaptionBuilder(product).createCatSubcatNameMarkPackageView() +
                       "Req Count: " + productCountIncrement);

            } catch (ElementNotFound e) {
                logger.warn("Could Not Find Element: " + e.getMessage() + "\nFor product" + product);
            }
        }
    }

    boolean setCountWithIncrementButton(Product product, int productCountIncrement) {
        SelenideElement counter = findProductCounter(product);
        int currentProductCount = getCurrentProductCountValue(counter);
        int targetProductCount = currentProductCount + productCountIncrement;
        SelenideElement incrementButton = getIncrementButtonFromQuantityCounter(counter);

        logger.info(
                "Starting product count increase for product: " +
                        ". Current count: " + currentProductCount +
                        "  increments: " + productCountIncrement);

        int retries = 0;
        while (productCountIncrement > 0) {
            clickWebElementWithJS(incrementButton);

            int newProductCountValue = getCurrentProductCountValue(findProductCounter(product));
            if (newProductCountValue == currentProductCount + 1) {
                logger.info(new ProductCaptionBuilder(product).createCatSubcatNameMarkPackageView() +
                        " Increment Succeed. New Value: " + newProductCountValue);
                productCountIncrement--;
                currentProductCount++;
            } else {
                logger.warn(new ProductCaptionBuilder(product).createCatSubcatNameMarkPackageView() +
                        " Increment Failed. Retries: " + retries);
                retries++;
            }
            if (retries > 5)
                return false;
        }

        int finalValidationCount = getCurrentProductCountValue(findProductCounter(product));
        if (finalValidationCount != targetProductCount) {
            logger.warn("Product count validation failed, retrying incrementation: ");
            setCountWithIncrementButton(product, targetProductCount - finalValidationCount);
        }

        return true;
    }
        private SelenideElement findProductCounter(Product product) {
        String threeLevelsUp = "./../../..";
        SelenideElement subCatText = $(byText(product.getProductSubCategory()));
        SelenideElement subCat = subCatText.$(By.xpath(threeLevelsUp));
        SelenideElement productText = subCat.$(byText(product.getName()));
        SelenideElement prodRow = productText.find(By.xpath(threeLevelsUp));

        return getQuantityControlElementFromProductRow(prodRow,
                product.getProductPackage().getDescription(),
                product.getProductForm());
    }

    private SelenideElement getQuantityControlElementFromProductRow(SelenideElement productTableRow, String productPackage, String productForm) {

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
        ProductBuilder productBuilder = new ProductBuilder();
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

    private List<SelenideElement> getRegularProductPacks(SelenideElement product) {
        return new TastyCoffeeWebPageElement(product)
                .applySelector(PRODUCT_REGULAR_PACKAGE)
                .getElements();
    }

    private List<SelenideElement> getGrindableProductPacks(SelenideElement product) {
        return new TastyCoffeeWebPageElement(product)
                .applySelector(PRODUCT_GRINDABLE_PACKAGE)
                .getElements();
    }

    private SelenideElement getProductCountInput(SelenideElement counter) {
        return counter.find(By.cssSelector("div.input-wrap")).find(By.name("count"));
    }

    private SelenideElement getIncrementButtonFromQuantityCounter(SelenideElement counter) {
        return counter.find(By.cssSelector("div.input-group-append")).find(By.tagName("button"));
    }

    private int getCurrentProductCountValue(SelenideElement counter) {
        String value = getProductCountInput(counter).getDomProperty("value");
        return Integer.parseInt(Objects.requireNonNull(value));
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