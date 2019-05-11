package com.techyourchance.dialoghelpersample.infodialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.techyourchance.dialoghelper.DialogHelper;
import com.techyourchance.dialoghelpersample.BaseDialog;

import org.greenrobot.eventbus.EventBus;

/**
 * A dialog that can show title and message and has a single button. Actions performed
 * in this dialog will be posted to event bus as {@link InfoDialogDismissedEvent}.
 */
public class InfoDialog extends BaseDialog {

    protected static final String ARG_TITLE = "ARG_TITLE";
    protected static final String ARG_MESSAGE = "ARG_MESSAGE";
    protected static final String ARG_BUTTON_CAPTION = "ARG_BUTTON_CAPTION";

    public static InfoDialog newInfoDialog(String title, String message, String buttonCaption) {
        InfoDialog infoDialog = new InfoDialog();
        Bundle args = new Bundle(3);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_BUTTON_CAPTION, buttonCaption);
        infoDialog.setArguments(args);
        return infoDialog;
    }

    protected EventBus mEventBus;
    protected DialogHelper mDialogHelper;

    @Override
    public final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEventBus = EventBus.getDefault();
        mDialogHelper = new DialogHelper(requireActivity().getSupportFragmentManager());
    }

    @NonNull
    @Override
    public final Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() == null) {
            throw new IllegalStateException("arguments mustn't be null");
        }

        // see BaseDialog#setAlertDialogListenersIfNeeded() method's source comments to understand why this is needed
        return newAlertDialogWithExitAnimationSupport(
                getArguments().getString(ARG_TITLE),
                getArguments().getString(ARG_MESSAGE),
                getArguments().getString(ARG_BUTTON_CAPTION),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onButtonClicked();
                    }
                },
                null,
                null
        );
    }

    protected void onButtonClicked() {
        dismiss();
        mEventBus.post(new InfoDialogDismissedEvent(mDialogHelper.getDialogId(this)));
    }

}
