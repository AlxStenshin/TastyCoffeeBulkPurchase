package ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager;

import org.springframework.stereotype.Service;
import ru.alxstn.tastycoffeebulkpurchase.entity.Customer;
import ru.alxstn.tastycoffeebulkpurchase.repository.CustomerRepository;

import java.util.List;

@Service
public class BasicCustomerManagerService implements CustomerManagerService {

    private final CustomerRepository customerRepository;

    public BasicCustomerManagerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer getByChatId(long chatId) {
        return customerRepository.getByChatId(chatId);
    }

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

}
