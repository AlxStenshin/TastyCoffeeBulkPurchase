package ru.alxstn.tastycoffeebulkpurchase.service.orderCreator;

import ru.alxstn.tastycoffeebulkpurchase.model.SessionProductFilters;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;


public interface OrderCreatorService {
    void placeFullOrder(Session session);

    void placeOrderWithProductTypes(SessionProductFilters productTypes);
}
