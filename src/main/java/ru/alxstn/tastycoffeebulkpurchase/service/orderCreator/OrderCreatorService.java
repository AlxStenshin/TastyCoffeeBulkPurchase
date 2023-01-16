package ru.alxstn.tastycoffeebulkpurchase.service.orderCreator;

import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.entity.RequiredProductProperties;


public interface OrderCreatorService {
    void placeFullOrder(Session session);

    void placeOrderWithProductTypes(RequiredProductProperties productTypes);
}
