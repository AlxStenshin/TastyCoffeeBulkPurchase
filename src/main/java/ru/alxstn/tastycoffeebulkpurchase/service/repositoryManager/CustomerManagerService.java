package ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager;

import ru.alxstn.tastycoffeebulkpurchase.entity.Customer;

import java.util.List;

public interface CustomerManagerService {

    Customer getByChatId(long chatId);

    List<Customer> findAll();
}
