package skean.me.base.utils;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.CookieJar;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

import retrofit2.converter.fastjson.FastJsonConverterFactory;
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

    /**
     * 生成带进度监控OkHttpClient, 主要添加了超时设置, Cookies, 和自动输出HttpLog ,进度监控(需要后续设置监听器)
     *
     * @return OkHttpClient
     */
    public static OkHttpClient.Builder newAppHttpProgressBuilder() {
        return new OkHttpClient.Builder().connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                                         .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                                         .cookieJar(persistentCookieJar())
                                         .addInterceptor(httpLoggingInterceptor(true))
                                         .addNetworkInterceptor(new ProgressInterceptor());
    }

    /**
     * 生成带进度监控OkHttpClient, 主要添加了超时设置, Cookies, 和自动输出HttpLog ,进度监控
     *
     * @param uploadListener   上传监听器
     * @param downloadListener 下载监听器
     * @return OkHttpClient
     */
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

    /**
     * 基础Retrofit解析器, 添加了FastJson作为Json解析器, 添加了RX适配器
     *
     * @param baseUrl 域名基本url
     * @return Retrofit解析器
     */
    public static Retrofit baseRetrofit(String baseUrl) {
        return new Retrofit.Builder().baseUrl(baseUrl)
                                     .client(newAppHttpBuilder().build())
                                     .addConverterFactory(FastJsonConverterFactory.create())
                                     .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                                     .build();
    }

    /**
     * 基础Retrofit解析器, 添加了FastJson作为Json解析器, 添加了RX适配器
     *
     * @param baseUrl      域名基本url
     * @param parserConfig FastJson的解析设置项目
     * @return Retrofit解析器
     */
    public static Retrofit baseRetrofit(String baseUrl, ParserConfig parserConfig) {
        return new Retrofit.Builder().baseUrl(baseUrl)
                                     .client(newAppHttpBuilder().build())
                                     .addConverterFactory(FastJsonConverterFactory.create().setParserConfig(parserConfig))
                                     .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                                     .build();
    }

    /**
     * 进度监控Retrofit解析器, 添加了FastJson作为Json解析器, 添加了RX适配器, 进度监控(需要后续设置监听器)
     *
     * @param baseUrl 域名基本url
     * @return Retrofit解析器
     */
    public static Retrofit progressRetrofit(String baseUrl) {
        return new Retrofit.Builder().baseUrl(baseUrl)
                                     .client(newAppHttpProgressBuilder().build())
                                     .addConverterFactory(FastJsonConverterFactory.create())
                                     .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                                     .build();
    }

    /**
     * 进度监控Retrofit解析器, 添加了FastJson作为Json解析器, 添加了RX适配器, 进度监控
     *
     * @param baseUrl          域名基本url
     * @param uploadListener   上传监听器
     * @param downloadListener 下载监听器
     * @return Retrofit解析器
     */
    public static Retrofit progressRetrofit(String baseUrl, UploadListener uploadListener, DownloadListener downloadListener) {
        return new Retrofit.Builder().baseUrl(baseUrl)
                                     .client(newAppHttpProgressBuilder(uploadListener, downloadListener).build())
                                     .addConverterFactory(FastJsonConverterFactory.create())
                                     .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                                     .build();
    }

    /**
     * 为Retrofit设置上传监听器
     *
     * @param retrofit       Retrofit
     * @param upLoadListener 上传监听器
     * @return Retrofit
     */
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

    /**
     * 为Retrofit设置下载监听器
     *
     * @param retrofit         Retrofit
     * @param downLoadListener 下载监听器
     * @return Retrofit
     */
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

    /**
     * 生成当使用Retrofit进行MultiPart请求时候需要的ResponseBody参数  , 注意content应该传一个完整参数 , 比如"name=Skean"这样的完整参数
     *
     * @param content 参数
     * @return ResponseBody
     */
    public static ResponseBody multiParamPart(String content) {
        return ResponseBody.create(MultipartBody.FORM, content);
    }

    /**
     * 生成当使用Retrofit进行MultiPart请求时候需要的MultipartBody.Part参数 , 类型为text, 具体信息为"{name}={content}"
     *
     * @param name    名称
     * @param content 值
     * @return ResponseBody
     */
    public static MultipartBody.Part multiParamPart(String name, String content) {
        return MultipartBody.Part.createFormData(name, content);

    }

    /**
     * 生成当使用Retrofit进行MultiPart请求时候需要的MultipartBody.Part的文件参数 , 类型为text, 具体信息为"name={name};filename={uploadFile.getName()}"
     *
     * @param name       名称
     * @param uploadFile 值
     * @return ResponseBody
     */
    public static MultipartBody.Part multiFilePart(String name, File uploadFile) {
        return MultipartBody.Part.createFormData(name, uploadFile.getName(), RequestBody.create(MultipartBody.FORM, uploadFile));
    }
}
