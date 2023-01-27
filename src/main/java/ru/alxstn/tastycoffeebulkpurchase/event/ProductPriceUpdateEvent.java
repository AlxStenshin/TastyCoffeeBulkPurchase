package ru.alxstn.tastycoffeebulkpurchase.event;

import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.model.ProductCaptionBuilder;

public class ProductPriceUpdateEvent extends ProductUpdateEvent {
    public ProductPriceUpdateEvent(Object source, Product oldProduct, Product newProduct) {
        super(source, oldProduct, newProduct);

        super.setUpdateMessage("Изменилась цена на продукт из вашего заказа:<code> \n" +
                new ProductCaptionBuilder(oldProduct)
                        .createIconNameMarkPackagePriceCatSubcatView() + "</code>\n" +
                "Новая цена: " + newProduct.getPrice() +
                "\nСохранить с новой ценой или удалить продукт из заказа?");
    }
}
