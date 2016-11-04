package skean.me.base.component;

import android.content.Context;
import android.content.DialogInterface;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import skean.me.base.widget.LoadingDialog;
import skean.yzsm.com.framework.R;

/**
 * App的Activity基类 <p/>
 */
@SuppressWarnings("unused")
public class BaseActivity extends AppCompatActivity {
    protected AppApplication app;
    protected Context context = null;
    protected ActionBar actionBar;
    protected LoadingDialog loadingDialog;

    private Handler mainHandler;


    protected boolean useHomeAsBack = true;
    protected boolean isMenuCreated  = false;

    public static final int RESULT_MODIFIED = -2;
    public static final int RESULT_DELETE = -3;
    public static final int RESULT_ADD = -4;
    public static final int RESULT_ERROR = -5;

    protected ContextThemeWrapper alertTheme;

    protected static final int MAX_INTERVAL_FOR_CLICK = 250;
    protected static final int MAX_DISTANCE_FOR_CLICK = 100;
    protected static final int FILTER_FOR_CLICK = 300;

    Toast toast;

    ///////////////////////////////////////////////////////////////////////////
    // 声明周期/初始化/设置
    ///////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (AppApplication) getApplication();
        context = this;
        initActionBar();
        mainHandler = new Handler();
        alertTheme = new ContextThemeWrapper(context, R.style.Theme_AppCompat_Light_Dialog_Alert);
        toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadingDialog != null) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result =  super.onCreateOptionsMenu(menu);
        isMenuCreated  = true;
        return result;
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
        return getLoadingDialog(getString(R.string.loading), true).setFinished(false);
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

    public boolean isActiveNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            NetworkInfo network = manager.getActiveNetworkInfo();
            if (network != null && network.isConnectedOrConnecting()) {
                return true;
            }
        }
        return false;
    }

    public boolean isAnyNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            NetworkInfo[] info = manager.getAllNetworkInfo();
            if (info.length != 0) {
                for (NetworkInfo networkInfo : info) {
                    if (NetworkInfo.State.CONNECTED.equals(networkInfo.getState())) return true;
                }
            }
        }
        return false;
    }

    public boolean isGpsEnabled() {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public boolean isSdcardMounted(boolean alert, DialogInterface.OnClickListener listener) {
        boolean enabled = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
        if (alert) {
            new AlertDialog.Builder(alertTheme).setTitle(R.string.tips)
                                               .setMessage(R.string.noSdcardMounted)
                                               .setPositiveButton(R.string.confirm, listener)
                                               .show();
        }
        return enabled;
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
