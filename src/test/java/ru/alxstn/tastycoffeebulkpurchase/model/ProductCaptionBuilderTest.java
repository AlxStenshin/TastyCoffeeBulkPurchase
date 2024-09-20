package ru.alxstn.tastycoffeebulkpurchase.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.entity.ProductPackage;

import java.math.BigDecimal;

class ProductCaptionBuilderTest {

    private static final ProductPackage pack = new ProductPackage("Упаковка 250 г");
    private static final Product regularPackagedProductWithForm = new Product("fineGrind",
                    new BigDecimal(1),
                    "none",
                    pack,
                    "Group",
                    "Subgroup",
                    "Мелкий",
                    true);

    private static final Product regularPackagedProductWithFormAndNoMark = new Product("fineGrind",
                    new BigDecimal(1),
                    "",
                    pack,
                    "Group",
                    "Subgroup",
                    "Мелкий",
                    true);

    private static final Product regularPackagedProductNoFormNoMark = new Product("Product",
                    new BigDecimal(1),
                    "",
                    pack,
                    "Group",
                    "Subgroup",
                    "",
                    false);

    private static final Product unavailablePackagedProductWithForm = new Product("fineGrind",
                    new BigDecimal(1),
                    "нет",
                    pack,
                    "Group",
                    "Subgroup",
                    "Мелкий",
                    true);


    // Regular Product With Product Form And Package
    @Test
    void shouldSkipIconWithNonIconMarkedProducts() {
        Assertions.assertEquals(regularPackagedProductWithForm.getName(),
                new ProductCaptionBuilder(regularPackagedProductWithForm).createIconNameView());

        Assertions.assertEquals(regularPackagedProductNoFormNoMark.getName(),
                new ProductCaptionBuilder(regularPackagedProductNoFormNoMark).createIconNameView());
    }

    @Test
    void shouldContainIconWithEveryViewAndProductMarkedUnavailable() {

        Assertions.assertTrue(new ProductCaptionBuilder(
                unavailablePackagedProductWithForm).createIconNameView()
                .contains(unavailablePackagedProductWithForm.getIcon()));

        Assertions.assertTrue(new ProductCaptionBuilder(
                unavailablePackagedProductWithForm).createIconNameMarkPriceView()
                .contains(unavailablePackagedProductWithForm.getIcon()));

        Assertions.assertTrue(new ProductCaptionBuilder(
                unavailablePackagedProductWithForm).createIconNameMarkPackagePriceView()
                .contains(unavailablePackagedProductWithForm.getIcon()));

        Assertions.assertTrue(new ProductCaptionBuilder(
                unavailablePackagedProductWithForm).createIconNameMarkPackagePriceCatSubcatView()
                .contains(unavailablePackagedProductWithForm.getIcon()));

        Assertions.assertTrue(new ProductCaptionBuilder(
                unavailablePackagedProductWithForm).createIconCatSubcatNameMarkPackageFormView()
                .contains(unavailablePackagedProductWithForm.getIcon()));
    }

    @Test
    void shouldReturnNamePackageFormViewView() {
        Assertions.assertEquals(unavailablePackagedProductWithForm.getName() + " " +
                unavailablePackagedProductWithForm.getProductPackage() + " " +
                unavailablePackagedProductWithForm.getProductForm(),
                new ProductCaptionBuilder(unavailablePackagedProductWithForm).createNamePackageFormView());
    }

    @Test
    void shouldReturnIconNameMarkPriceView() {
        Assertions.assertEquals(unavailablePackagedProductWithForm.getIcon() + " " +
                unavailablePackagedProductWithForm.getName() + ", " +
                "'" + unavailablePackagedProductWithForm.getSpecialMark() + "', " +
                unavailablePackagedProductWithForm.getPrice() + "₽",
                new ProductCaptionBuilder(unavailablePackagedProductWithForm).createIconNameMarkPriceView());
    }

    @Test
    void shouldReturnIconNameMarkPackagePriceView() {
        Assertions.assertEquals(unavailablePackagedProductWithForm.getIcon() + " " +
                unavailablePackagedProductWithForm.getName() + " " +
                "'" + unavailablePackagedProductWithForm.getSpecialMark() + "', " +
                unavailablePackagedProductWithForm.getProductPackage().getDescription() + ", " +
                unavailablePackagedProductWithForm.getPrice() + "₽",
                new ProductCaptionBuilder(unavailablePackagedProductWithForm).createIconNameMarkPackagePriceView());
    }

    @Test
    void shouldSkipIconWithIconNameMarkPriceViewAndNoIconProduct() {
        Assertions.assertEquals(regularPackagedProductWithForm.getName() + ", " +
                "'" + regularPackagedProductWithForm.getSpecialMark() + "', " +
                        regularPackagedProductWithForm.getPrice() + "₽",
                new ProductCaptionBuilder(regularPackagedProductWithForm).createIconNameMarkPriceView());
    }

    @Test
    void shouldSkipSpecialMarkWithIconNameMarkPriceViewAndNoMarkProduct() {
        Assertions.assertEquals(regularPackagedProductWithFormAndNoMark.getName() + ", " +
                        regularPackagedProductWithFormAndNoMark.getPrice() + "₽",
                new ProductCaptionBuilder(regularPackagedProductWithFormAndNoMark).createIconNameMarkPriceView());
    }

    @Test
    void shouldReturnIconNameMarkPackagePriceCatSubcatView() {
        Assertions.assertEquals(unavailablePackagedProductWithForm.getIcon() + " " +
                        unavailablePackagedProductWithForm.getName() + " " +
                        "'" + unavailablePackagedProductWithForm.getSpecialMark() + "', " +
                        unavailablePackagedProductWithForm.getProductPackage().getDescription() + ", " +
                        unavailablePackagedProductWithForm.getPrice() + "₽" +
                        "\nИз категории " + unavailablePackagedProductWithForm.getProductCategory() + " " +
                        "\nПодкатегории " + unavailablePackagedProductWithForm.getProductSubCategory(),
                new ProductCaptionBuilder(unavailablePackagedProductWithForm).createIconNameMarkPackagePriceCatSubcatView());
    }

    @Test
    void shouldReturnIconCatSubcatNameMarkPackageFormView() {

        Assertions.assertEquals(unavailablePackagedProductWithForm.getIcon() + " " +
                        unavailablePackagedProductWithForm.getProductCategory() + ", " +
                        unavailablePackagedProductWithForm.getProductSubCategory() + ", " +
                        unavailablePackagedProductWithForm.getName() + ", " +
                        "'" + unavailablePackagedProductWithForm.getSpecialMark() + "', " +
                        unavailablePackagedProductWithForm.getProductPackage().getDescription() + ", " +
                        unavailablePackagedProductWithForm.getProductForm(),
                new ProductCaptionBuilder(unavailablePackagedProductWithForm).createIconCatSubcatNameMarkPackageFormView());
    }

}