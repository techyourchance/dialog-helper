package com.techyourchance.dialoghelper;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * Instances of this class simplify dialogs management in the application.<br>
 * You can have a single instance of this class in your application, or you can have multiple instances
 * (e.g. one instance per Activity or Fragment). This class will work seamlessly in all setups and the state will
 * be preserved across both configuration change and save & restore (aka. process death),
 * as long as you show ALL your dialogs using DialogHelper.
 */
@UiThread
public class DialogHelper {

    /**
     * Whenever DialogHelper shows dialog with non-empty "id", the provided id will be stored in
     * arguments Bundle of the dialog under this key.
     */
    private static final String ARGUMENT_DIALOG_ID = "com.techyourchance.dialoghelper.ARGUMENT_DIALOG_ID";

    /**
     * In case there will be multiple instances of DialogHelper, or Activity or Fragment that instantiated this
     * DialogsManager are re-created (e.g. in case of memory reclaim by OS, orientation change, etc.),
     * we need to be able to get a reference to the last shown dialog.<br>
     * This tag will be supplied with all DialogFragment's shown by this DialogsManager and can be used
     * to query {@link FragmentManager} for last shown dialog.
     */
    private static final String DIALOG_FRAGMENT_TAG = "com.techyourchance.dialoghelper.DIALOG_TAG";

    private final FragmentManager mFragmentManager;

    public DialogHelper(FragmentManager fragmentManager) {
        mFragmentManager = fragmentManager;
    }

    /**
     * Get a reference to the currently shown dialog. This method assumes that the dialog was shown using
     * DialogHelper.
     * @return a reference to the currently shown dialog, or null if no dialog is shown.
     */
    public @Nullable DialogFragment getCurrentlyShownDialog() {
        Fragment fragmentWithDialogTag = mFragmentManager.findFragmentByTag(DIALOG_FRAGMENT_TAG);
        if (fragmentWithDialogTag != null
                && DialogFragment.class.isAssignableFrom(fragmentWithDialogTag.getClass())) {
            return (DialogFragment) fragmentWithDialogTag;
        } else {
            return null;
        }
    }

    /**
     * Obtain the id of the currently shown dialog. This method assumes that the dialog was shown using
     * DialogHelper.
     * @return the id of the currently shown dialog; null if no dialog is shown, or the currently
     *         shown dialog has no id
     */
    public @Nullable String getCurrentlyShownDialogId() {
        DialogFragment currentlyShownDialog = getCurrentlyShownDialog();
        if (currentlyShownDialog == null) {
            return null;
        } else {
            return getDialogId(currentlyShownDialog);
        }
    }

    /**
     * Obtain the ID of the given dialog. This method assumes that the dialog was shown using
     * DialogHelper.
     * @return the ID of the given dialog; null if the dialog has no ID
     */
    public @Nullable String getDialogId(@NonNull DialogFragment dialog) {
        if (dialog.getArguments() == null ||
                !dialog.getArguments().containsKey(ARGUMENT_DIALOG_ID)) {
            return null;
        } else {
            return dialog.getArguments().getString(ARGUMENT_DIALOG_ID);
        }
    }

    /**
     * Dismiss the currently shown dialog. Has no effect if no dialog is shown.
     */
    public void dismissCurrentlyShownDialog() {
        DialogFragment currentlyShownDialog = getCurrentlyShownDialog();
        if (currentlyShownDialog != null) {
            currentlyShownDialog.dismissAllowingStateLoss();
        }
    }

    /**
     * Show dialog and assign it a given "id". Replaces any other currently shown dialog.
     * @param dialog dialog to show
     * @param id string that uniquely identifies the dialog; can be null
     */
    public void showDialog(DialogFragment dialog, @Nullable String id) {
        dismissCurrentlyShownDialog();
        setId(dialog, id);
        showDialog(dialog);
    }

    private void setId(DialogFragment dialog, String id) {
        Bundle args = dialog.getArguments() != null ? dialog.getArguments() : new Bundle(1);
        args.putString(ARGUMENT_DIALOG_ID, id);
        dialog.setArguments(args);
    }

    private void showDialog(DialogFragment dialog) {
        mFragmentManager.beginTransaction()
            .add(dialog, DIALOG_FRAGMENT_TAG)
            .commitAllowingStateLoss();
    }

}
