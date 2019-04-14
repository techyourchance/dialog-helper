package com.techyourchance.dialoghelpersample;

import com.techyourchance.dialoghelper.DialogHelper;
import com.techyourchance.dialoghelpersample.infodialog.InfoDialog;
import com.techyourchance.dialoghelpersample.promptdialog.PromptDialog;

/**
 * This class isn't strictly required, but its usage makes the code more unit testable.
 */
public class DialogsManager {

    private final DialogHelper mDialogHelper;

    public DialogsManager(DialogHelper dialogHelper) {
        mDialogHelper = dialogHelper;
    }

    public InfoDialog showInfoDialog(String title, String message, String buttonCaption, String dialogId) {
        InfoDialog infoDialog = InfoDialog.newInfoDialog(title, message, buttonCaption);
        mDialogHelper.showDialog(infoDialog, dialogId);
        return infoDialog;
    }

    public PromptDialog showPromptDialog(String title, String message, String positiveButtonCaption, String negativeButtonCaption, String dialogId) {
        PromptDialog promptDialog = PromptDialog.newPromptDialog(title, message, positiveButtonCaption, negativeButtonCaption);
        mDialogHelper.showDialog(promptDialog, dialogId);
        return promptDialog;
    }
}
