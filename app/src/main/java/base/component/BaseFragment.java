package base.component;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ActionMode;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.trello.rxlifecycle2.components.support.RxFragment;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import impl.component.AppApplication;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import base.utils.WeakReferenceViewRunnable;
import base.widget.LoadingDialog;
import skean.yzsm.com.framework.R;

/**
 * App的DialogFragment基类 <p/>
 */
@SuppressWarnings("unused")
public abstract class BaseFragment extends RxFragment {
    protected  Bundle savedInstanceStateCache ;
    protected AppApplication app;
    protected BaseHostActivity hostActivity;
    private Context context;
    protected ActionBar actionBar;
    protected LoadingDialog loadingDialog;

    protected float fragmentIndex;

    protected boolean isMenuCreated;

    public ActionMode tempActionMode;

    protected ContextThemeWrapper alertTheme;

    Toast toast;

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
        savedInstanceStateCache = savedInstanceState;
        app = hostActivity.getAppApplication();
        alertTheme = new ContextThemeWrapper(getContext(), R.style.Theme_AppCompat_Light_Dialog_Alert);
        toast = hostActivity.getToast();
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
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        isMenuCreated = true;
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
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 状态获取/上下文相关
    ///////////////////////////////////////////////////////////////////////////

    public boolean isMenuCreated() {
        return isMenuCreated;
    }

    public AppApplication getAppApplication() {
        return app;
    }

    public BaseHostActivity getHostActivity() {
        return hostActivity;
    }

    public BaseFragment getThis() {
        return this;
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

    public void setLoaded(String text) {
        hostActivity.setLoaded(text);
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
        toast.setText(stringId);
        toast.setDuration(toastLength);
        toast.show();
    }

    public void toast(String text, int toastLength) {
        toast.setText(text);
        toast.setDuration(toastLength);
        toast.show();

    }

    public void toast(int stringId) {
        toast.setText(stringId);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();

    }

    public void toast(String text) {
        toast.setText(text);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    public void toastFormat(String text, Object... args) {
        String content = String.format(text, args);
        toast(content);
    }

    public void toastFormat(@StringRes int resId, Object... args) {
        String content = getString(resId, args);
        toast(content);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 本地广播相关
    ///////////////////////////////////////////////////////////////////////////

    public LocalBroadcastManager getLocalBroadcastManager() {
        return hostActivity.getLocalBroadcastManager();
    }

    public boolean sendLocalBroadcast(Intent intent) {
        return hostActivity.sendLocalBroadcast(intent);
    }

    public void sendLocalBroadcastSync(Intent intent) {
        hostActivity.sendLocalBroadcastSync(intent);
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

    /**
     * 展示软键盘
     * <p/>
     * * @return 是否有执行展示软键盘的操作
     */
    public boolean showSoftKeyboard(EditText target) {
        return hostActivity.showSoftKeyboard(target);
    }

    /**
     * 展示软键盘, 并且光标移到最后面
     * * @return 是否有执行展示软键盘的操作
     */
    public boolean showSoftKeyboardAndMoveToEnd(EditText target) {
        return hostActivity.showSoftKeyboardAndMoveToEnd(target);
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

    protected AlertDialog.Builder buildAlert(String title, String message) {
        return new AlertDialog.Builder(alertTheme).setTitle(title).setMessage(message);
    }

    protected AlertDialog.Builder buildAlert(int titleId, int messageId) {
        return new AlertDialog.Builder(alertTheme).setTitle(titleId).setMessage(messageId);
    }

    private Scheduler io(){
        return Schedulers.io();
    }


    private Scheduler mainThread(){
        return AndroidSchedulers.mainThread();
    }


}
