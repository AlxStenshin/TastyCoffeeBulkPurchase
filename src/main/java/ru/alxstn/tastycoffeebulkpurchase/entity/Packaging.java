package ru.alxstn.tastycoffeebulkpurchase.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "package")
public class Packaging {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long package_id;

    @Column(name = "name")
    private String packageName;

    @OneToMany(mappedBy = "productPackage", cascade = CascadeType.ALL)
    private Set<Product> products = new HashSet<>();

    public void addProduct(Product product) {
        products.add(product);
    }

    public Packaging() { }

    public Packaging(String packageName) {
        this.packageName = packageName;
    }

    public Long getPackage_id() {
        return package_id;
    }

    public void setPackage_id(Long id) {
        this.package_id = id;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
