package ru.alxstn.tastycoffeebulkpurchase.service;

import org.springframework.stereotype.Component;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;

import java.util.List;

@Component
public class BasicProductNameShortener implements ProductNameShortener {

    @Override
    public String getShortName(Product product) {
        final String productName = product.getName();

        if (product.getName().length() <= 21)
            return productName;

        // Remove words occurrence using pre-defined Dictionary
        List<String> dict = List.of("Дрип-пакеты", "Шоколад фирменный", "Горячий шоколад", "Капсулы", "Напиток в банках");
        for (String s : dict) {
            if (productName.toLowerCase().contains(s.toLowerCase())) {
                return remove(productName, s);
            }
        }

        // Trying to remove any pointless clarification between '(' and ')'
        if (productName.contains("(") && productName.contains(")")) {
            String parentheses = productName.substring(productName.indexOf('('), productName.indexOf(')') + 1);
            return remove(productName, parentheses);
        }

        // Trying to remove category name from product name
        else if (productName.toLowerCase().contains(product.getProductCategory().toLowerCase()))
            return remove(productName, product.getProductCategory());

        // Trying to remove sub-category name from product name
        else if (productName.toLowerCase().contains(product.getProductSubCategory().toLowerCase()))
            return remove(productName, product.getProductSubCategory());

        return productName;
    }

    private String remove(String source, String target) {

        StringBuilder sbSource = new StringBuilder(source);
        StringBuilder sbSourceLower = new StringBuilder(source.toLowerCase());
        String searchString = target.toLowerCase();

        int idx = 0;
        while ((idx = sbSourceLower.indexOf(searchString, idx)) != -1) {
            sbSource.replace(idx, idx + searchString.length(), " ");
            sbSourceLower.replace(idx, idx + searchString.length(), " ");
            idx += " ".length();
        }
        sbSourceLower.setLength(0);
        sbSourceLower.trimToSize();

        return sbSource.toString().trim().replaceAll("\\s+", " ");
    }
}
