package skean.me.base.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.util.Map;

import skean.me.base.utils.AppUrl;
import skean.me.base.utils.ContentUtil;

/**
 * 封装好的WebView
 */
public class AppWebView extends WebView {

    private static final String TAG = "AppProgressWebView";

    public static final String LOVE_TREE_SCHEME = "lovetreemall";

    private String token;
    private AppProgressWebView.CallBack callBack;

    public interface CallBack {
        void onFinished(String title);
    }

    public AppWebView(Context context) {
        super(context);
        init();
    }

    public AppWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public AppWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AppWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setWebViewClient(new WebViewClient() {

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
                return super.shouldOverrideUrlLoading(view, addTokenIfNeeded(url));
            }
        });

        WebSettings settings = getSettings();
        // 判断系统版本是不是5.0或之上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //让系统不屏蔽混合内容和第三方Cookie
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true);
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        // 不支持缩放
        settings.setSupportZoom(false);
        // 
        settings.setJavaScriptEnabled(true);
        settings.setGeolocationEnabled(true);

        // 自适应屏幕大小
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        //
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

    @Override
    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
        super.loadUrl(addTokenIfNeeded(url), additionalHttpHeaders);
    }

    @Override
    public void loadUrl(String url) {
        super.loadUrl(addTokenIfNeeded(url));
    }

    public AppProgressWebView.CallBack getCallBack() {
        return callBack;
    }

    public void setCallBack(AppProgressWebView.CallBack callBack) {
        this.callBack = callBack;
    }
}
