package ru.alxstn.tastycoffeebulkpurchase.service.pricelistsSaver;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.alxstn.tastycoffeebulkpurchase.event.PriceListReceivedEvent;
import ru.alxstn.tastycoffeebulkpurchase.repository.PriceListRepository;

@Component
public class PriceListRepoSaver {
    private final PriceListRepository repository;

    public PriceListRepoSaver(PriceListRepository repository) {
        this.repository = repository;
    }

    @EventListener
    public void handlePriceList(final PriceListReceivedEvent event) {
        repository.saveAll(event.getPriceList());
    }
}
