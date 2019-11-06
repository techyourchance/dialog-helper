package com.techyourchance.dialoghelpersample.infodialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.TextView;

import com.techyourchance.dialoghelper.DialogHelper;
import com.techyourchance.dialoghelpersample.BaseDialog;
import com.techyourchance.dialoghelpersample.R;

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

    private TextView mTxtTitle;
    private TextView mTxtMessage;
    private AppCompatButton mBtnPositive;

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

        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_info);

        mTxtTitle = dialog.findViewById(R.id.txt_title);
        mTxtMessage = dialog.findViewById(R.id.txt_message);
        mBtnPositive = dialog.findViewById(R.id.btn_positive);

        mTxtTitle.setText(getArguments().getString(ARG_TITLE));
        mTxtMessage.setText(getArguments().getString(ARG_MESSAGE));
        mBtnPositive.setText(getArguments().getString(ARG_BUTTON_CAPTION));

        mBtnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClicked();
            }
        });

        return dialog;
    }

    protected void onButtonClicked() {
        dismiss();
        mEventBus.post(new InfoDialogDismissedEvent(mDialogHelper.getDialogId(this)));
    }

}

