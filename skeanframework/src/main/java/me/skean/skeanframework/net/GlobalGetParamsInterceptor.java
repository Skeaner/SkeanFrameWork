package me.skean.skeanframework.net;

import java.io.IOException;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 全局get参数添加的拦截器
 */
public class GlobalGetParamsInterceptor implements Interceptor {

    private Map<String, String> params;

    public GlobalGetParamsInterceptor(Map<String, String> params) {
        this.params = params;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        //获取原来的url信息
        HttpUrl originalHttpUrl = original.url();
        HttpUrl.Builder builder = originalHttpUrl.newBuilder();
        //添加附加信息
        for (String key : params.keySet()) {
            builder.addQueryParameter("key", params.get(key));
        }
        //构建新的信息
        Request.Builder requestBuilder = original.newBuilder().url(builder.build());
        Request request = requestBuilder.build();
        return chain.proceed(request);
    }
}
