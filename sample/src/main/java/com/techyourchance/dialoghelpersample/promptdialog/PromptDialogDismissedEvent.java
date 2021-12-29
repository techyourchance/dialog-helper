package com.techyourchance.dialoghelpersample.promptdialog;


import com.techyourchance.dialoghelpersample.BaseDialogEvent;

/**
 * This event will be posted to EventBus when prompt dialog is dismissed
 */
public class PromptDialogDismissedEvent extends BaseDialogEvent {

    private final ClickedButton mClickedButton;

    public enum ClickedButton {
        POSITIVE, NEGATIVE
    }

    public PromptDialogDismissedEvent(String id, ClickedButton clickedButton) {
        super(id);
        mClickedButton = clickedButton;
    }

    public ClickedButton getClickedButton() {
        return mClickedButton;
    }
}
