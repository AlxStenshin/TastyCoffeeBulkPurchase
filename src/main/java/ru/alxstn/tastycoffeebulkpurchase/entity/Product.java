package ru.alxstn.tastycoffeebulkpurchase.entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "product")
public class Product {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @CreationTimestamp
    @Column(name = "date_created")
    private LocalDateTime dateCreated;

    @Column(name = "price")
    private Double price;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "mark_id")
    private ProductMark specialMark;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "package_id")
    private ProductPackaging productPackage;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "group_id")
    private ProductGroup productGroup;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "subgroup_id")
    private ProductSubgroup productSubGroup;

    public Product() {

    }

    public Product(ProductGroup group, ProductSubgroup subgroup, String name, ProductMark specialMark, ProductPackaging pack, Double price) {
        this.productGroup = group;
        this.productSubGroup = subgroup;
        this.name = name;
        this.specialMark = specialMark;
        this.productPackage = pack;
        this.price = price;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", productGroup=" + productGroup +
                ", productSubGroup=" + productSubGroup +
                ", productPackage=" + productPackage +
                ", price=" + price +
                ", specialMark='" + specialMark + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProductMark getSpecialMark() {
        return specialMark;
    }

    public void setSpecialMark(ProductMark specialMark) {
        this.specialMark = specialMark;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public ProductPackaging getPack() {
        return productPackage;
    }

    public void setPack(ProductPackaging pack) {
        this.productPackage = pack;
    }

    public ProductGroup getGroup() {
        return productGroup;
    }

    public void setGroup(ProductGroup group) {
        this.productGroup = group;
    }

    public ProductSubgroup getSubGroup() {
        return productSubGroup;
    }

    public void setSubGroup(ProductSubgroup subGroup) {
        this.productSubGroup = subGroup;
    }

    public static class Builder {
        private ProductGroup group;
        private ProductSubgroup subgroup;
        private ProductPackaging pack;
        private ProductMark specialMark;
        private String name;
        private Double price;

        public Builder() {}

        public Builder setGroup(String group) {
            this.group = new ProductGroup(group);
            return this;
        }

        public Builder setSubGroup(String subGroup) {
            this.subgroup = new ProductSubgroup(subGroup);
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setSpecialMark(String specialMark) {
            this.specialMark = new ProductMark(specialMark);
            return this;
        }

        public Builder setPackage(String pack) {
            this.pack = new ProductPackaging(pack);
            return this;
        }

        public Builder setPrice(Double price) {
            this.price = price;
            return this;
        }

        public Product build() {
            return new Product(group, subgroup, name, specialMark, pack, price);
        }
    }
}

