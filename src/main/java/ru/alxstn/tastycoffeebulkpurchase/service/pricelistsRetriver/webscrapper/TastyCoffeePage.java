package ru.alxstn.tastycoffeebulkpurchase.service.pricelistsRetriver.webscrapper;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import ru.alxstn.tastycoffeebulkpurchase.configuration.TastyCoffeeConfigProperties;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.event.ProductFoundEvent;

import java.util.*;

import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

@Component
public class TastyCoffeePage {
    private final TastyCoffeeConfigProperties tastyCoffeeConfig;
    private final ApplicationEventPublisher newProductEventPublisher;

    public TastyCoffeePage(TastyCoffeeConfigProperties properties,
                           ApplicationEventPublisher newProductPublisher) {
        this.tastyCoffeeConfig = properties;
        this.newProductEventPublisher = newProductPublisher;

        Configuration.timeout = 10;
        Configuration.browserSize = "1920x1080";
        Configuration.browserPosition = "2x2";
        Configuration.headless = true;
    }

    public void login() {
        open(tastyCoffeeConfig.getUrl());
        SelenideElement bulkPurchaseClientLoginButton = $(byText("Вход для оптовых клиентов"));
        bulkPurchaseClientLoginButton.click();

        Selenide.sleep(1_000);

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

        Product.Builder productBuilder = new Product.Builder();

        for (SelenideElement mainAccordion : mainAccordions) {
            SelenideElement groupTitle = mainAccordion.find(By.cssSelector("h3.title"));
            productBuilder.setGroup(groupTitle.getAttribute("innerHTML"));

            ElementsCollection productAccordions = mainAccordion.findAll("div.accordion.accordion_product");
            for (SelenideElement productAccordion : productAccordions) {
                SelenideElement subgroupTitle = productAccordion.find(By.cssSelector("span"));
                productBuilder.setSubGroup(subgroupTitle.getAttribute("innerHTML"));

                SelenideElement productsTable = productAccordion.find(By.cssSelector("table.table-lk"));
                ElementsCollection tableRows = productsTable.findAll(By.cssSelector("tr.searchTab"));

                for (SelenideElement tableRow : tableRows) {
                    SelenideElement productNameTableCell = tableRow.find(By.cssSelector("button.font-13.p-0.selectable.btn.pointer"));
                    productBuilder.setName(productNameTableCell.getAttribute("innerHTML"));

                    try {
                        SelenideElement specialMark = tableRow.find(By.cssSelector("div.prefixdiv.empty-ic"));
                        productBuilder.setSpecialMark(specialMark.getAttribute("data-div"));
                    } catch (ElementNotFound ignored) {}

                    ArrayList<SelenideElement> prices = new ArrayList<>();
                    prices.addAll(tableRow.findAll(By.cssSelector("td.price-count-1")));
                    prices.addAll(tableRow.findAll(By.cssSelector("td.price-count-2")));

                    for (var productPriceTableCell : prices) {
                        try {
                            var price = getPriceFromTableCell(Objects.requireNonNull(
                                    productPriceTableCell.find("div.coffee-week-price").getAttribute("innerHTML")));
                            productBuilder.setPrice(price);
                            var pack = productPriceTableCell.find("samp.mob").getAttribute("innerHTML");
                            productBuilder.setPackage(pack);

                            newProductEventPublisher.publishEvent(new ProductFoundEvent(this, productBuilder.build()));
                            categoryProducts.add(productBuilder.build());
                        } catch (RuntimeException e) {
                            System.out.println("Error parsing product " + productBuilder.build().toString() + " Message: " + e.getMessage());
                        }
                    }
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
        return Double.parseDouble(src);
    }
}