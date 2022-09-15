package ru.alxstn.tastycoffeebulkpurchase.service.pricelistsRetriver.webscrapper;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;
import ru.alxstn.tastycoffeebulkpurchase.configuration.TastyCoffeeConfigProperties;

import java.util.HashSet;
import java.util.Set;

import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

@Component
public class TastyCoffeePage {
    private final TastyCoffeeConfigProperties tastyCoffeeConfig;
    private final SelenideElement bulkPurchaseClientLoginButton = $(byText("Вход для оптовых клиентов"));
    private final SelenideElement usernameInput = $(byName("login"));
    private final SelenideElement passwordInput = $(byName("password"));
    private final SelenideElement loginButton = $(byText("Войти"));

    public TastyCoffeePage(TastyCoffeeConfigProperties properties) {
        tastyCoffeeConfig = properties;
    }

    public void login() {
        open(tastyCoffeeConfig.getUrl());
        bulkPurchaseClientLoginButton.click();
        usernameInput.setValue(tastyCoffeeConfig.getUserName());
        passwordInput.setValue(tastyCoffeeConfig.getPassword());
        loginButton.click();
        Selenide.sleep(5_000);
    }

    // https://stackoverflow.com/questions/52720560/selenide-removing-displayedfalse-not-working
    public void clickAllElementsWithJS(By locator) {
        String jsClickCode = "arguments[0].scrollIntoView(true); arguments[0].click();";
        try {
            var driver = getWebDriver();
            for (WebElement elementToClick : driver.findElements(locator)) {
                ((JavascriptExecutor) driver).executeScript(jsClickCode, elementToClick);
            }
        } catch (Exception e) {
            System.out.println("Element could not be clicked.. " + e.getMessage());
        }
    }

    public void expandAll() {
        clickAllElementsWithJS(byCssSelector("button.status.active"));
        Selenide.sleep(1_000);
        parseAccordions();
    }

    public void parseAccordions() {
        ElementsCollection mainAccordions = $$("div.main-accordion");
        for (SelenideElement mainAccordion : mainAccordions) {
            SelenideElement mainTitle = mainAccordion.find(By.cssSelector("h3.title"));
            String productGroup = mainTitle.getAttribute("innerHTML");
            //System.out.println("Product Group: " + productGroup);

            ElementsCollection productAccordions = mainAccordion.findAll("div.accordion.accordion_product");
            for (SelenideElement productAccordion : productAccordions) {

                SelenideElement title = productAccordion.find(By.cssSelector("span"));
                String productSubgroup = title.getAttribute("innerHTML");
                //System.out.println("Product Subgroup: " + productSubgroup);

                SelenideElement productsTable = productAccordion.find(By.cssSelector("table.table-lk"));
                ElementsCollection packaging = productsTable.findAll(withText("Упаковка"));
                Set<String> packagingOptions = new HashSet<>();
                for (SelenideElement pack : packaging) {
                    String packValue = pack.getAttribute("innerHTML");
                    packagingOptions.add(packValue);
                }

                System.out.println("Group: " + productGroup + " Subgroup: " + productSubgroup + "\n Packaging Options: " + packagingOptions);
            }
        }
    }
}