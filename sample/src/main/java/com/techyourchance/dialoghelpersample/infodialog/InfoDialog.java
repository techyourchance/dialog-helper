package com.techyourchance.dialoghelpersample.infodialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

import com.techyourchance.dialoghelper.DialogHelper;

import org.greenrobot.eventbus.EventBus;

/**
 * A dialog that can show title and message and has a single button. Actions performed
 * in this dialog will be posted to event bus as {@link InfoDialogDismissedEvent}.
 */
public class InfoDialog extends DialogFragment {

    private static final String ARG_TITLE = "ARG_TITLE";
    private static final String ARG_MESSAGE = "ARG_MESSAGE";
    private static final String ARG_BUTTON_CAPTION = "ARG_BUTTON_CAPTION";

    public static InfoDialog newInfoDialog(String title, String message, String buttonCaption) {
        InfoDialog infoDialog = new InfoDialog();
        Bundle args = new Bundle(3);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_BUTTON_CAPTION, buttonCaption);
        infoDialog.setArguments(args);
        return infoDialog;
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

        setCancelable(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder
                .setTitle(getArguments().getString(ARG_TITLE))
                .setMessage(getArguments().getString(ARG_MESSAGE))
                .setPositiveButton(
                        getArguments().getString(ARG_BUTTON_CAPTION),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dismiss();
                            }
                        }
                );
        return builder.create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        mEventBus.post(new InfoDialogDismissedEvent(mDialogHelper.getDialogId(this)));
    }

}
