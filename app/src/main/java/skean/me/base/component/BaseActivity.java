package skean.me.base.component;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import skean.me.base.utils.WeakReferenceViewRunnable;
import skean.me.base.widget.LoadingDialog;
import skean.yzsm.com.framework.R;

/**
 * App的Activity基类 <p/>
 */
@SuppressWarnings("unused")
public class BaseActivity extends RxAppCompatActivity {
    protected AppApplication app;
    protected Context context = null;
    protected ActionBar actionBar;
    protected LoadingDialog loadingDialog;

    private Handler mainHandler;

    protected boolean useHomeAsBack = true;
    protected boolean isMenuCreated = false;

    public static final int RESULT_MODIFIED = -2;
    public static final int RESULT_DELETE = -3;
    public static final int RESULT_ADD = -4;
    public static final int RESULT_ERROR = -5;

    protected ContextThemeWrapper alertTheme;

    protected static final int FILTER_FOR_CLICK = 300;

    private Toast toast;

    private LocalBroadcastManager lbm;

    ///////////////////////////////////////////////////////////////////////////
    // 声明周期/初始化/设置
    ///////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (AppApplication) getApplication();
        context = this;
        lbm = LocalBroadcastManager.getInstance(getContext());
        registerForceUpdateReceiver();
        initActionBar();
        mainHandler = new Handler();
        alertTheme = new ContextThemeWrapper(context, R.style.Theme_AppCompat_Light_Dialog_Alert);
        toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
    }

    @Override
    protected void onDestroy() {
        unRegisterForceUpdateReceiver();
        super.onDestroy();
        if (loadingDialog != null) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        isMenuCreated = true;
        return result;
    }

    @Override
    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        super.setSupportActionBar(toolbar);
        initActionBar();
    }

    /**
     * 初始化Actionbar
     */
    /**
     * 初始化Actionbar
     */
    protected void initActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(useHomeAsBack);
        }
    }

    public void setUseHomeAsBack(boolean useHomeAsBack) {
        this.useHomeAsBack = useHomeAsBack;
        initActionBar();
    }

    /**
     * 返回键的行为 <p/>
     *
     * @return true:当前Activity执行了返回键相关操作,  false:当前Activity没有执行任何操作, 将会执行默认的操作(如finish())
     */
    public boolean onBack() {
        return actionBar != null && collapseActionView(actionBar);
    }

    @Override
    public void onBackPressed() {
        if (!onBack()) finish();
    }

    protected boolean collapseActionView(ActionBar actionBar) {
        boolean result = false;
        try {
            result = (boolean) ActionBar.class.getMethod("collapseActionView").invoke(actionBar);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home && useHomeAsBack) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 进度框便利方法
    ///////////////////////////////////////////////////////////////////////////

    public void showLoading(boolean cancelable) {
        showLoading(R.string.loading, cancelable);
    }

    public void showLoading(int stringId, boolean cancelable) {
        showLoading(getString(stringId), cancelable);
    }

    public void showLoading(String text, boolean cancelable) {
        getLoadingDialog(text, cancelable).setFinished(false).show();
    }

    public void setLoaded() {
        getLoadingDialog().setFinished(true).setLoadingText("");
    }

    public void setLoaded(String text) {
        getLoadingDialog().setFinished(true).setLoadingText(text);
    }

    public void setLoadingText(String text) {
        getLoadingDialog().setLoadingText(text);
    }

    public void setLoadingText(int resId) {
        getLoadingDialog().setLoadingText(getString(resId));
    }

    public void dismissLoading() {
        getLoadingDialog().dismiss();
    }

    public void dismissLoadingDelayed(long millis) {
        mainHandler.removeCallbacks(dismissTask);
        mainHandler.postDelayed(dismissTask, millis);
    }

    public Runnable dismissTask = new Runnable() {
        @Override
        public void run() {
            getLoadingDialog().dismiss();
        }
    };

    private LoadingDialog getLoadingDialog() {
        if (loadingDialog == null) {
            loadingDialog = getLoadingDialog(getString(R.string.loading), true).setFinished(false);
        }
        return loadingDialog;
    }

    private LoadingDialog getLoadingDialog(String text, boolean cancelable) {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(context, text, cancelable);
        } else {
            loadingDialog.setLoadingText(text).setCancelable(cancelable);
        }
        return loadingDialog;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 状态获取/上下文相关
    ///////////////////////////////////////////////////////////////////////////

    public AppApplication getAppApplication() {
        return app;
    }

    public Context getContext() {
        return context;
    }

    public Handler getMainHandler() {
        return mainHandler;
    }

    public boolean isMenuCreated() {
        return isMenuCreated;
    }

    ///////////////////////////////////////////////////////////////////////////
    // toast的便捷方法
    ///////////////////////////////////////////////////////////////////////////

    public Toast getToast() {
        return toast;
    }

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
    // Snack便捷方法
    ///////////////////////////////////////////////////////////////////////////

    public void snack(View parent, String text) {
        Snackbar snackbar = Snackbar.make(parent, text, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    public void snack(View parent, String text, int duration) {
        Snackbar snackbar = Snackbar.make(parent, text, duration);
        snackbar.show();
    }

    public void snackTop(View parent, String text, int duration) {
        Snackbar snackbar = Snackbar.make(parent, text, duration);
        final long millis = duration == Snackbar.LENGTH_SHORT ? 1500 : 2750;
        View view = snackbar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.gravity = Gravity.TOP;
        view.setLayoutParams(params);
        view.setVisibility(View.INVISIBLE);
        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onShown(final Snackbar sb) {
                sb.getView().setVisibility(View.VISIBLE);
                sb.getView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sb.getView().setVisibility(View.GONE);
                    }
                }, millis);
            }

        });
        snackbar.show();
    }

    ///////////////////////////////////////////////////////////////////////////
    // 本地广播相关
    ///////////////////////////////////////////////////////////////////////////

    public LocalBroadcastManager getLocalBroadcastManager() {
        return lbm;
    }

    public boolean sendLocalBroadcast(Intent intent) {
        return lbm.sendBroadcast(intent);
    }

    public void sendLocalBroadcastSync(Intent intent) {
        lbm.sendBroadcastSync(intent);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 强制更新相关
    ///////////////////////////////////////////////////////////////////////////
    private BroadcastReceiver forceUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(IntentKey.ACTION_FORCE_UPDATE_EXIT)) finish();
        }
    };

    private void registerForceUpdateReceiver() {
        lbm.registerReceiver(forceUpdateReceiver, new IntentFilter(IntentKey.ACTION_FORCE_UPDATE_EXIT));
    }

    private void unRegisterForceUpdateReceiver() {
        lbm.unregisterReceiver(forceUpdateReceiver);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 便利方法
    ///////////////////////////////////////////////////////////////////////////

    public void postInMain(Runnable r) {
        mainHandler.post(r);
    }

    public void postInMainDelayed(Runnable r, long millis) {
        mainHandler.postDelayed(r, millis);
    }

    /**
     * 隐藏软键盘 <p/>
     *
     * @return 是否有执行隐藏软键盘的操作
     */
    public boolean hideSoftKeyboard() {
        InputMethodManager kbManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null) {
                return kbManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return false;
    }

    /**
     * 展示软键盘
     * <p/>
     * * @return 是否有执行展示软键盘的操作
     */
    public boolean showSoftKeyboard(EditText target) {
        InputMethodManager kbManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        target.requestFocus();
        return kbManager.showSoftInput(target, 0);
    }

    /**
     * 展示软键盘, 并且光标移到最后面
     * * @return 是否有执行展示软键盘的操作
     */
    public boolean showSoftKeyboardAndMoveToEnd(EditText target) {
        InputMethodManager kbManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        target.requestFocus();
        target.setSelection(target.length());
        return kbManager.showSoftInput(target, 0);
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

    protected abstract class ProgressAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

        @Override
        protected void onPreExecute() {
            showLoading(true);
        }

        @Override
        protected void onPostExecute(Result result) {
            dismissLoading();
        }
    }

}
