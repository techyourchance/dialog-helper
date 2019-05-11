package com.techyourchance.dialoghelpersample.promptdialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;

import com.techyourchance.dialoghelper.DialogHelper;
import com.techyourchance.dialoghelpersample.BaseDialog;

import org.greenrobot.eventbus.EventBus;

/**
 * A dialog that can show title and message and has two buttons. Actions performed
 * in this dialog will be posted to event bus as {@link PromptDialogDismissedEvent}.
 */
public class PromptDialog extends BaseDialog {

    private static final String ARG_TITLE = "ARG_TITLE";
    private static final String ARG_MESSAGE = "ARG_MESSAGE";
    private static final String ARG_POSITIVE_BUTTON_CAPTION = "ARG_POSITIVE_BUTTON_CAPTION";
    private static final String ARG_NEGATIVE_BUTTON_CAPTION = "ARG_NEGATIVE_BUTTON_CAPTION";

    public static PromptDialog newPromptDialog(String title, String message, String positiveButtonCaption, String negativeButtonCaption) {
        PromptDialog promptDialog = new PromptDialog();
        Bundle args = new Bundle(4);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_POSITIVE_BUTTON_CAPTION, positiveButtonCaption);
        args.putString(ARG_NEGATIVE_BUTTON_CAPTION, negativeButtonCaption);
        promptDialog.setArguments(args);
        return promptDialog;
    }

    private EventBus mEventBus;
    private DialogHelper mDialogHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEventBus = EventBus.getDefault();
        mDialogHelper = new DialogHelper(requireActivity().getSupportFragmentManager());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() == null) {
            throw new IllegalStateException("arguments mustn't be null");
        }

        // see BaseDialog#setAlertDialogListenersIfNeeded() method's source comments to understand why this is needed
        return newAlertDialogWithExitAnimationSupport(
                getArguments().getString(ARG_TITLE),
                getArguments().getString(ARG_MESSAGE),
                getArguments().getString(ARG_POSITIVE_BUTTON_CAPTION),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        positiveButtonClicked();
                    }
                },
                getArguments().getString(ARG_NEGATIVE_BUTTON_CAPTION),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        negativeButtonClicked();
                    }
                }
        );
    }

    private void positiveButtonClicked() {
        dismiss();
        mEventBus.post(
                new PromptDialogDismissedEvent(
                        mDialogHelper.getDialogId(this),
                        PromptDialogDismissedEvent.ClickedButton.POSITIVE
                )
        );
    }
    
    private void negativeButtonClicked() {
        dismiss();
        mEventBus.post(
                new PromptDialogDismissedEvent(
                        mDialogHelper.getDialogId(this), 
                        PromptDialogDismissedEvent.ClickedButton.NEGATIVE
                )
        );
    }

}
