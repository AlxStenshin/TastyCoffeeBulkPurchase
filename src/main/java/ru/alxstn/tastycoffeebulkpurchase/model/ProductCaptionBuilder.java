package ru.alxstn.tastycoffeebulkpurchase.model;

import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.util.StringUtil;

import static ru.alxstn.tastycoffeebulkpurchase.model.CaptionBuilderSeparator.*;

public class ProductCaptionBuilder {

    private final StringBuilder captionBuilder;
    private final Product product;
    private boolean separatorApplied;

    public ProductCaptionBuilder(Product product) {
        this.product = product;
        captionBuilder = new StringBuilder();
    }

    public String createIconNameView() {
        return icon().separator(SPACE).name().build();
    }

    public String createIconNameMarkPriceView() {
        return icon().separator(SPACE)
                .name().separator(SPACE)
                .mark().separator(COMMA_SPACE)
                .price().rouble()
                .build();
    }

    public String createIconNameMarkPackagePriceView() {
        return icon().separator(SPACE)
                .name().separator(SPACE)
                .mark().separator(COMMA_SPACE)
                .pack().separator(COMMA_SPACE)
                .price().rouble()
                .build();
    }

    public String createIconNameMarkPackagePriceCatSubcatView() {
        return icon().separator(SPACE)
                .name().separator(SPACE)
                .mark().separator(COMMA_SPACE)
                .pack().separator(COMMA_SPACE)
                .price().rouble().separator(SPACE)
                .titledCat().separator(SPACE)
                .titledSubcat().separator(SPACE)
                .build();
    }

    public String createIconCatSubcatNameMarkPackageFormView() {
        return icon().separator(SPACE)
                .cat().separator(COMMA_SPACE)
                .subcat().separator(COMMA_SPACE)
                .name().separator(COMMA_SPACE)
                .mark().separator(COMMA_SPACE)
                .pack().separator(COMMA_SPACE)
                .form()
                .build();
    }
    public String createCatSubcatNameMarkPackageView() {
        return cat().separator(COMMA_SPACE)
                .subcat().separator(COMMA_SPACE)
                .name().separator(COMMA_SPACE)
                .mark().separator(COMMA_SPACE)
                .pack().separator(COMMA_SPACE)
                .build();
    }

    public String createCatSubcatNameMarkView() {
        return cat().separator(COMMA_SPACE)
                .subcat().separator(COMMA_SPACE)
                .name().separator(SPACE)
                .mark()
                .build();
    }

    private ProductCaptionBuilder icon() {
        captionBuilder.append(product.getIcon());
        separatorApplied = false;
        return this;
    }
    private ProductCaptionBuilder name() {
        captionBuilder.append(product.getName());
        separatorApplied = false;
        return this;
    }
    private ProductCaptionBuilder mark() {
        captionBuilder.append(product.getSpecialMark().isEmpty() ? "" : "'" + product.getSpecialMark() + "'");
        separatorApplied = false;
        return this;
    }
    private ProductCaptionBuilder price() {
        captionBuilder.append(product.getPrice());
        separatorApplied = false;
        return this;
    }
    private ProductCaptionBuilder pack() {
        String pack = product.getProductPackage().getDescription();
        captionBuilder.append(pack.isEmpty() ? "" : pack);
        separatorApplied = false;
        return this;
    }
    private ProductCaptionBuilder titledCat() {
        captionBuilder.append(product.getProductCategory().isEmpty() ? "" :
                "\nИз категории " + product.getProductCategory());
        separatorApplied = false;
        return this;
    }
    private ProductCaptionBuilder titledSubcat() {
        captionBuilder.append(product.getProductSubCategory().isEmpty() ? "" :
                "\nПодкатегории " + product.getProductSubCategory());
        separatorApplied = false;
        return this;
    }
    private ProductCaptionBuilder cat() {
        captionBuilder.append(product.getProductCategory().isEmpty() ? "" :
                product.getProductCategory());
        separatorApplied = false;
        return this;
    }
    private ProductCaptionBuilder subcat() {
        captionBuilder.append(product.getProductSubCategory().isEmpty() ? "" :
                product.getProductSubCategory());
        separatorApplied = false;
        return this;
    }
    private ProductCaptionBuilder form() {
        captionBuilder.append(product.getProductForm().isEmpty() ? "" : product.getProductForm());
        separatorApplied = false;
        return this;
    }
    private ProductCaptionBuilder rouble() {
        captionBuilder.append("₽");
        separatorApplied = false;
        return this;
    }
    private ProductCaptionBuilder separator(CaptionBuilderSeparator separator) {
        if (!separatorApplied) {
            captionBuilder.append(separator.getValue());
            separatorApplied = true;
        }
        return this;
    }

    private String build() {
        return StringUtil.removeExtraSpaces(captionBuilder.toString());
    }

}
