package ru.alxstn.tastycoffeebulkpurchase.util;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
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
import ru.alxstn.tastycoffeebulkpurchase.entity.Purchase;
import ru.alxstn.tastycoffeebulkpurchase.event.ProductFoundEvent;
import ru.alxstn.tastycoffeebulkpurchase.exception.webPage.WebPageElementException;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

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
            SelenideElement tabsBar = $("ul.nav.tab-lk");
            ElementsCollection tabs = tabsBar.$$("li.nav-item");
            List<String> acceptedProductTypes = List.of("Кофе", "Чай", "Шоколад", "Сиропы");

            for (var tab : tabs) {

                SelenideElement link = tab.$("a");
                String text = link.innerHtml();
                if (acceptedProductTypes.stream().anyMatch(text::contains)) {
                    logger.info("Now Parsing Tab " + text);
                    clickWebElement(link.getWrappedElement());
                    expandAllProductGroups();
                    allProducts.addAll(parseProductType());
                } else {
                    logger.info("Ignoring Price List Tab " + text.replace("\n", ""));
                }

            }
        } catch (RuntimeException e) {
            logger.error("WebPageElementError: " + e.getMessage());
            throw new WebPageElementException(e);
        }
        return allProducts;
    }
    public List<Purchase> placeOrder(List<Purchase> currentSessionPurchases) {
        try {
            login();
            resetOrder();

            SelenideElement tabsBar = $("ul.nav.tab-lk");
            ElementsCollection tabs = tabsBar.$$("li.nav-item");
            List<String> acceptedCategories = List.of("Кофе", "Чай", "Шоколад", "Сиропы");

            for (var tab : tabs) {
                SelenideElement link = tab.$("a");
                String text = link.innerHtml();
                if (acceptedCategories.stream().anyMatch(text::contains)) {
                    logger.info("Now Filling Group " + text.replace("\n", ""));
                    clickWebElement(link.getWrappedElement());
                    expandAllProductGroups();
                    fillPurchasesAvailableOnPage(currentSessionPurchases);
                } else {
                    logger.info("Ignoring Price List Tab " + text.replace("\n", ""));
                }
            }
        } catch (RuntimeException e) {
            logger.error("WebPageElementError: " + e.getMessage());
            throw new WebPageElementException(e);
        }

        // ToDo: Check total sum and product count by groups (counters in "nav-link active")
        return currentSessionPurchases;
    }

    private void login() {
        try {
            open(tastyCoffeeConfig.getUrl());

            SelenideElement bulkPurchaseClientLoginButton = $(byText("Вход для оптовых клиентов"));
            bulkPurchaseClientLoginButton.click();
            Selenide.sleep(1_000);

            try {
                SelenideElement goToAccountButton = $(byText("Личный кабинет"));
                goToAccountButton.click();
                logger.info("User already logged in, login routine skipped");
            } catch (ElementNotFound ignored) {
                SelenideElement loginButton = $(byText("Войти"));

                SelenideElement usernameInput = $(byName("login"));
                usernameInput.setValue(tastyCoffeeConfig.getUserName());

                SelenideElement passwordInput = $(byName("password"));
                passwordInput.setValue(tastyCoffeeConfig.getPassword());

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
            SelenideElement resetButtonTextElement = $(byText("Сбросить заказ"));
            SelenideElement resetOrderButton = resetButtonTextElement.find(By.xpath("./.."));
            clickWebElement(resetOrderButton);
            try {
                SelenideElement confirmButtonText = $(byText("Да"));
                SelenideElement confirmButton = confirmButtonText.find(By.xpath("./.."));
                clickWebElement(confirmButton);
            } catch (ElementNotFound ignored) {
                logger.info("Confirm Reset Order button not found, is order empty already?");
            }
        } catch (RuntimeException e) {
            logger.error("WebPageElementError: " + e.getMessage());
            throw new WebPageElementException(e);
        }
    }

    public void expandAllProductGroups() {
        try {
            clickElements(byCssSelector("button.status.active"));
            Selenide.sleep(1_000);
        } catch (RuntimeException e) {
            logger.error("WebPageElementError: " + e.getMessage());
            throw new WebPageElementException(e);
        }
    }

    // https://stackoverflow.com/questions/52720560/selenide-removing-displayedfalse-not-working
    public void clickElements(By locator) {
        try {
            var driver = getWebDriver();
            for (WebElement elementToClick : driver.findElements(locator)) {
                clickWebElement(elementToClick);
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

    private void fillPurchasesAvailableOnPage(List<Purchase> currentSessionPurchases) {

        for (SelenideElement productGroup : getProductGroups()) {
            for (SelenideElement productSubgroup : getProductSubgroupsFromGroup(productGroup)) {
                String subgroupTitle = getSubGroupTitle(productSubgroup);

                List<Purchase> currentSubcategoryPurchases = currentSessionPurchases.stream()
                        .filter(p -> p.getProduct().getProductSubCategory().equals(subgroupTitle))
                        .collect(Collectors.toList());

                for (Purchase purchase : currentSubcategoryPurchases) {
                    try {
                        String productName = purchase.getProduct().getName();
                        String productPackage = purchase.getProduct().getProductPackage().getDescription();
                        String productForm = purchase.getProductForm();
                        int productCount = purchase.getCount();

                        SelenideElement correspondingProduct = productSubgroup.find(byText(productName));
                        SelenideElement correspondingProductTableRow = correspondingProduct.find(By.xpath("./../../.."));
                        SelenideElement counter = getQuantityControlElement(correspondingProductTableRow, productPackage, productForm);
                        SelenideElement incrementButton = getIncrementButtonFromQuantityCounter(counter);

                        while (productCount > 0) {
                            clickWebElement(incrementButton);
                            productCount--;
                            currentSessionPurchases.remove(purchase);
                        }
                    } catch (ElementNotFound ignored) {
                        logger.warn("Could Not Find Element By Text: " + purchase.getProduct().getName());
                    }
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

        if (productForm.isEmpty())
            // Beans or other non-grindable package
            return availableElements.get(priceElementIndex + 1);
        else {
            // Grindable Package
            return availableElements.get(priceElementIndex + 2);
        }
    }

    private List<Product> parseProductType() {
        List<Product> currentTypeProducts = new ArrayList<>();
        Product.ProductBuilder productBuilder = new Product.ProductBuilder();

        for (SelenideElement productGroup : getProductGroups()) {
            productBuilder.setCategory(getGroupTitle(productGroup));
            for (SelenideElement productSubgroup : getProductSubgroupsFromGroup(productGroup)) {
                productBuilder.setSubCategory(getSubGroupTitle(productSubgroup));
                for (SelenideElement product : getProductsListFromSubgroup(productSubgroup)) {
                    productBuilder.setName(getProductTitle(product));
                    try {
                        productBuilder.setSpecialMark(getSpecialMarkTitle(product));
                    } catch (ElementNotFound ignored) {
                        productBuilder.setSpecialMark("");
                    }

                    productBuilder.setGrindable(false);
                    addProducts(currentTypeProducts, productBuilder, getProductPacks(product));

                    productBuilder.setGrindable(true);
                    addProducts(currentTypeProducts, productBuilder, getGrindableProductPacks(product));
                }
            }
        }

        logger.info("Price List Parsing Complete! Found: " + currentTypeProducts.size() + " items.");
        return currentTypeProducts;
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

    private ElementsCollection getProductGroups() {
        return $$("div.main-accordion");
    }

    private ElementsCollection getProductSubgroupsFromGroup(SelenideElement productGroup) {
        return productGroup.$$("div.accordion.accordion_product");
    }

    private ElementsCollection getProductsListFromSubgroup(SelenideElement productSubgroup) {
        SelenideElement productsTable = productSubgroup.find(By.cssSelector("table.table-lk"));
        return productsTable.findAll(By.cssSelector("tr.searchTab"));
    }

    private List<SelenideElement> getProductPacks(SelenideElement product) {
        return new ArrayList<>(product.findAll(By.cssSelector("td.price-count-1")));
    }

    private List<SelenideElement> getGrindableProductPacks(SelenideElement product) {
        return new ArrayList<>(product.findAll(By.cssSelector("td.price-count-2")));
    }

    private SelenideElement getIncrementButtonFromQuantityCounter(SelenideElement counter) {
        return counter.find(By.cssSelector("div.input-group-append")).find(By.tagName("button"));
    }

    private String getGroupTitle(SelenideElement groupElement) {
        SelenideElement groupTitle = groupElement.find(By.cssSelector("h3.title"));
        return groupTitle.getAttribute("innerHTML");
    }

    private String getSubGroupTitle(SelenideElement subgroupElement) {
        SelenideElement subgroupTitle = subgroupElement.find(By.cssSelector("span"));
        return subgroupTitle.getAttribute("innerHTML");
    }

    private String getProductTitle(SelenideElement product) {
        SelenideElement productNameTableCell = product.find(By.cssSelector("button.font-13.p-0.selectable.btn.pointer"));
        return productNameTableCell.getAttribute("innerHTML");
    }

    private String getSpecialMarkTitle(SelenideElement product) {
        SelenideElement specialMark = product.find(By.cssSelector("div.prefixdiv.empty-ic"));
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