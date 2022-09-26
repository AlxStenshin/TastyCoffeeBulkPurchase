package ru.alxstn.tastycoffeebulkpurchase.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "product_group")
public class ProductGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long group_id;

    @Column(name = "name")
    private String groupName;

    @OneToMany(mappedBy = "productGroup", cascade = CascadeType.ALL)
    private Set<Product> products = new HashSet<>();

    public void addProduct(Product product) {
        products.add(product);
    }

    public ProductGroup() { }

    public ProductGroup(String groupName) {
        this.groupName = groupName;
    }

    public Long getGroup_id() {
        return group_id;
    }

    public void setGroup_id(Long id) {
        this.group_id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
