package com.techyourchance.dialoghelpersample.promptdialog;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import android.view.View;
import android.widget.TextView;

import com.techyourchance.dialoghelper.DialogHelper;
import com.techyourchance.dialoghelpersample.BaseDialog;
import com.techyourchance.dialoghelpersample.R;

import org.greenrobot.eventbus.EventBus;

/**
 * A dialog that can show title and message and has two buttons. Actions performed
 * in this dialog will be posted to event bus as {@link PromptDialogDismissedEvent}.
 */
public class PromptDialog extends BaseDialog {

    protected static final String ARG_TITLE = "ARG_TITLE";
    protected static final String ARG_MESSAGE = "ARG_MESSAGE";
    protected static final String ARG_POSITIVE_BUTTON_CAPTION = "ARG_POSITIVE_BUTTON_CAPTION";
    protected static final String ARG_NEGATIVE_BUTTON_CAPTION = "ARG_NEGATIVE_BUTTON_CAPTION";

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

    protected EventBus mEventBus;
    protected DialogHelper mDialogHelper;

    private TextView mTxtTitle;
    private TextView mTxtMessage;
    private AppCompatButton mBtnPositive;
    private AppCompatButton mBtnNegative;

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
        dialog.setContentView(R.layout.dialog_prompt);

        mTxtTitle = dialog.findViewById(R.id.txt_title);
        mTxtMessage = dialog.findViewById(R.id.txt_message);
        mBtnPositive = dialog.findViewById(R.id.btn_positive);
        mBtnNegative = dialog.findViewById(R.id.btn_negative);

        mTxtTitle.setText(getArguments().getString(ARG_TITLE));
        mTxtMessage.setText(getArguments().getString(ARG_MESSAGE));
        mBtnPositive.setText(getArguments().getString(ARG_POSITIVE_BUTTON_CAPTION));
        mBtnNegative.setText(getArguments().getString(ARG_NEGATIVE_BUTTON_CAPTION));

        mBtnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPositiveButtonClicked();
            }
        });

        mBtnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNegativeButtonClicked();
            }
        });

        return dialog;
    }

    protected void onPositiveButtonClicked() {
        dismiss();
        mEventBus.post(
                new PromptDialogDismissedEvent(
                        mDialogHelper.getDialogId(this),
                        PromptDialogDismissedEvent.ClickedButton.POSITIVE
                )
        );
    }

    protected void onNegativeButtonClicked() {
        dismiss();
        mEventBus.post(
                new PromptDialogDismissedEvent(
                        mDialogHelper.getDialogId(this),
                        PromptDialogDismissedEvent.ClickedButton.NEGATIVE
                )
        );
    }

}