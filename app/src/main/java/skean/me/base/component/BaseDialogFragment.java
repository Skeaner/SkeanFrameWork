package skean.me.base.component;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.trello.rxlifecycle2.components.support.RxDialogFragment;

import skean.me.base.utils.WeakReferenceViewRunnable;
import skean.yzsm.com.framework.R;

/**
 * App的DialogFragment基类
 */
public class BaseDialogFragment extends RxDialogFragment implements DialogInterface.OnClickListener {

    private boolean dismissWhenPositiveClick = true;
    private int customizeAnimation = -1;
    protected NoticeDialogListener noticeDialogListener;
    protected static final int MAX_INTERVAL_FOR_CLICK = 250;
    protected static final int MAX_DISTANCE_FOR_CLICK = 100;

    ///////////////////////////////////////////////////////////////////////////
    // 接口内部类
    ///////////////////////////////////////////////////////////////////////////

    public interface NoticeDialogListener {
        void onDialogPositiveClick();

        void onDialogNegativeClick();
    }

    public static class DefaultNoticeDialogListener implements NoticeDialogListener {

        @Override
        public void onDialogPositiveClick() {

        }

        @Override
        public void onDialogNegativeClick() {

        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 生命周期
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onStart() {
        super.onStart();
        final Dialog dialog = getDialog();
        //修改动画
        if (customizeAnimation > 0) dialog.getWindow().setWindowAnimations(customizeAnimation);
        //修改返回键的内容
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP && onBack();
            }
        });
        if (!dismissWhenPositiveClick && dialog instanceof AlertDialog) {
            Button btnConfirm = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BaseDialogFragment.this.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                }
            });
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                if (noticeDialogListener != null) noticeDialogListener.onDialogPositiveClick();
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                if (noticeDialogListener != null) noticeDialogListener.onDialogNegativeClick();
                break;
        }
    }

    protected AppApplication getAppApplication() {
        return (AppApplication) getActivity().getApplication();
    }


    protected boolean onBack() {
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    //  设置
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 点击确定时候是否dismiss对话框
     *
     * @param set 是否
     */
    public BaseDialogFragment setDismissWhenPositiveClick(boolean set) {
        this.dismissWhenPositiveClick = set;
        return this;
    }

    /**
     * 设置弹出的动画, 注意这个styleId是在style中的两项设置的id
     *
     * @param styleId styId, 具体需要的两个item为 "android:windowEnterAnimation" 和 "android:windowExitAnimation"
     */
    public BaseDialogFragment setCustomizeAnimation(int styleId) {
        customizeAnimation = styleId;
        return this;
    }

    public NoticeDialogListener getNoticeDialogListener() {
        return noticeDialogListener;
    }

    public BaseDialogFragment setNoticeDialogListener(NoticeDialogListener noticeDialogListener) {
        this.noticeDialogListener = noticeDialogListener;
        return this;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Toast便利方法
    ///////////////////////////////////////////////////////////////////////////

    public void toast(int stringId, int toastLength) {
        Toast.makeText(getContext(), stringId, toastLength).show();
    }

    public void toast(String text, int toastLength) {
        Toast.makeText(getContext(), text, toastLength).show();
    }

    public void toast(int stringId) {
        Toast.makeText(getContext(), stringId, Toast.LENGTH_SHORT).show();
    }

    public void toast(String text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

    public void toastFormat(String text, Object... args) {
        Toast.makeText(getContext(), String.format(text, args), Toast.LENGTH_SHORT).show();
    }

    public void toastFormat(@StringRes int resId, Object... args) {
        Toast.makeText(getContext(), getString(resId, args), Toast.LENGTH_SHORT).show();
    }


    protected void hideViews(View... views) {
        for (View view : views) {
            view.post(new WeakReferenceViewRunnable(view) {
                @Override
                public void run() {
                    getView().setVisibility(View.GONE);
                }
            });
        }
    }

    protected void showViews(View... views) {
        for (View view : views) {
            view.post(new WeakReferenceViewRunnable(view) {
                @Override
                public void run() {
                    getView().setVisibility(View.VISIBLE);
                }
            });
        }
    }

    protected void enableViews(View... views) {
        for (View view : views) {
            view.post(new WeakReferenceViewRunnable(view) {
                @Override
                public void run() {
                    getView().setEnabled(true);
                }
            });
        }
    }

    protected boolean detectClickEvent(View v, MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            v.setTag(R.id.keyDownX, ev.getX());
            v.setTag(R.id.keyDownY, ev.getY());
        } else if (ev.getAction() == MotionEvent.ACTION_UP) {
            int dx = (int) Math.abs((float) v.getTag(R.id.keyDownX) - ev.getX());
            int dy = (int) Math.abs((float) v.getTag(R.id.keyDownY) - ev.getY());
            long dm = ev.getEventTime() - ev.getDownTime();
            return dx < MAX_DISTANCE_FOR_CLICK && dy < MAX_DISTANCE_FOR_CLICK && dm < MAX_INTERVAL_FOR_CLICK;
        }
        return false;
    }


    protected void setErrorAndRequestFocus(EditText et, String errMessage) {
        et.setError(errMessage);
        et.requestFocus();
    }


}
