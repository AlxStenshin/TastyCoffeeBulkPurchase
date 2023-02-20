package ru.alxstn.tastycoffeebulkpurchase.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class DateTimeProvider {
    public String getFormattedCurrentDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");
        return LocalDateTime.now(ZoneId.of("Europe/Samara")).format(formatter);
    }

    public LocalDateTime getCurrentTimestamp() {
        return LocalDateTime.now();
    }
}
