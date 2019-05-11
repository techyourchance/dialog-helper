package com.techyourchance.dialoghelpersample.bouncedialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.techyourchance.dialoghelpersample.DialogEnterAnimation;
import com.techyourchance.dialoghelpersample.DialogExitAnimation;
import com.techyourchance.dialoghelpersample.promptdialog.PromptDialog;
import com.techyourchance.dialoghelpersample.promptdialog.PromptDialogDismissedEvent;

public class BouncePromptDialog extends PromptDialog {

    public static BouncePromptDialog newBouncePromptDialog() {
        BouncePromptDialog bouncePromptDialog = new BouncePromptDialog();
        Bundle args = new Bundle(4);
        args.putString(ARG_TITLE, "Bounce dialog");
        args.putString(ARG_MESSAGE, "Which direction would you like the dialog to exit?");
        args.putString(ARG_POSITIVE_BUTTON_CAPTION, "Left");
        args.putString(ARG_NEGATIVE_BUTTON_CAPTION, "Bottom");
        bouncePromptDialog.setArguments(args);
        bouncePromptDialog.setEnterAnimation(DialogEnterAnimation.SLIDE_IN_AND_BOUNCE_FROM_TOP);
        return bouncePromptDialog;
    }



    @Override
    protected void onPositiveButtonClicked() {
        setExitAnimation(DialogExitAnimation.SLIDE_OUT_FROM_LEFT);
        dismiss();
        mEventBus.post(
                new PromptDialogDismissedEvent(
                        mDialogHelper.getDialogId(this),
                        PromptDialogDismissedEvent.ClickedButton.POSITIVE
                )
        );
    }

    @Override
    protected void onNegativeButtonClicked() {
        setExitAnimation(DialogExitAnimation.SLIDE_OUT_FROM_BOTTOM);
        dismiss();
        mEventBus.post(
                new PromptDialogDismissedEvent(
                        mDialogHelper.getDialogId(this), 
                        PromptDialogDismissedEvent.ClickedButton.NEGATIVE
                )
        );
    }

}
