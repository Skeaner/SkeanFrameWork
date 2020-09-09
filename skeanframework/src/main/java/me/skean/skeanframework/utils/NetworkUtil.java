package me.skean.skeanframework.utils;

import android.webkit.MimeTypeMap;

import com.blankj.utilcode.util.FileUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.kotlin.KotlinModule;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import org.apache.commons.lang3.reflect.FieldUtils;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import me.skean.skeanframework.component.SkeanFrameWork;
import me.skean.skeanframework.net.ProgressInterceptor;
import okhttp3.CookieJar;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

//import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * 网络框架工具类
 */

public class NetworkUtil {
    public static final int TIME_OUT = 10;

    private static HttpLoggingInterceptor.Level httpLogLevel = HttpLoggingInterceptor.Level.BASIC;

    public static void setHttpLogLevel(HttpLoggingInterceptor.Level httpLogLevel) {
        NetworkUtil.httpLogLevel = httpLogLevel;
    }

    private static HashMap<Class, String> baseUrlMaps = new HashMap<>();

    public static <T> T buildService(Class<T> clazz) {
        String baseUrl = getBaseUrlForClass(clazz);
        Retrofit retrofit = NetworkUtil.baseRetrofit(baseUrl);
        return retrofit.create(clazz);
    }

    public static <T> T buildService(Class<T> clazz, Interceptor... interceptors) {
        String baseUrl = getBaseUrlForClass(clazz);
        Retrofit retrofit = NetworkUtil.baseRetrofit(baseUrl, interceptors);
        return retrofit.create(clazz);
    }

    private static String getBaseUrlForClass(Class clazz) {
        if (baseUrlMaps.containsKey(clazz)) {
            return baseUrlMaps.get(clazz);
        } else {
            String url = null;
            try {
                url = (String) FieldUtils.readStaticField(clazz, "BASE_URL");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (url == null) {
                throw new RuntimeException("请给Service指定一个publish static String BASE_URL 的属性!");
            }
            baseUrlMaps.put(clazz, url);
            return url;
        }
    }

    /**
     * 生成一个不安全信任全部https证书的okhttpBuilder
     */
    public static OkHttpClient.Builder newUnsafeAppHttpBuilder() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[]{};
                }
            }};
            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = newAppHttpBuilder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);
            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

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

    public static OkHttpClient.Builder newAppHttpProgressBuilder(ProgressInterceptor.UploadListener uploadListener,
                                                                 ProgressInterceptor.DownloadListener downloadListener) {
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
    public static Interceptor httpLoggingInterceptor(boolean basic) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(basic ? HttpLoggingInterceptor.Level.BASIC : httpLogLevel);
        return interceptor;
    }

    /**
     * 用SharedPreferences持久化保存Cookie的CookieJar
     *
     * @return CookieJar
     */
    public static CookieJar persistentCookieJar() {
        return new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(SkeanFrameWork.getContext()));
    }

    public static ObjectMapper newObjectMapper() {
        return new ObjectMapper().registerModule(new KotlinModule())
                                 .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
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

    public static Retrofit progressRetrofit(String baseUrl,
                                            ProgressInterceptor.UploadListener uploadListener,
                                            ProgressInterceptor.DownloadListener downloadListener) {
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

    public static MultipartBody.Part paramMultiPart(String name, String content) {
        return MultipartBody.Part.createFormData(name, content);
    }

    public static MultipartBody.Part fileMultiPart(String name, File uploadFile) {
        String type = null;
        String filename = uploadFile.getName();
        String extension = FileUtils.getFileExtension(uploadFile);
        if (!ContentUtil.isEmpty(extension)) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        if (ContentUtil.isEmpty(type)) {
            type = "application/octet-stream";
        }
        return MultipartBody.Part.createFormData(name, filename, RequestBody.create(MediaType.parse(type), uploadFile));
    }
}
