package me.skean.skeanframework.component.function;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import me.skean.skeanframework.R;

/**
 * 加载中Dialog
 */
public class LoadingDialog2 extends AlertDialog {

    private static final String TAG = "LoadingDialog2";

    private final int MESSAGE_PROGRESS = 0;
    private final int MESSAGE_INTERVAL = 1;
    private final int MESSAGE_DISMISS = 2;

    private final int UPDATE_INTERVAL = 100;

    private ProgressBar pgbProgress;
    private ImageView imvFinish;
    private TextView txvLoadingText;

    private int mProgressVal;
    private CharSequence mMessage;

    private boolean mHasStarted;
    private Handler mHandler;

    public LoadingDialog2(Context context) {
        super(context, android.R.style.Theme_Dialog);
    }

    public static LoadingDialog2 show(Context context, CharSequence message) {
        return show(context, message, true, null);
    }

    public static LoadingDialog2 show(Context context, CharSequence message, boolean cancelable) {
        return show(context, message, cancelable, null);
    }

    public static LoadingDialog2 show(Context context, CharSequence message, boolean cancelable, OnCancelListener cancelListener) {
        LoadingDialog2 dialog = new LoadingDialog2(context);
        dialog.setMessage(message);
        dialog.setCancelable(cancelable);
        dialog.setOnCancelListener(cancelListener);
        dialog.show();
        return dialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.dimAmount = 0.0f;
            window.setAttributes(params);
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setWindowAnimations(R.style.NullAnimationDialog);
        }
        mHandler = new Handler(callback);
        setContentView(R.layout.sfw_dialog_loading);
        pgbProgress = (ProgressBar) findViewById(R.id.pgbProgress);
        imvFinish = (ImageView) findViewById(R.id.imvFinish);
        txvLoadingText = (TextView) findViewById(R.id.txvLoadingText);
        setMessage(mMessage);
    }

    @Override
    public void onStart() {
        super.onStart();
        mHasStarted = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHasStarted = false;
        mHandler.removeCallbacksAndMessages(null);
    }

    private Handler.Callback callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_PROGRESS:
                    txvLoadingText.setText(mProgressVal + "%");
                    break;
                case MESSAGE_DISMISS:
                    dismiss();
                    break;
            }
            return false;
        }
    };

    public void setProgress(int value) {
        mProgressVal = value;
        if (mHasStarted) {
            onProgressChanged();
        }
    }

    public int getProgress() {
        return mProgressVal;
    }

    @Override
    public void setMessage(CharSequence message) {
        mMessage = message;
        if (txvLoadingText != null) txvLoadingText.setText(mMessage);
    }

    public CharSequence getMessage() {
        return mMessage;
    }

    public void setFinishAndDismiss(long delayed) {
        pgbProgress.setVisibility(View.GONE);
        txvLoadingText.setText("完成");
        mHandler.sendEmptyMessageDelayed(MESSAGE_DISMISS, delayed);
    }

    private void onProgressChanged() {
        if (mHandler != null && !mHandler.hasMessages(MESSAGE_INTERVAL)) {
            mHandler.sendEmptyMessage(MESSAGE_PROGRESS);
            mHandler.sendEmptyMessageDelayed(MESSAGE_INTERVAL, UPDATE_INTERVAL);
        }
    }

}
