package me.skean.skeanframework.net;

import java.io.IOException;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


/**
 * 全局post参数添加的拦截器
 */
public class AddPostParamsInterceptor implements Interceptor {

    private Map<String, String> params;

    public AddPostParamsInterceptor(Map<String, String> params) {
        this.params = params;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : params.keySet()) {
            builder.add(key, params.get(key));
        }
        request = request.newBuilder().post(builder.build()).build();
        return chain.proceed(request);
    }
}
