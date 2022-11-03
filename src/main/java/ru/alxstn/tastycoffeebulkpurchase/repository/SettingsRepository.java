package ru.alxstn.tastycoffeebulkpurchase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.alxstn.tastycoffeebulkpurchase.entity.Settings;

import java.util.List;

@Repository
public interface SettingsRepository extends JpaRepository<Settings, Long> {
    @Query("SELECT s FROM Settings s WHERE s.receiveDiscountNotification = true")
    List<Settings> findDiscountNotificationSubscribedUsers();
}
