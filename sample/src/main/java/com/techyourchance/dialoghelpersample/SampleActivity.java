package com.techyourchance.dialoghelpersample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.techyourchance.dialoghelper.DialogHelper;
import com.techyourchance.dialoghelpersample.infodialog.InfoDialogDismissedEvent;
import com.techyourchance.dialoghelpersample.promptdialog.PromptDialogDismissedEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class SampleActivity extends AppCompatActivity {

    private static final String DIALOG_ID_INFO = "DIALOG_ID_INFO";
    private static final String DIALOG_ID_PROMPT = "DIALOG_ID_PROMPT";

    private DialogsManager mDialogsManager;
    private EventBus mEventBus;

    private AppCompatButton mBtnShowInfoDialog;
    private AppCompatButton mBtnShowPromptDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDialogsManager = new DialogsManager(new DialogHelper(getSupportFragmentManager()));
        mEventBus = EventBus.getDefault();

        setContentView(R.layout.activity_sample);

        mBtnShowInfoDialog = findViewById(R.id.btn_show_info_dialog);
        mBtnShowPromptDialog = findViewById(R.id.btn_show_prompt_dialog);

        registerButtonListeners();
    }

    private void registerButtonListeners() {
        mBtnShowInfoDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialogsManager.showInfoDialog(
                        "Info dialog title",
                        "Info dialog message",
                        "Info dialog button",
                        DIALOG_ID_INFO
                );
            }
        });

        mBtnShowPromptDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialogsManager.showPromptDialog(
                        "Prompt dialog title",
                        "Prompt dialog message",
                        "Positive button",
                        "Negative button",
                        DIALOG_ID_PROMPT
                );
            }
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
        if (DIALOG_ID_INFO.equals(event.getDialogId())) {
            Toast.makeText(this, "Info dialog dismissed", Toast.LENGTH_LONG).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PromptDialogDismissedEvent event) {
        if (DIALOG_ID_PROMPT.equals(event.getDialogId())) {
            String clickedButton =
                    event.getClickedButton() == PromptDialogDismissedEvent.ClickedButton.POSITIVE ? "positive" : "negative";
            Toast.makeText(this, "Prompt dialog dismissed with " + clickedButton + " button click", Toast.LENGTH_LONG).show();
        }
    }

}
