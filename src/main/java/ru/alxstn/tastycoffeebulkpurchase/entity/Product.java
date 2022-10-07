package ru.alxstn.tastycoffeebulkpurchase.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ru.alxstn.tastycoffeebulkpurchase.annotation.JsonExclude;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

// ToDo: Normalize table, separate productCategory, productSubCategory, productPackage and productMark entities.

@Entity
@Table(name = "product")
        // ToDo: find the way to avoid PSQLException: ERROR: duplicate key value violates unique constraint
        //  enable "on conflict do nothing" with jpa?
        //uniqueConstraints = {
        //@UniqueConstraint(columnNames = { "name", "price", "mark", "package", "category", "subcategory" }})
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
    private Double price;

    @Column(name = "mark")
    private String specialMark;

    @Column(name = "package")
    private String productPackage;

    @Column(name = "category")
    private String productCategory;

    @Column(name = "subcategory")
    private String productSubCategory;

    @Column(name = "actual")
    @JsonExclude
    private boolean actual;

    public Product() {

    }

    public Product(String name, Double price, String specialMark, String productPackage, String productGroup, String productSubGroup) {
        this.name = name;
        this.price = price;
        this.specialMark = specialMark;
        this.productPackage = productPackage;
        this.productCategory = productGroup;
        this.productSubCategory = productSubGroup;
        this.actual = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getSpecialMark() {
        return specialMark;
    }

    public void setSpecialMark(String specialMark) {
        this.specialMark = specialMark;
    }

    public String getProductPackage() {
        return productPackage;
    }

    public void setProductPackage(String productPackage) {
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

    public boolean isActual() {
        return actual;
    }

    public void setActual(boolean actual) {
        this.actual = actual;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        if (!Objects.equals(name, product.name)) return false;
        if (!Objects.equals(price, product.price)) return false;
        if (!Objects.equals(specialMark, product.specialMark)) return false;
        if (!Objects.equals(productPackage, product.productPackage))
            return false;
        if (!Objects.equals(productCategory, product.productCategory))
            return false;
        return Objects.equals(productSubCategory, product.productSubCategory);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (price != null ? price.hashCode() : 0);
        result = 31 * result + (specialMark != null ? specialMark.hashCode() : 0);
        result = 31 * result + (productPackage != null ? productPackage.hashCode() : 0);
        result = 31 * result + (productCategory != null ? productCategory.hashCode() : 0);
        result = 31 * result + (productSubCategory != null ? productSubCategory.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return name + " " + productPackage +" " + price;
    }

    public static class ProductBuilder {
        private String group;
        private String subgroup;
        private String pack;
        private String specialMark;
        private String name;
        private Double price;

        public ProductBuilder() {}

        public ProductBuilder setGroup(String group) {
            this.group = group;
            return this;
        }

        public ProductBuilder setSubGroup(String subGroup) {
            this.subgroup = subGroup;
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

        public ProductBuilder setPackage(String pack) {
            this.pack = pack;
            return this;
        }

        public ProductBuilder setPrice(Double price) {
            this.price = price;
            return this;
        }

        public Product build() {
            return new Product(name, price, specialMark, pack, group, subgroup);
        }
    }
}
