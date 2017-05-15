package skean.me.base.net;

import android.support.annotation.IntDef;

import java.io.IOException;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static skean.me.base.net.GlobalPostParamsInterceptor.PostType.MULTI_PART;
import static skean.me.base.net.GlobalPostParamsInterceptor.PostType.FORM;

/**
 * 全局get参数添加的拦截器
 */
public class GlobalPostParamsInterceptor implements Interceptor {

    private Map<String, String> params;
    @PostType
    private int type;

    @IntDef(value = {MULTI_PART, FORM})
    @interface PostType {
        int MULTI_PART = 1;
        int FORM = 2;
    }

    public GlobalPostParamsInterceptor(Map<String, String> params, @PostType int type) {
        this.params = params;
        this.type = type;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (type == FORM) {
            FormBody.Builder builder = new FormBody.Builder();
            for (String key : params.keySet()) {
                builder.add(key, params.get(key));
            }
            request = request.newBuilder().post(builder.build()).build();
        } else if (type == MULTI_PART) {
            MultipartBody.Builder builder = new MultipartBody.Builder();
            for (String key : params.keySet()) {
                builder.addFormDataPart(key, params.get(key));
            }
            request = request.newBuilder().post(builder.build()).build();
        }
        return chain.proceed(request);
    }
}
