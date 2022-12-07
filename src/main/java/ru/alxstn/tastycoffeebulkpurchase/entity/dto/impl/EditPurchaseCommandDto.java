package ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl;

import ru.alxstn.tastycoffeebulkpurchase.entity.Purchase;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineObject;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;

public class EditPurchaseCommandDto extends SerializableInlineObject {

    private Purchase purchase;
    private boolean editMode = false;

    public EditPurchaseCommandDto() {
        super(SerializableInlineType.EDIT_PURCHASE);
    }

    public EditPurchaseCommandDto(Purchase purchase, boolean editMode) {
        this();
        this.purchase = purchase;
        this.editMode = editMode;
    }
    public EditPurchaseCommandDto(Purchase purchase, boolean editMode, SerializableInlineObject previous) {
        this();
        this.purchase = purchase;
        this.editMode = editMode;
        super.setPrevious(previous);
    }

    public boolean isEditMode() {
        return editMode;
    }

    public Purchase getPurchase() {
        return purchase;
    }
}
