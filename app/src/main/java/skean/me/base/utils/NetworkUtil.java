package skean.me.base.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import okhttp3.CookieJar;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
//import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import skean.me.base.component.AppApplication;
import skean.me.base.net.ProgressInterceptor;
import skean.me.base.net.ProgressInterceptor.DownloadListener;
import skean.me.base.net.ProgressInterceptor.UploadListener;
import skean.yzsm.com.framework.BuildConfig;

/**
 * 网络框架工具类
 */

public class NetworkUtil {
    public static final int TIME_OUT = 10;

    /**
     * 生成基础OkHttpClient, 主要添加了超时设置, Cookies, 和自动输出HttpLog
     *
     * @return OkHttpClient
     */
    public static OkHttpClient.Builder newAppHttpBuilder() {
        return new OkHttpClient.Builder().connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                                         .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                                         .cookieJar(persistentCookieJar())
                                         .addInterceptor(httpLoggingInterceptor(false));
    }

    public static OkHttpClient.Builder newAppHttpProgressBuilder() {
        return new OkHttpClient.Builder().connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                                         .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                                         .cookieJar(persistentCookieJar())
                                         .addInterceptor(httpLoggingInterceptor(true))
                                         .addNetworkInterceptor(new ProgressInterceptor());
    }

    public static OkHttpClient.Builder newAppHttpProgressBuilder(UploadListener uploadListener, DownloadListener downloadListener) {
        return new OkHttpClient.Builder().connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                                         .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                                         .cookieJar(persistentCookieJar())
                                         .addInterceptor(httpLoggingInterceptor(true))
                                         .addNetworkInterceptor(new ProgressInterceptor(uploadListener, downloadListener));
    }

    /**
     * http请求的logger拦截器
     *
     * @return Interceptor
     */
    public static Interceptor httpLoggingInterceptor(boolean forceBasic) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(forceBasic || !BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BASIC : HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }

    /**
     * 用SharedPreferences持久化保存Cookie的CookieJar
     *
     * @return CookieJar
     */
    public static CookieJar persistentCookieJar() {
        return new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(AppApplication.getContext()));
    }

    public static ObjectMapper newObjectMapper() {
        return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                                 .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
    }

    public static Retrofit baseRetrofit(String baseUrl) {
        return new Retrofit.Builder().baseUrl(baseUrl)
                                     .client(newAppHttpBuilder().build())
                                     .addConverterFactory(JacksonConverterFactory.create(newObjectMapper()))
                                     .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                     .build();
    }

    public static Retrofit baseRetrofit(String baseUrl, ObjectMapper mapper) {
        return new Retrofit.Builder().baseUrl(baseUrl)
                                     .client(newAppHttpBuilder().build())
                                     .addConverterFactory(JacksonConverterFactory.create(mapper))
                                     .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                     .build();
    }

    public static Retrofit baseRetrofit(String baseUrl, Interceptor... interceptors) {
        OkHttpClient.Builder builder = newAppHttpBuilder();
        builder.interceptors().addAll(0, Arrays.asList(interceptors));
        return new Retrofit.Builder().baseUrl(baseUrl)
                                     .client(builder.build())
                                     .addConverterFactory(JacksonConverterFactory.create(newObjectMapper()))
                                     .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                     .build();
    }

    public static Retrofit progressRetrofit(String baseUrl) {
        return new Retrofit.Builder().baseUrl(baseUrl)
                                     .client(newAppHttpProgressBuilder().build())
                                     .addConverterFactory(JacksonConverterFactory.create(newObjectMapper()))
                                     .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                     .build();
    }

    public static Retrofit progressRetrofit(String baseUrl, Interceptor... interceptors) {
        OkHttpClient.Builder builder = newAppHttpProgressBuilder();
        builder.interceptors().addAll(0, Arrays.asList(interceptors));
        return new Retrofit.Builder().baseUrl(baseUrl)
                                     .client(builder.build())
                                     .addConverterFactory(JacksonConverterFactory.create(newObjectMapper()))
                                     .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                     .build();
    }

    public static Retrofit progressRetrofit(String baseUrl, UploadListener uploadListener, DownloadListener downloadListener) {
        return new Retrofit.Builder().baseUrl(baseUrl)
                                     .client(newAppHttpProgressBuilder(uploadListener, downloadListener).build())
                                     .addConverterFactory(JacksonConverterFactory.create(newObjectMapper()))
                                     .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                     .build();
    }

    public static Retrofit setUploadListener(Retrofit retrofit, ProgressInterceptor.UploadListener upLoadListener) {
        OkHttpClient client = (OkHttpClient) retrofit.callFactory();
        for (Interceptor interceptor : client.networkInterceptors()) {
            if (interceptor instanceof ProgressInterceptor) {
                ((ProgressInterceptor) interceptor).setUploadListener(upLoadListener);
                break;
            }
        }
        return retrofit;
    }

    public static Retrofit setDownloadListener(Retrofit retrofit, ProgressInterceptor.DownloadListener downLoadListener) {
        OkHttpClient client = (OkHttpClient) retrofit.callFactory();
        for (Interceptor interceptor : client.networkInterceptors()) {
            if (interceptor instanceof ProgressInterceptor) {
                ((ProgressInterceptor) interceptor).setDownloadListener(downLoadListener);
                break;
            }
        }
        return retrofit;
    }

    public static ResponseBody multiParamPart(String content) {
        return ResponseBody.create(MultipartBody.FORM, content);
    }

    public static MultipartBody.Part multiParamPart(String name, String content) {
        return MultipartBody.Part.createFormData(name, content);

    }

    public static MultipartBody.Part multiFilePart(String name, File uploadFile) {
        return MultipartBody.Part.createFormData(name,
                                                 uploadFile.getName(),
                                                 RequestBody.create(MediaType.parse("application/otcet-stream"), uploadFile));
    }
}
