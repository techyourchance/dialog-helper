package com.techyourchance.dialoghelpersample.infodialog;


import com.techyourchance.dialoghelpersample.BaseDialogEvent;

/**
 * This event will be posted to EventBus when info dialog is dismissed
 */
public class InfoDialogDismissedEvent extends BaseDialogEvent {

    public InfoDialogDismissedEvent(String id) {
        super(id);
    }
}
