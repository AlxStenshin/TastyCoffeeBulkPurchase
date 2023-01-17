package ru.alxstn.tastycoffeebulkpurchase.service.orderCreator;

import ru.alxstn.tastycoffeebulkpurchase.entity.DiscardedProductProperties;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;


public interface OrderCreatorService {
    void placeFullOrder(Session session);

    void placeOrderWithProductTypes(DiscardedProductProperties productTypes);
}
