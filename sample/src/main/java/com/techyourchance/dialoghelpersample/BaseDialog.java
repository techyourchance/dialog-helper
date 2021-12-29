package com.techyourchance.dialoghelpersample;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;

public abstract class BaseDialog extends DialogFragment {

    private static final int DEFAULT_ENTER_ANIMATION_DURATION_MS = 300;
    private static final int DEFAULT_EXIT_ANIMATION_DURATION_MS = 300;

    private static final String SAVED_STATE_FIRST_ON_START = "SAVED_STATE_FIRST_ON_START";
    private static final String SAVED_STATE_ENTER_ANIMATION = "SAVED_STATE_ENTER_ANIMATION";
    private static final String SAVED_STATE_EXIT_ANIMATION = "SAVED_STATE_EXIT_ANIMATION";

    private DialogAnimatorsFactory mDialogAnimatorsFactory;

    protected boolean mFirstOnStart = true;

    private @Nullable DialogEnterAnimation mEnterAnimation;
    private int mEnterAnimationDurationMs = DEFAULT_ENTER_ANIMATION_DURATION_MS;

    private @Nullable DialogExitAnimation mExitAnimation;
    private int mExitAnimationDurationMs = DEFAULT_EXIT_ANIMATION_DURATION_MS;

    protected boolean mPlayingExitAnimation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDialogAnimatorsFactory = new DialogAnimatorsFactory();
        if (savedInstanceState != null) {
            mFirstOnStart = savedInstanceState.getBoolean(SAVED_STATE_FIRST_ON_START);
            mEnterAnimation = (DialogEnterAnimation) savedInstanceState.getSerializable(SAVED_STATE_ENTER_ANIMATION);
            mExitAnimation = (DialogExitAnimation) savedInstanceState.getSerializable(SAVED_STATE_EXIT_ANIMATION);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_STATE_FIRST_ON_START, mFirstOnStart);
        outState.putSerializable(SAVED_STATE_ENTER_ANIMATION, mEnterAnimation);
        outState.putSerializable(SAVED_STATE_EXIT_ANIMATION, mExitAnimation);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mFirstOnStart) {

            executeHackToPreventSystemNavigationUiToBlink();

            // currently cancellable dialogs not supported; see setCancellable method
            super.setCancelable(false);

            playEnterAnimationIfNeeded();

            mFirstOnStart = false;
        }
    }

    private void executeHackToPreventSystemNavigationUiToBlink() {
        // this method is a workaround for an annoying problem that appears if you use full-screen
        // mode and hide system navigation controls
        // the solution was taken from here: https://stackoverflow.com/a/24549869/2463035

        getDialogWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        getDialogDecorView().setSystemUiVisibility(requireActivity().getWindow().getDecorView().getSystemUiVisibility());

        getDialog().setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                WindowManager windowManager = (WindowManager) requireContext().getSystemService(Context.WINDOW_SERVICE);
                if (windowManager == null) {
                    // window manager shouldn't be null at this point, but just in case let's fail fast to surface the problem
                    throw new RuntimeException("dialog window is null");
                }

                // clear the not focusable flag from the window
                getDialogWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

                // update the WindowManager with the new attributes (no nicer way I know of how to do this)..
                windowManager.updateViewLayout(getDialogWindow().getDecorView(), getDialogWindow().getAttributes());
            }
        });
    }

    @Override
    public void setCancelable(boolean cancelable) {
        /*
        If dialogs will ever need to be cancellable, remove this override and a line in onViewCreated.
        However, keep in mind that the current exit animations scheme won't work because the entire
        dialog's window will be dismissed when clicked outside of its area. In that case,
        animations can still be done through Window#setWindowAnimations, but that scheme is limited
        to animations in XML only, which is less flexible.
        Alternatively, you can manually implement cancel actions and keep programmatic animations support.
        */
        if (cancelable) {
            throw new RuntimeException("cancellable dialogs not currently supported; please consult the source code comments");
        }
    }

    private void playEnterAnimationIfNeeded() {
        if (mEnterAnimation == null) {
            return;
        }
        ObjectAnimator animator = mDialogAnimatorsFactory.newEnterAnimator(mEnterAnimation, mEnterAnimationDurationMs);
        animator.start();
    }

    @Override
    public void dismiss() {
        if (mPlayingExitAnimation) {
            return;
        }
        playExitAnimationIfNeededWithEndRunnable(new Runnable() {
            @Override
            public void run() {
                BaseDialog.super.dismiss();
            }
        });
    }

    @Override
    public void dismissAllowingStateLoss() {
        if (mPlayingExitAnimation) {
            return;
        }
        playExitAnimationIfNeededWithEndRunnable(new Runnable() {
            @Override
            public void run() {
                BaseDialog.super.dismissAllowingStateLoss();
            }
        });
    }

    private void playExitAnimationIfNeededWithEndRunnable(final Runnable postAnimationRunnable) {
        if (mExitAnimation == null) {
            postAnimationRunnable.run();
            return;
        }

        ObjectAnimator animator = mDialogAnimatorsFactory.newExitAnimator(mExitAnimation, mExitAnimationDurationMs);

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                mPlayingExitAnimation = false;
                postAnimationRunnable.run();
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });

        animator.start();
        mPlayingExitAnimation = true;
    }

    public void setEnterAnimation(@Nullable DialogEnterAnimation enterAnimation) {
        mEnterAnimation = enterAnimation;
    }

    public void setEnterAnimationDuration(int durationMs) {
        mEnterAnimationDurationMs = durationMs;
    }

    public void setExitAnimation(@Nullable DialogExitAnimation exitAnimation) {
        mExitAnimation = exitAnimation;
    }

    public void setExitAnimationDuration(int durationMs) {
        mExitAnimationDurationMs = durationMs;
    }

    private Window getDialogWindow() {
        Window dialogWindow = getDialog().getWindow();
        if (dialogWindow == null) {
            // dialog's window shouldn't be null at this point, but just in case let's fail fast to surface the problem
            throw new RuntimeException("dialog window is null");
        }
        return dialogWindow;
    }

    private View getDialogDecorView() {
        return getDialogWindow().getDecorView();
    }

    /**
     * This Factory encapsulates the logic that defines animations' details
     */
    private class DialogAnimatorsFactory {

        public ObjectAnimator newEnterAnimator(DialogEnterAnimation enterAnimation, long durationMs) {
            if (enterAnimation == null) {
                throw new IllegalStateException("enter animation mustn't be null");
            }

            ObjectAnimator animator;
            switch (enterAnimation) {
                case SLIDE_IN_FROM_RIGHT:
                    animator = ObjectAnimator.ofFloat(
                            getDialogDecorView(),
                            "translationX",
                            requireActivity().getWindow().getDecorView().getWidth(),
                            0
                    );
                    break;
                case SLIDE_IN_AND_BOUNCE_FROM_TOP:
                    animator = ObjectAnimator.ofFloat(
                            getDialogDecorView(),
                            "translationY",
                            -requireActivity().getWindow().getDecorView().getHeight(),
                            0
                    );
                    animator.setInterpolator(new BounceInterpolator());
                    break;
                default:
                    throw new RuntimeException("unhandled enter animation: " + enterAnimation);
            }

            animator.setDuration(durationMs);
            return animator;
        }

        public ObjectAnimator newExitAnimator(DialogExitAnimation exitAnimation, long durationMs) {
            if (exitAnimation == null) {
                throw new IllegalStateException("exit animation mustn't be null");
            }

            ObjectAnimator animator;
            switch (exitAnimation) {
                case SLIDE_OUT_FROM_LEFT:
                    animator = ObjectAnimator.ofFloat(
                            getDialogDecorView(),
                            "translationX",
                            0,
                            -requireActivity().getWindow().getDecorView().getWidth()
                    );
                    break;
                case SLIDE_OUT_FROM_BOTTOM:
                    animator = ObjectAnimator.ofFloat(
                            getDialogDecorView(),
                            "translationY",
                            0,
                            requireActivity().getWindow().getDecorView().getHeight()
                    );
                    break;
                default:
                    throw new RuntimeException("unhandled exit animation: " + exitAnimation);
            }

            animator.setDuration(durationMs);
            return animator;
        }

    }

}
