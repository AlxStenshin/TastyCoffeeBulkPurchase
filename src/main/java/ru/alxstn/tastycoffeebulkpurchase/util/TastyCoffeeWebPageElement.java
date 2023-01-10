package ru.alxstn.tastycoffeebulkpurchase.util;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class TastyCoffeeWebPageElement {

    private SelenideElement element;
    private ElementsCollection elements;

    public TastyCoffeeWebPageElement() {
    }

    public TastyCoffeeWebPageElement(SelenideElement element) {
        this.element = element;
    }

    public TastyCoffeeWebPageElement(ElementsCollection elements) {
        this.elements = elements;
    }

    public TastyCoffeeWebPageElement applySelector(TastyCoffeePageElementSelector selector) {
        if (element == null && elements == null) {
            switch (selector.getSelectorType()) {
                case "text" -> {
                    return selector.isExpectingList() ?
                            new TastyCoffeeWebPageElement($$(byText(selector.getSelector()))) :
                            new TastyCoffeeWebPageElement($(byText(selector.getSelector())));
                }
                case "name" -> {
                    return selector.isExpectingList() ?
                            new TastyCoffeeWebPageElement($$(byName(selector.getSelector()))) :
                            new TastyCoffeeWebPageElement($(byName(selector.getSelector())));
                }
                case "css" -> {
                    return selector.isExpectingList() ?
                            new TastyCoffeeWebPageElement($$(byCssSelector(selector.getSelector()))) :
                            new TastyCoffeeWebPageElement($(byCssSelector(selector.getSelector())));
                }
                case "xpath" -> {
                    return selector.isExpectingList() ?
                            new TastyCoffeeWebPageElement($$(byXpath(selector.getSelector()))) :
                            new TastyCoffeeWebPageElement($(byXpath(selector.getSelector())));
                }
            }

        } else if (element != null) {
            switch (selector.getSelectorType()) {
                case "text" -> {
                    return selector.isExpectingList() ?
                            new TastyCoffeeWebPageElement(element.$$(byText(selector.getSelector()))) :
                            new TastyCoffeeWebPageElement(element.$(byText(selector.getSelector())));
                }
                case "name" -> {
                    return selector.isExpectingList() ?
                            new TastyCoffeeWebPageElement(element.$$(byName(selector.getSelector()))) :
                            new TastyCoffeeWebPageElement(element.$(byName(selector.getSelector())));
                }
                case "css" -> {
                    return selector.isExpectingList() ?
                            new TastyCoffeeWebPageElement(element.$$(byCssSelector(selector.getSelector()))) :
                            new TastyCoffeeWebPageElement(element.$(byCssSelector(selector.getSelector())));
                }
                case "xpath" -> {
                    return selector.isExpectingList() ?
                            new TastyCoffeeWebPageElement(element.$$(byXpath(selector.getSelector()))) :
                            new TastyCoffeeWebPageElement(element.$(byXpath(selector.getSelector())));
                }
            }

        } else {
            // element collections is the source
        }

        return null;
    }

    public SelenideElement getElement() {
        return element;
    }

    public ElementsCollection getElements() {
        return elements;
    }
}
