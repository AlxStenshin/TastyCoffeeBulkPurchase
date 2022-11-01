package ru.alxstn.tastycoffeebulkpurchase.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "product_package")
public class ProductPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "description")
    private String description;

    @Column(name = "weight")
    private double weight;

    public ProductPackage() { }

    public ProductPackage(String description) {
        this.description = description;
        this.weight = description.contains("100 г") ? 0.1 :
                description.contains("250 г") ? 0.25 :
                description.contains("1 кг") ? 1 :
                description.contains("2 кг") ? 2 : 0;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductPackage that = (ProductPackage) o;

        return Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return description != null ? description.hashCode() : 0;
    }
}
