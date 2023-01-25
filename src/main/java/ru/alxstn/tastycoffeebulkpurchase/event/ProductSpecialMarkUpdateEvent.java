package ru.alxstn.tastycoffeebulkpurchase.event;

import ru.alxstn.tastycoffeebulkpurchase.entity.Product;

public class ProductSpecialMarkUpdateEvent extends ProductUpdateEvent {

    public ProductSpecialMarkUpdateEvent(Object source, Product oldProduct, Product newProduct) {
        super(source, oldProduct, newProduct);

        super.setUpdateMessage("Изменилась метка продукта из вашего заказа: \n<code>" +
                oldProduct.getFullDisplayNameWithPackage() + "\n</code>" +
                "Новая метка: " + newProduct.getSpecialMark() +
                "\n\nЧто это значит?" +
                "\nМетка 'нет' означает что продукт временно не доступен для заказа." +
                "\nМетка, содержащая дату так же означает что продукт не доступен для заказа." +
                "\nНа товары с меткой 'Сорт недели' не распостраняется скидка." +
                "\nМеткой 'Сорт месяца' маркируется чай по сниженной цене." +
                "\nОстальные отметки имеют недвусмысленное значение." +
                "\n\nСохранить с новой меткой или удалить продукт из заказа?");
    }
}
