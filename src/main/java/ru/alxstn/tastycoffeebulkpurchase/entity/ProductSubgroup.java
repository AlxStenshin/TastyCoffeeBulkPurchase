package ru.alxstn.tastycoffeebulkpurchase.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "product_subgroup")
public class ProductSubgroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subgroup_id;

    @Column(name = "name")
    private String subgroupName;

    @OneToMany(mappedBy = "productSubGroup", cascade = CascadeType.ALL)
    private Set<Product> products = new HashSet<>();

    public void addProduct(Product product) {
        products.add(product);
    }

    public ProductSubgroup() { }

    public ProductSubgroup(String subgroupName) {
        this.subgroupName = subgroupName;
    }

    public Long getSubgroup_id() {
        return subgroup_id;
    }

    public void setSubgroup_id(Long id) {
        this.subgroup_id = id;
    }

    public String getSubgroupName() {
        return subgroupName;
    }

    public void setSubgroupName(String subgroupName) {
        this.subgroupName = subgroupName;
    }
}
