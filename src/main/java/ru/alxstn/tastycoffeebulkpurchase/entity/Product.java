package ru.alxstn.tastycoffeebulkpurchase.entity;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ru.alxstn.tastycoffeebulkpurchase.annotation.JsonExclude;
import ru.alxstn.tastycoffeebulkpurchase.model.ProductCaptionBuilder;
import ru.alxstn.tastycoffeebulkpurchase.util.StringUtil;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// ToDo: Normalize table, separate productCategory, productSubCategory and productMark entities.

@Entity
@Table(name = "product")
public class Product {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonExclude
    private long id;

    @Column(name = "name")
    private String name;

    @CreationTimestamp
    @Column(name = "date_created", updatable = false, nullable = false)
    @JsonExclude
    private LocalDateTime dateCreated;

    @Column(name = "date_updated")
    @UpdateTimestamp
    @JsonExclude
    private LocalDateTime dateUpdated;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "mark")
    private String specialMark;

    // ToDo: Add ProcessingType field

    @ManyToOne(fetch = FetchType.EAGER)
    @Cascade(CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "product_package_id")
    private ProductPackage productPackage;

    @Column(name = "category")
    private String productCategory;

    @Column(name = "subcategory")
    private String productSubCategory;

    @Column(name = "actual")
    @JsonExclude
    private boolean actual;
    
    @Column(name = "grindable")
    @JsonExclude
    private boolean grindable;

    @Column(name = "product_form")
    private String productForm;

    public Product() { }

    public Product(String name,
                   BigDecimal price,
                   String specialMark,
                   ProductPackage productPackage,
                   String productGroup,
                   String productSubGroup,
                   String productForm,
                   boolean grindable) {
        this.name = name;
        this.price = price;
        this.specialMark = specialMark;
        this.productPackage = productPackage;
        this.productCategory = productGroup;
        this.productSubCategory = productSubGroup;
        this.productForm = productForm;
        this.grindable = grindable;
        this.actual = true;
    }

    public Product createFormedProduct(String form) {
        return new Product(
                this.getName(),
                this.getPrice(),
                this.getSpecialMark(),
                this.getProductPackage(),
                this.getProductCategory(),
                this.getProductSubCategory(),
                form,
                this.grindable);

    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal  price) {
        this.price = price;
    }

    public String getSpecialMark() {
        return specialMark;
    }

    public void setSpecialMark(String specialMark) {
        this.specialMark = specialMark;
    }

    public ProductPackage getProductPackage() {
        return productPackage;
    }

    public void setProductPackage(ProductPackage productPackage) {
        this.productPackage = productPackage;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productGroup) {
        this.productCategory = productGroup;
    }

    public String getProductSubCategory() {
        return productSubCategory;
    }

    public void setProductSubCategory(String productSubGroup) {
        this.productSubCategory = productSubGroup;
    }

    public String getProductForm() {
        return productForm;
    }

    public void setProductForm(String productForm) {
        this.productForm = productForm;
    }

    public boolean isActual() {
        return actual;
    }

    public void setActual(boolean actual) {
        this.actual = actual;
    }

    public boolean isGrindable() {
        return grindable;
    }

    public void setGrindable(boolean grindable) {
        this.grindable = grindable;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDateTime getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(LocalDateTime dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    @Override
    public String toString() {
        return new ProductCaptionBuilder(this).createIconNameMarkPackagePriceCatSubcatView();
    }

    public boolean isDiscountable() {
        return (!getSpecialMark().equals("Сорт недели") &&
                !getSpecialMark().equals("Микролот недели") &&
                (getProductCategory().equals("КОФЕ ДЛЯ ФИЛЬТРА") ||
                getProductCategory().equals("КОФЕ ДЛЯ ЭСПРЕССО") ||
                getProductCategory().equals("КОФЕ ДЛЯ МОЛОЧНЫХ НАПИТКОВ")));
    }

    public boolean isWeightableCoffee() {
        return (getProductCategory().equals("КОФЕ ДЛЯ ФИЛЬТРА") ||
                getProductCategory().equals("КОФЕ ДЛЯ ЭСПРЕССО") ||
                getProductCategory().equals("КОФЕ ДЛЯ МОЛОЧНЫХ НАПИТКОВ"));
    }

    public boolean isTea() {
        return getProductCategory().equals("Чай");
    }

    public boolean isAvailable() {
        return !getSpecialMark().equals("нет") && !StringUtil.containsDate(this.getSpecialMark());
    }

    public boolean isSpecialOffer() {
        return getSpecialMark().equals("Сорт недели") ||
                getSpecialMark().equals("Микролот недели") ||
                getSpecialMark().equals("Сорт месяца") ;
    }

    public String getIcon() {

        if (isSpecialOffer())
            return "⭐️";

        // 🚫
        if (!isAvailable() || !isActual())
            return "\uD83D\uDEAB";

        // 🆕
        if (specialMark.equals("Новый"))
            return "\uD83C\uDD95";

        // Low Battery
        if (specialMark.equals("Заканчивается"))
            return "\uD83E\uDEAB";

        // 👍
        if (specialMark.equals("Рекомендуем"))
            return"\uD83D\uDC4D";

        // 🔥
        if (specialMark.equals("Популярный"))
            return "\uD83D\uDD25️";

        return "";
    }
}
