package ru.alxstn.tastycoffeebulkpurchase.model;

import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.entity.ProductPackage;

import java.math.BigDecimal;

public class ProductBuilder {
    private String group;
    private String subgroup;
    private ProductPackage pack;
    private String specialMark;
    private String name;
    private BigDecimal price;
    private String productForm;
    private boolean grindable;

    public ProductBuilder() {}

    public ProductBuilder setCategory(String cat) {
        this.group = cat;
        return this;
    }

    public ProductBuilder setSubCategory(String subCat) {
        this.subgroup = subCat;
        return this;
    }

    public ProductBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ProductBuilder setSpecialMark(String specialMark) {
        this.specialMark = specialMark;
        return this;
    }

    public ProductBuilder setPackage(ProductPackage pack) {
        this.pack = pack;
        return this;
    }

    public ProductBuilder setPrice(BigDecimal  price) {
        this.price = price;
        return this;
    }

    public ProductBuilder setProductForm(String productForm) {
        this.productForm = productForm;
        return this;
    }

    public ProductBuilder setGrindable(boolean grindable) {
        this.grindable = grindable;
        return this;
    }

    public Product build() {
        return new Product(name, price, specialMark, pack, group, subgroup, productForm, grindable);
    }
}