package skean.me.base.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tamic.jswebview.browse.BridgeHandler;
import com.tamic.jswebview.browse.BridgeWebView;
import com.tamic.jswebview.browse.CallBackFunction;
import com.tamic.jswebview.browse.JsWeb.CustomWebChromeClient;
import com.tamic.jswebview.browse.JsWeb.CustomWebViewClient;
import com.tamic.jswebview.browse.JsWeb.JavaCallHandler;
import com.tamic.jswebview.browse.JsWeb.JsHandler;
import com.tamic.jswebview.view.NumberProgressBar;

import java.util.ArrayList;
import java.util.Map;

import skean.me.base.utils.AppUrl;
import skean.me.base.utils.ContentUtil;
import skean.yzsm.com.framework.R;

/**
 * 封装好的ProgressWebView
 */
public class AppProgressWebView extends LinearLayout {

    private static final String TAG = "AppProgressWebView";

    public static final String LOVE_TREE_SCHEME = "lovetreemall";

    private NumberProgressBar mProgressBar;
    private BridgeWebView mWebView;
    private String token;
    private CallBack callBack;

    public interface CallBack {
        void onFinished(String title);
    }

    public AppProgressWebView(Context context) {
        super(context);
        init(context, null);
    }

    public AppProgressWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public AppProgressWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AppProgressWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setOrientation(LinearLayout.VERTICAL);

        // 初始化进度条
        if (mProgressBar == null) {
            mProgressBar = new NumberProgressBar(context, attrs);
            mProgressBar.setId(R.id.pwvProgressBar);
        }
        addView(mProgressBar);

        // 初始化webview
        if (mWebView == null) {
            mWebView = new BridgeWebView(context);
            mWebView.setId(R.id.pwvWebView);
        }

