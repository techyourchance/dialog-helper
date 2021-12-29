package com.techyourchance.dialoghelpersample;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.techyourchance.dialoghelper.DialogHelper;
import com.techyourchance.dialoghelpersample.bouncedialog.BouncePromptDialog;
import com.techyourchance.dialoghelpersample.infodialog.InfoDialog;
import com.techyourchance.dialoghelpersample.infodialog.InfoDialogDismissedEvent;
import com.techyourchance.dialoghelpersample.promptdialog.PromptDialog;
import com.techyourchance.dialoghelpersample.promptdialog.PromptDialogDismissedEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class SampleActivity extends AppCompatActivity {

    private static final String DIALOG_ID_INFO = "DIALOG_ID_INFO";
    private static final String DIALOG_ID_PROMPT = "DIALOG_ID_PROMPT";
    private static final String DIALOG_ID_FIRST_IN_CHAIN = "DIALOG_ID_FIRST_IN_CHAIN";
    private static final String DIALOG_ID_SECOND_IN_CHAIN = "DIALOG_ID_SECOND_IN_CHAIN";
    private static final String DIALOG_ID_BOUNCE = "DIALOG_ID_BOUNCE";

    private DialogHelper mDialogHelper;

    /**
     * Event bus isn't strictly required and the communication from dialogs to other
     * components can be implemented in multiple other ways. However, I find event buses
     * very handy in handling this use case.
     */
    private EventBus mEventBus;

    private Button mBtnShowInfoDialog;
    private Button mBtnShowPromptDialog;
    private Button mBtnShowBouncePromptDialog;
    private Button mBtnShowDialogsChain;
    private Button mBtnShowDialogAndHideImmediately;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDialogHelper = new DialogHelper(getSupportFragmentManager());
        mEventBus = EventBus.getDefault();

        setContentView(R.layout.activity_sample);

        mBtnShowInfoDialog = findViewById(R.id.btn_show_info_dialog);
        mBtnShowPromptDialog = findViewById(R.id.btn_show_prompt_dialog);
        mBtnShowDialogsChain = findViewById(R.id.btn_show_dialogs_chain);
        mBtnShowBouncePromptDialog = findViewById(R.id.btn_show_bounce_prompt_dialog);
        mBtnShowDialogAndHideImmediately = findViewById(R.id.btn_show_dialog_and_hide_immediately);

        registerButtonListeners();
    }

    private void registerButtonListeners() {
        mBtnShowInfoDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialogHelper.showDialog(
                        InfoDialog.newInfoDialog(
                                "Info dialog title",
                                "Info dialog message",
                                "Dismiss button"
                        ),
                        DIALOG_ID_INFO
                );
            }
        });

        mBtnShowPromptDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialogHelper.showDialog(
                        PromptDialog.newPromptDialog(
                                "Prompt dialog title",
                                "Prompt dialog message",
                                "Positive button",
                                "Negative button"
                        ),
                        DIALOG_ID_PROMPT
                );
            }
        });

        mBtnShowDialogsChain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PromptDialog promptDialog = PromptDialog.newPromptDialog(
                        "First dialog in chain",
                        "Would you like to continue to the second dialog in chain?",
                        "Continue",
                        "Cancel"
                );
                promptDialog.setEnterAnimation(DialogEnterAnimation.SLIDE_IN_FROM_RIGHT);
                promptDialog.setExitAnimation(DialogExitAnimation.SLIDE_OUT_FROM_LEFT);
                mDialogHelper.showDialog(promptDialog, DIALOG_ID_FIRST_IN_CHAIN);
            }
        });

        mBtnShowBouncePromptDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BouncePromptDialog bouncePromptDialog = BouncePromptDialog.newBouncePromptDialog();
                mDialogHelper.showDialog(bouncePromptDialog, DIALOG_ID_BOUNCE);
            }
        });

        mBtnShowDialogAndHideImmediately.setOnClickListener(view -> {
            mDialogHelper.showDialog(
                    InfoDialog.newInfoDialog(
                            "Error",
                            "If you see this dialog, then something doesn't work as expected. "
                                    + "It should've been dismissed immediately!",
                            "Crap!"
                    ),
                    null
            );
            mDialogHelper.dismissCurrentlyShownDialog();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mEventBus.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mEventBus.unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(InfoDialogDismissedEvent event) {
        switch (event.getDialogId()) {
            case DIALOG_ID_INFO:
                Toast.makeText(this, "Info dialog dismissed", Toast.LENGTH_LONG).show();
                break;
            case DIALOG_ID_SECOND_IN_CHAIN:
                Toast.makeText(this, "Last dialog in chain dismissed", Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PromptDialogDismissedEvent event) {
        switch (event.getDialogId()) {
            case DIALOG_ID_PROMPT:
                String clickedButton =
                        event.getClickedButton() == PromptDialogDismissedEvent.ClickedButton.POSITIVE ? "positive" : "negative";
                Toast.makeText(this, "Prompt dialog dismissed with " + clickedButton + " button click", Toast.LENGTH_LONG).show();
                break;
            case DIALOG_ID_FIRST_IN_CHAIN:
                if (event.getClickedButton() == PromptDialogDismissedEvent.ClickedButton.POSITIVE) {
                    InfoDialog infoDialog = InfoDialog.newInfoDialog(
                            "Second dialog in chain",
                            "",
                            "Dismiss button"
                    );
                    infoDialog.setEnterAnimation(DialogEnterAnimation.SLIDE_IN_FROM_RIGHT);
                    infoDialog.setExitAnimation(DialogExitAnimation.SLIDE_OUT_FROM_LEFT);
                    mDialogHelper.showDialog(infoDialog, DIALOG_ID_SECOND_IN_CHAIN);
                } else {
                    Toast.makeText(this, "Dialogs chain cancelled", Toast.LENGTH_LONG).show();
                }
                break;
            case DIALOG_ID_BOUNCE:
                Toast.makeText(this, "Don't forget to try another option as well", Toast.LENGTH_LONG).show();
                break;
        }
    }

}
