package skean.me.base.component;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.view.ActionMode;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import skean.me.base.widget.LoadingDialog;
import skean.yzsm.com.hzevent.R;

/**
 * App的Fragment基类 <p/>
 */
@SuppressWarnings("unused")
public abstract class BaseFragment extends Fragment {
    protected AppApplication app;
    protected BaseHostActivity hostActivity;
    private Context context;
    protected ActionBar actionBar;
    protected LoadingDialog loadingDialog;

    protected float fragmentIndex;

    protected static final int MAX_INTERVAL_FOR_CLICK = 250;
    protected static final int MAX_DISTANCE_FOR_CLICK = 100;
    protected static final int FILTER_FOR_CLICK = 300;

    public ActionMode tempActionMode;

    protected ContextThemeWrapper alertTheme;

    ///////////////////////////////////////////////////////////////////////////
    // 设置/生命周期/初始化
    ///////////////////////////////////////////////////////////////////////////

    public BaseFragment() {
        this.fragmentIndex = getFragmentIndex();
    }

    public abstract float getFragmentIndex();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = hostActivity.getAppApplication();
        alertTheme = new ContextThemeWrapper(getContext(), R.style.Theme_AppCompat_Light_Dialog_Alert);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.hostActivity = (BaseHostActivity) activity;
        getHostActivity().currentFragment = this;
    }

    /**
     * 自定义ActionBar的操作
     */
    public void customizeActionBar() {
    }

    /**
     * 清除ActionBar自定义内容
     */
    public void clearActionBar() {
    }

    public boolean onBack() {
        return true;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 状态获取/上下文相关
    ///////////////////////////////////////////////////////////////////////////

    public AppApplication getAppApplication() {
        return app;
    }

    public BaseHostActivity getHostActivity() {
        return hostActivity;
    }

    public Handler getMainHandler() {
        return hostActivity.getMainHandler();
    }

    protected ActionBar getSupportActionBar() {
        return hostActivity.getSupportActionBar();
    }

    protected ActionMode startSupportActionMode(ActionMode.Callback callback) {
        return hostActivity.startSupportActionMode(callback);
    }

    public boolean isActiveNetworkAvailable() {
        return hostActivity.isActiveNetworkAvailable();
    }

    public boolean isAnyNetworkAvailable() {
        return hostActivity.isAnyNetworkAvailable();
    }

    public boolean isGpsEnabled() {
        return hostActivity.isGpsEnabled();
    }

    public boolean isSdcardMounted(boolean alert, DialogInterface.OnClickListener listener) {
        return hostActivity.isSdcardMounted(alert, listener);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 进度框便利方法
    ///////////////////////////////////////////////////////////////////////////

    public void showLoading(boolean cancelable) {
        hostActivity.showLoading(cancelable);
    }

    public void showLoading(int stringId, boolean cancelable) {
        hostActivity.showLoading(stringId, cancelable);
    }

    public void showLoading(String text, boolean cancelable) {
        hostActivity.showLoading(text, cancelable);
    }

    public void setLoaded() {
        hostActivity.setLoaded();
    }

    public void setLoadingText(String text) {
        hostActivity.setLoadingText(text);
    }

    public void setLoadingText(int resId) {
        hostActivity.setLoadingText(resId);
    }

    public void dismissLoading() {
        hostActivity.dismissLoading();
    }

    public void dismissLoadingDelayed(long millis) {
        hostActivity.dismissLoadingDelayed(millis);
    }

    ///////////////////////////////////////////////////////////////////////////
    // toast便利方法
    ///////////////////////////////////////////////////////////////////////////

    public void toast(int stringId, int toastLength) {
        Toast.makeText(context, stringId, toastLength).show();
    }

    public void toast(String text, int toastLength) {
        Toast.makeText(context, text, toastLength).show();
    }

    public void toast(int stringId) {
        Toast.makeText(context, stringId, Toast.LENGTH_SHORT).show();
    }

    public void toast(String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public void toastFormat(String text, Object... args) {
        Toast.makeText(context, String.format(text, args), Toast.LENGTH_SHORT).show();
    }

    public void toastFormat(@StringRes int resId, Object... args) {
        Toast.makeText(context, getString(resId, args), Toast.LENGTH_SHORT).show();
    }

    ///////////////////////////////////////////////////////////////////////////
    // 便利方法
    ///////////////////////////////////////////////////////////////////////////

    public void postInMain(Runnable r) {
        hostActivity.postInMain(r);
    }

    public void postInMainDelayed(Runnable r, long delayMillis) {
        hostActivity.postInMainDelayed(r, delayMillis);
    }

    /**
     * 隐藏软键盘 <p/>
     *
     * @return 是否有执行隐藏软键盘的操作
     */
    public boolean hideSoftInput() {
        return hostActivity.hideSoftKeyboard();
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
