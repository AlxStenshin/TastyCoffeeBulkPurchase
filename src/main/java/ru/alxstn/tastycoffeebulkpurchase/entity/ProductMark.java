package ru.alxstn.tastycoffeebulkpurchase.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "product_mark")
public class ProductMark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long group_id;

    @Column(name = "mark")
    private String specialMark;

    @OneToMany(mappedBy = "productGroup", cascade = CascadeType.ALL)
    private Set<Product> products = new HashSet<>();

    public void addProduct(Product product) {
        products.add(product);
    }

    public ProductMark() { }

    public ProductMark(String groupName) {
        this.specialMark = groupName;
    }

    public Long getGroup_id() {
        return group_id;
    }

    public void setGroup_id(Long id) {
        this.group_id = id;
    }

    public String getSpecialMark() {
        return specialMark;
    }

    public void setSpecialMark(String groupName) {
        this.specialMark = groupName;
    }
}
