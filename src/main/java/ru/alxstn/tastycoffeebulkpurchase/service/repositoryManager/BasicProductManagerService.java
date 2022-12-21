package ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager;

import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.entity.ProductPackage;
import ru.alxstn.tastycoffeebulkpurchase.repository.ProductPackageRepository;
import ru.alxstn.tastycoffeebulkpurchase.repository.ProductRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BasicProductManagerService implements ProductManagerService {

    private final ProductRepository productRepository;
    private final ProductPackageRepository productPackageRepository;


    public BasicProductManagerService(ProductRepository productRepository,
                                      ProductPackageRepository productPackageRepository) {
        this.productRepository = productRepository;
        this.productPackageRepository = productPackageRepository;
    }

    @Override
    public List<String> findAllActiveCategories() {
        return productRepository.findAllActiveCategories();
    }

    @Override
    public List<String> findAllActiveSubCategories(String category) {
        return productRepository.findAllActiveSubCategories(category);
    }

    @Override
    public List<Product> findDistinctActiveProductsBySubCategory(String subCategory) {
        return productRepository.findDistinctActiveProductsBySubCategory(subCategory);
    }

    @Override
    public List<Product> findAllActiveProductsByProductNameAndSubcategory(String productName, String productSubCat) {
        return productRepository.findAllActiveProductsByProductNameAndSubcategory(productName, productSubCat);
    }

    @Override
    public Optional<Product> productExists(String name, String cat, String subCat, ProductPackage pack, String mark, BigDecimal price) {
        return productRepository.productExists(name, cat, subCat, pack, mark, price);
    }

    @Override
    public List<Product> getProductsByNameCategorySubcategoryAndPackage(String name, String cat, String subCat, ProductPackage pack) {
        return productRepository.getProductsByNameCategorySubcategoryAndPackage(name, cat, subCat, pack);
    }

    @Override
    public void updateProduct(String name, String cat, String subCat, ProductPackage pack, String mark, BigDecimal price, LocalDateTime updateDateTime) {
        productRepository.update(name, cat, subCat, pack, mark, price, updateDateTime);
    }

    @Override
    public void markAllNotActual() {
        productRepository.markAllNotActual();
    }

    @Override
    public void save(Product product) {
        productRepository.save(product);
    }

    @Override
    public void save(ProductPackage pack) {
        productPackageRepository.save(pack);
    }

    @Override
    public ProductPackage getProductPackageByDescription(String description) {
        return productPackageRepository.getProductPackageByDescription(description);
    }

    @Override
    public boolean productPackageExists(Example<ProductPackage> packageExample) {
        return productPackageRepository.exists(packageExample);
    }
}