        mWebView.setWebChromeClient(new CustomWebChromeClient(mProgressBar));
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (callBack != null) callBack.onFinished(view.getTitle());
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                Uri uri = Uri.parse(url);
//                if (ContentUtil.isEqual(LOVE_TREE_SCHEME, uri.getScheme())) {
//                    String host = uri.getHost();
//                    Bundle extras = new Bundle();
//                    for (String key : uri.getQueryParameterNames()) {
//                        String value = uri.getQueryParameter(key);
//                        extras.putString(key, value);
//                    }
//                    try {
//                        Router.sharedRouter().open(host, extras);
//                    } catch (Exception e) {
//                        Toast.makeText(getContext(), "不合法的协议!", Toast.LENGTH_SHORT).show();
//                    }
//                    return true;
//                } else
//
                    return super.shouldOverrideUrlLoading(view, addTokenIfNeeded(url));
            }
        });

        WebSettings settings = mWebView.getSettings();
        // 判断系统版本是不是5.0或之上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //让系统不屏蔽混合内容和第三方Cookie
            CookieManager.getInstance().setAcceptThirdPartyCookies(mWebView, true);
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        // 不支持缩放
        settings.setSupportZoom(false);
        settings.setJavaScriptEnabled(true);
        settings.setGeolocationEnabled(true);

        // 自适应屏幕大小
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        mWebView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        //
        mWebView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
                        mWebView.goBack();
                        return true;
                    }
                }
                return false;
            }
        });
        addView(mWebView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public NumberProgressBar getProgressBar() {
        return mProgressBar;
    }

    public BridgeWebView getWebView() {
        return mWebView;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String addTokenIfNeeded(String url) {
        if (!ContentUtil.isEmpty(token)) {
            AppUrl.Builder builder = AppUrl.parseBuilder(url);
            if (builder != null) {
                builder.addQueryParameter("token", token);
                return builder.toString();
            }
        }
        return url;
    }

    public CallBack getCallBack() {
        return callBack;
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    /**
     * Loads the given URL.
     *
     * @param url the URL of the resource to load
     */
    public void loadUrl(String url) {
        loadUrl(url, null);
    }

    /**
     * Loads the given URL with the specified additional HTTP headers.
     *
     * @param url                   the URL of the resource to load
     * @param additionalHttpHeaders the additional headers to be used in the
     *                              HTTP request for this URL, specified as a map from name to
     *                              value. Note that if this map contains any of the headers
     *                              that are set by default by this WebView, such as those
     *                              controlling caching, accept types or the User-Agent, their
     *                              values may be overriden by this WebView's defaults.
     */
    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
        loadUrl(url, additionalHttpHeaders, null);
    }

    /**
     * Loads the given URL with the specified additional HTTP headers.
     *
     * @param url                   the URL of the resource to load
     * @param additionalHttpHeaders the additional headers to be used in the
     *                              HTTP request for this URL, specified as a map from name to
     *                              value. Note that if this map contains any of the headers
     *                              that are set by default by this WebView, such as those
     *                              controlling caching, accept types or the User-Agent, their
     *                              values may be overriden by this WebView's defaults.
     * @param returnCallback        the CallBackFunction to be Used call js registerHandler Function,
     *                              rerurn response data.
     */
    public void loadUrl(String url, Map<String, String> additionalHttpHeaders, CallBackFunction returnCallback) {
        mWebView.loadUrl(addTokenIfNeeded(url), additionalHttpHeaders, returnCallback);
    }

    public void setWebViewClient(CustomWebViewClient client) {
        mWebView.setWebViewClient(client);
    }

    public void setWebChromeClient(CustomWebChromeClient chromeClient) {
        mWebView.setWebChromeClient(chromeClient);
    }

    /**
     * @param handler default handler,handle messages send by js without assigned handler name,
     *                if js message has handler name, it will be handled by named handlers registered by native
     */
    public void setDefaultHandler(BridgeHandler handler) {
        mWebView.setDefaultHandler(handler);
    }

    public void send(String data) {
        mWebView.send(data);
    }

    public void send(String data, CallBackFunction responseCallback) {
        mWebView.send(data, responseCallback);
    }

    /**
     * 注册本地java方法，以供js端调用
     *
     * @param handlerName 方法名称
     * @param handler     回调接口
     */
    public void registerHandler(final String handlerName, final JsHandler handler) {
        mWebView.registerHandler(handlerName, new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                if (handler != null) {
                    handler.OnHandler(handlerName, data, function);
                }
            }
        });
    }

    /**
     * 批量注册本地java方法，以供js端调用
     *
     * @param handlerNames 方法名称数组
     * @param handler      回调接口
     */
    public void registerHandlers(final ArrayList<String> handlerNames, final JsHandler handler) {
        if (handler != null) {
            for (final String handlerName : handlerNames) {
                mWebView.registerHandler(handlerName, new BridgeHandler() {
                    @Override
                    public void handler(String data, CallBackFunction function) {
                        handler.OnHandler(handlerName, data, function);
                    }
                });
            }
        }
    }

    /**
     * 调用js端已经注册好的方法
     *
     * @param handlerName 方法名称
     * @param javaData    本地端传递给js端的参数，json字符串
     * @param handler     回调接口
     */
    public void callHandler(final String handlerName, String javaData, final JavaCallHandler handler) {
        mWebView.callHandler(handlerName, javaData, new CallBackFunction() {
            @Override
            public void onCallBack(String data) {
                if (handler != null) {
                    handler.OnHandler(handlerName, data);
                }
            }
        });
    }

    /**
     * 批量调用js端已经注册好的方法
     *
     * @param handlerInfos 方法名称与参数的map，方法名为key
     * @param handler      回调接口
     */
    public void callHandler(final Map<String, String> handlerInfos, final JavaCallHandler handler) {
        if (handler != null) {
            for (final Map.Entry<String, String> entry : handlerInfos.entrySet()) {
                mWebView.callHandler(entry.getKey(), entry.getValue(), new CallBackFunction() {
                    @Override
                    public void onCallBack(String data) {
                        handler.OnHandler(entry.getKey(), data);
                    }
                });
            }
        }
    }
}
