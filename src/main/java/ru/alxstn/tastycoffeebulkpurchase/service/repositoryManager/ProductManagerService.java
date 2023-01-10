package ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager;

import org.springframework.data.domain.Example;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.entity.ProductPackage;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductManagerService {

    List<String> findAllActiveCategories();

    List<String> findAllActiveSubCategories(String category);

    List<Product> findDistinctActiveProductsBySubCategory(String subCategory);

    List<Product> findAllActiveProductsByProductNameAndSubcategory(String productName, String productSubCat);

    Optional<Product> productExists(String name, String cat, String subCat, ProductPackage pack, String mark, String form, BigDecimal price);

    Optional<Product> findProductWithForm(Product product, String form);

    List<Product> getSimilarProducts(String name, String cat, String subCat, String form, ProductPackage pack);

    void updateProduct(String name, String cat, String subCat, ProductPackage pack, String mark, String form, BigDecimal price, LocalDateTime updateDateTime);

    void markAllNotActual();

    void save(Product product);

    void save(ProductPackage pack);

    ProductPackage getProductPackageByDescription(String description);

    boolean productPackageExists(Example<ProductPackage> packageExample);

}
