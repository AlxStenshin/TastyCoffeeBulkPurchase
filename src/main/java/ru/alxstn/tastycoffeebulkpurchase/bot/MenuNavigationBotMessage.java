package ru.alxstn.tastycoffeebulkpurchase.bot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class MenuNavigationBotMessage {

    private final Update update;
    private List<String> dataSource;
    private String title;
    private Function<String, InlineKeyboardButton> buttonCreator;
    private String backButtonCallback;
    private String mainMenuButtonCallback;
    private String selectProductCategoryButtonCallback;

    private List<List<InlineKeyboardButton>> buttons;

    public MenuNavigationBotMessage(Update update) {
        this.update = update;
        buttons = new ArrayList<>();
    }

    public void setDataSource(List<String> dataSource) {
        this.dataSource = dataSource;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setButtonCreator(Function<String, InlineKeyboardButton> buttonCreator) {
        this.buttonCreator = buttonCreator;
    }

    public void setBackButtonCallback(String backButtonCallback) {
        this.backButtonCallback = backButtonCallback;
    }

    public void setMainMenuButtonCallback(String mainMenuButtonCallback) {
        this.mainMenuButtonCallback = mainMenuButtonCallback;
    }

    public void setSelectProductCategoryButtonCallback(String selectProductCategoryButtonCallback) {
        this.selectProductCategoryButtonCallback = selectProductCategoryButtonCallback;
    }

    public void setButtons(List<List<InlineKeyboardButton>> buttons) {
        this.buttons = buttons;
    }

    public SendMessage newMessage() {
        prepareMessage();
        return SendMessage.builder()
                .text(title)
                .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                .build();
    }

    public EditMessageText updatePrevious() {
        prepareMessage();
        return EditMessageText.builder()
                .text(title)
                .messageId(update.getCallbackQuery().getMessage().getMessageId())
                .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                .build();
    }

    private void prepareMessage() {

        if (buttons.isEmpty()) {
            for (var s : dataSource) {
                buttons.add(Collections.singletonList(buttonCreator.apply(s)));
            }
        }

        List<InlineKeyboardButton> serviceButtonsRow = new ArrayList<>();
        if (backButtonCallback != null) {
            serviceButtonsRow.add(InlineKeyboardButton.builder()
                            .text("< Назад")
                            .callbackData(backButtonCallback)
                            .build());
        }

        if (selectProductCategoryButtonCallback != null) {
            serviceButtonsRow.add(InlineKeyboardButton.builder()
                            .text("Выбрать категорию")
                            .callbackData(selectProductCategoryButtonCallback)
                            .build());
        }

        if (mainMenuButtonCallback != null) {
            serviceButtonsRow.add(InlineKeyboardButton.builder()
                            .text("Главное меню")
                            .callbackData(mainMenuButtonCallback)
                            .build());
        }

        buttons.add(serviceButtonsRow);

        int maxLen = buttons.stream()
                .flatMap(List::stream)
                .map(b -> b.getText().length())
                .reduce(Integer::max)
                .orElse(title.length());
        title = title + " ".repeat(Math.max(0, maxLen - title.length()));
    }
}
