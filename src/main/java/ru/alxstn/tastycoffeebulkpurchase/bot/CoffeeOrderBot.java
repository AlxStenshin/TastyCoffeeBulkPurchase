package ru.alxstn.tastycoffeebulkpurchase.bot;

import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.alxstn.tastycoffeebulkpurchase.configuration.TelegramBotConfigProperties;
import ru.alxstn.tastycoffeebulkpurchase.event.bot.AlertMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.event.bot.RemoveMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.event.bot.SendMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.event.bot.UpdateMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.handler.UpdateHandler;


import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RabbitListener(queues = "botUpdateQueue")
@Component
public class CoffeeOrderBot extends TelegramLongPollingBot {
    Logger logger = LogManager.getLogger(CoffeeOrderBot.class);

    private final TelegramBotConfigProperties botConfigProperties;
    private final RabbitTemplate mqTemplate;
    private final Queue updateQueue;
    private List<UpdateHandler> updateHandlers;

    public CoffeeOrderBot(TelegramBotConfigProperties botConfigProperties,
                          RabbitTemplate mqTemplate, Queue updateQueue, List<UpdateHandler> updateHandlers) {

        this.botConfigProperties = botConfigProperties;
        this.mqTemplate = mqTemplate;
        this.updateQueue = updateQueue;
        this.updateHandlers = updateHandlers;
    }

    @PostConstruct
    public void init() {
        updateHandlers = updateHandlers.stream()
                        .sorted(Comparator.comparingInt(u -> u.getStage().getOrder()))
                        .collect(Collectors.toList());
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        mqTemplate.convertAndSend(updateQueue.getName(), update);
    }

    @RabbitHandler
    public void processBotUpdate(Update update) {
        for (UpdateHandler updateHandler : updateHandlers) {
            try {
                if (updateHandler.handleUpdate(update)) {
                    return;
                }
            } catch (Exception e) {
                logger.error("Error handling update: " + update + "\nMessage: " + e.getMessage(), e);
            }
        }
    }

    // ToDo: Refactor with BotApiMethod
    @EventListener
    public void sendMessage(SendMessageEvent event) {
        try {
            execute(event.getMessage());
        } catch (TelegramApiException e) {
            logger.error(e.getMessage());
        }
    }

    @EventListener
    public void updateMessage(UpdateMessageEvent event) {
        try {
            execute(event.getMessage());
        } catch (TelegramApiException e) {
            logger.error(e.getMessage());
        }
    }

    @EventListener
    public void updateMessage(AlertMessageEvent event) {
        try {
            execute(event.getMessage());
        } catch (TelegramApiException e) {
            logger.error(e.getMessage());
        }
    }

    @EventListener
    public void deleteMessage(RemoveMessageEvent event) {
        try {
            execute(event.getDeleteMessage());
        } catch (TelegramApiException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return botConfigProperties.getName();
    }

    @Override
    public String getBotToken() {
        return botConfigProperties.getToken();
    }

}