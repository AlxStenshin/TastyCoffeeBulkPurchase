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

public class MenuNavigationBotMessage <T>  {

    private final Update update;
    private List<T> dataSource;
    private String title;
    private Function<T, InlineKeyboardButton> buttonCreator;
    private String backButtonCallback;
    private String selectProductCategoryButtonCallback;

    private List<List<InlineKeyboardButton>> buttons;
    private List<InlineKeyboardButton> additionalButtons = new ArrayList<>();

    public void setTitle(String title) {
        this.title = title;
    }

    public MenuNavigationBotMessage(Update update) {
        this.update = update;
        dataSource = new ArrayList<>();
        buttons = new ArrayList<>();
    }

    public void setDataSource(List<T> dataSource) {
        this.dataSource = dataSource;
    }

    public void setButtonCreator(Function<T, InlineKeyboardButton> buttonCreator) {
        this.buttonCreator = buttonCreator;
    }

    public void setBackButtonCallback(String backButtonCallback) {
        this.backButtonCallback = backButtonCallback;
    }

    public void setSelectProductCategoryButtonCallback(String selectProductCategoryButtonCallback) {
        this.selectProductCategoryButtonCallback = selectProductCategoryButtonCallback;
    }

    public void setButtons(List<List<InlineKeyboardButton>> buttons) {
        this.buttons = buttons;
    }

    public void addAdditionalButtons(List<InlineKeyboardButton> buttons) {
        this.additionalButtons = buttons;
    }

    public SendMessage newMessage() {
        prepareMessage();

        return SendMessage.builder()
                .text(title)
                .chatId(update.getMessage().getChatId().toString())
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
            for (var o : dataSource) {
                buttons.add(Collections.singletonList(buttonCreator.apply(o)));
            }
        }

        buttons.add(additionalButtons);

        List<InlineKeyboardButton> serviceButtonsRow = new ArrayList<>();
        if (backButtonCallback != null) {
            serviceButtonsRow.add(InlineKeyboardButton.builder()
                            .text("< Назад")
                            .callbackData(backButtonCallback)
                            .build());
        }

        if (selectProductCategoryButtonCallback != null) {
            serviceButtonsRow.add(InlineKeyboardButton.builder()
                            .text("<< Категории")
                            .callbackData(selectProductCategoryButtonCallback)
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
