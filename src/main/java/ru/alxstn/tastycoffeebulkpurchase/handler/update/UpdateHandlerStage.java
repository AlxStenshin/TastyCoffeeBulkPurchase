package ru.alxstn.tastycoffeebulkpurchase.handler.update;

public enum UpdateHandlerStage {
    CALLBACK,
    INLINE,
    PAYMENT,
    COMMAND,
    REPLY_BUTTON;

    public int getOrder() {
        return ordinal();
    }

}
