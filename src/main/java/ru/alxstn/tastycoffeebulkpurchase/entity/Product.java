package ru.alxstn.tastycoffeebulkpurchase.entity;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ru.alxstn.tastycoffeebulkpurchase.annotation.JsonExclude;
import ru.alxstn.tastycoffeebulkpurchase.util.StringUtil;

import javax.persistence.*;
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
        return getDisplayName();
    }

    public boolean isDiscountable() {
        return (!getSpecialMark().equals("???????? ????????????") &&
                (getProductCategory().equals("???????? ?????? ??????????????") ||
                getProductCategory().equals("???????? ?????? ????????????????") ||
                getProductCategory().equals("???????? ?????? ???????????????? ????????????????")));
    }

    public boolean isWeightableCoffee() {
        return (getProductCategory().equals("???????? ?????? ??????????????") ||
                getProductCategory().equals("???????? ?????? ????????????????") ||
                getProductCategory().equals("???????? ?????? ???????????????? ????????????????"));
    }

    public boolean isTea() {
        return getProductCategory().equals("??????");
    }

    public boolean isAvailable() {
        return !getSpecialMark().equals("??????");
    }

    public String getDisplayName() {
        String displayName = name;
        displayName += productPackage.getDescription().isEmpty() ? "" : ", " + productPackage.getDescription();
        displayName += specialMark.isEmpty() ? "" : ", '" + StringUtil.capitalize(specialMark)+ "'";
        displayName +=  ", " + price + "???";
        return displayName;
    }

    public String getFullDisplayName() {
        String displayName = getDisplayName();
        displayName += productCategory.isEmpty() ? "" : "\n???? ?????????????????? " + productCategory;
        displayName += productSubCategory.isEmpty() ? "" : "\n???????????????????????? " + productSubCategory;
        return displayName;
    }

    public String getShortName() {
        String displayName = "";
        displayName += productCategory.isEmpty() ? "" : productCategory + " ";
        displayName += productSubCategory.isEmpty() ? "" : productSubCategory + " ";
        displayName += name.isEmpty() ? "" : name;
        displayName += productForm.isEmpty() ? "" : ", " + productForm;
        return displayName;
    }

    public static class ProductBuilder {
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
}
