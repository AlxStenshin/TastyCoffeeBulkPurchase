package ru.alxstn.tastycoffeebulkpurchase.service.pricelistsRetriver.webscrapper;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;
import ru.alxstn.tastycoffeebulkpurchase.configuration.TastyCoffeeConfigProperties;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;

import java.util.*;

import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

@Component
public class TastyCoffeePage {
    private final TastyCoffeeConfigProperties tastyCoffeeConfig;

    public TastyCoffeePage(TastyCoffeeConfigProperties properties) {
        Configuration.timeout = 10;
        Configuration.browserSize = "1920x1080";
        Configuration.browserPosition = "2x2";
        Configuration.headless = true;
        tastyCoffeeConfig = properties;
    }

    public void login() {
        open(tastyCoffeeConfig.getUrl());
        SelenideElement bulkPurchaseClientLoginButton = $(byText("Вход для оптовых клиентов"));
        bulkPurchaseClientLoginButton.click();

        SelenideElement usernameInput = $(byName("login"));
        usernameInput.setValue(tastyCoffeeConfig.getUserName());

        SelenideElement passwordInput = $(byName("password"));
        passwordInput.setValue(tastyCoffeeConfig.getPassword());

        SelenideElement loginButton = $(byText("Войти"));
        loginButton.click();

        Selenide.sleep(10_000);
    }

    public List<Product> buildPriceList() {

        List<Product> allProducts = new ArrayList<>();

        SelenideElement tabsBar = $("ul.nav.tab-lk");
        ElementsCollection tabs = tabsBar.$$("li.nav-item");
        List<String> acceptedCategories = List.of("Кофе", "Чай", "Шоколад", "Сиропы");

        for (var tab : tabs) {
            SelenideElement link = tab.$("a");
            String text = link.innerHtml();
            if (acceptedCategories.stream().anyMatch(text::contains)) {
                System.out.println("Now Parsing Group " + text);
                clickWebElement(link.getWrappedElement());
                expandAllAccordions();
                allProducts.addAll(parseAccordions());
            } else {
                System.out.println("Ignoring Price List Tab " + text);
            }
        }

        return allProducts;
    }

    public void expandAllAccordions() {
        clickElements(byCssSelector("button.status.active"));
        Selenide.sleep(1_000);
    }

    // https://stackoverflow.com/questions/52720560/selenide-removing-displayedfalse-not-working
    public void clickElements(By locator) {
        var driver = getWebDriver();
        for (WebElement elementToClick : driver.findElements(locator)) {
            clickWebElement(elementToClick);
        }
    }

    private void clickWebElement(WebElement element) {
        try {
            String jsClickCode = "arguments[0].scrollIntoView(true); arguments[0].click();";
            var driver = getWebDriver();
            ((JavascriptExecutor) driver).executeScript(jsClickCode, element);
        } catch (Exception e) {
            System.out.println("Element could not be clicked.. " + e.getMessage());
        }
    }

    public List<Product> parseAccordions() {
        List<Product> categoryProducts = new ArrayList<>();
        ElementsCollection mainAccordions = $$("div.main-accordion");

        for (SelenideElement mainAccordion : mainAccordions) {
            SelenideElement groupTitle = mainAccordion.find(By.cssSelector("h3.title"));
            String productGroup = groupTitle.getAttribute("innerHTML");

            ElementsCollection productAccordions = mainAccordion.findAll("div.accordion.accordion_product");
            for (SelenideElement productAccordion : productAccordions) {

                SelenideElement subgroupTitle = productAccordion.find(By.cssSelector("span"));
                String productSubgroup = subgroupTitle.getAttribute("innerHTML");

                SelenideElement productsTable = productAccordion.find(By.cssSelector("table.table-lk"));
                ElementsCollection tableRows = productsTable.findAll(By.cssSelector("tr.searchTab"));

                for (SelenideElement tableRow : tableRows) {
                    SelenideElement productNameTableCell = tableRow.find(By.cssSelector("button.font-13.p-0.selectable.btn.pointer"));
                    String productName = productNameTableCell.getAttribute("innerHTML");

                    Product product = new Product(productGroup, productSubgroup, productName);
                    try {
                        SelenideElement specialMark = tableRow.find(By.cssSelector("div.prefixdiv.empty-ic"));
                        String mark = specialMark.getAttribute("data-div");
                        product.setSpecialMark(mark);
                    } catch (ElementNotFound ignored) {}

                    ArrayList<SelenideElement> prices = new ArrayList<>();
                    prices.addAll(tableRow.findAll(By.cssSelector("td.price-count-1")));
                    prices.addAll(tableRow.findAll(By.cssSelector("td.price-count-2")));

                    for (var productPriceTableCell : prices) {
                        try {
                            var cost = getPriceFromTableCell(Objects.requireNonNull(
                                    productPriceTableCell.find("div.coffee-week-price").getAttribute("innerHTML")));
                            var pack = productPriceTableCell.find("samp.mob").getAttribute("innerHTML");
                            if (cost != 0) {
                                product.addPrice(pack, cost);
                            }
                        } catch (ElementNotFound e) {
                            System.out.println("Element not found in field. " + e.getMessage());
                        }
                    }
                    System.out.println(product);
                    categoryProducts.add(product);
                }
            }
        }
        System.out.println("Price List Parsing Complete! Found: " + categoryProducts.size() + " items.");
        return categoryProducts;
    }

    double getPriceFromTableCell(String src) {
        src = src.replace("&nbsp;", "");
        src = src.replace(",", ".");
        double ret = 0;
        try {
            ret = Double.parseDouble(src);
        } catch (NumberFormatException e) {
            System.out.println("Error parsing price field from string '" + src + "', error: " + e.getMessage());
        }
        return ret;
    }
}