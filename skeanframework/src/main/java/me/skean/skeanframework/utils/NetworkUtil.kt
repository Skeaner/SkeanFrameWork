package me.skean.skeanframework.utils

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.webkit.MimeTypeMap
import com.blankj.utilcode.util.FileUtils.getFileExtension
import com.blankj.utilcode.util.ToastUtils
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import me.skean.skeanframework.net.ProgressInterceptor
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.logging.HttpLoggingInterceptor
import org.apache.commons.lang3.reflect.FieldUtils
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import java.io.File
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*
import kotlin.reflect.*

//import retrofit2.converter.jackson.JacksonConverterFactory;
/**
 * 网络框架工具类
 */
@SuppressLint("StaticFieldLeak")
object NetworkUtil {

    var timeout = 10
    private var httpLogLevel: HttpLoggingInterceptor.Level? = null
    private var context: Context? = null

    fun init(context: Context, httpLogLevel: HttpLoggingInterceptor.Level?) {
        NetworkUtil.httpLogLevel = httpLogLevel
        this.context = context
    }

    inline fun <reified T> createService(): T {
        val baseUrl = getBaseUrlForClass(T::class.java)
        val retrofit = baseRetrofit(baseUrl)
        return retrofit.create(T::class.java)
    }

    fun <T> createService(clazz: Class<T>): T {
        val baseUrl = getBaseUrlForClass(clazz)
        val retrofit = baseRetrofit(baseUrl)
        return retrofit.create(clazz)
    }

    inline fun <reified T> createService(vararg interceptors: Interceptor): T {
        val baseUrl = getBaseUrlForClass(T::class.java)
        val retrofit = baseRetrofit(baseUrl, *interceptors)
        return retrofit.create(T::class.java)
    }

    fun <T> createService(clazz: Class<T>, vararg interceptors: Interceptor): T {
        val baseUrl = getBaseUrlForClass(clazz)
        val retrofit = baseRetrofit(baseUrl, *interceptors)
        return retrofit.create(clazz)
    }

    fun getBaseUrlForClass(clazz: Class<*>): String? {
        if (clazz.isAnnotationPresent(Metadata::class.java)) { //kotlin的接口
            try {
                val implClass = Class.forName("${clazz.name}\$DefaultImpls")
                val method = implClass.getDeclaredMethod("getBaseUrl", clazz)
                return method.invoke(null, null).toString()
            } catch (e: Exception) {
                throw  java.lang.RuntimeException("请给接口定义一个 var baseUrl: String 的接口属性!")
            }
        } else { //java的接口
            var url: String? = null
            try {
                url = FieldUtils.readStaticField(clazz, "BASE_URL") as String
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (url == null) {
                throw RuntimeException("请给接口指定一个public static String BASE_URL 的属性!")
            } else {
                return url
            }
        }
    }

    /**
     * 生成一个不安全信任全部https证书的okhttpBuilder
     */
    fun newUnsafeAppHttpBuilder(): OkHttpClient.Builder {
        return try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                }

                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            })
            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory
            val builder = newAppHttpBuilder()
            builder.sslSocketFactory(sslSocketFactory, (trustAllCerts[0] as X509TrustManager))
            builder.hostnameVerifier(HostnameVerifier { hostname: String?, session: SSLSession? -> true })
            builder
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    /**
     * 生成基础OkHttpClient, 主要添加了超时设置, Cookies, 和自动输出HttpLog
     *
     * @return OkHttpClient
     */
    fun newAppHttpBuilder(): OkHttpClient.Builder {
        return OkHttpClient.Builder()
                .connectTimeout(timeout.toLong(), TimeUnit.SECONDS)
                .readTimeout(timeout.toLong(), TimeUnit.SECONDS)
                .cookieJar(persistentCookieJar())
                .addInterceptor(httpLoggingInterceptor(false))
    }

    fun newAppHttpProgressBuilder(): OkHttpClient.Builder {
        return OkHttpClient.Builder()
                .connectTimeout(timeout.toLong(), TimeUnit.SECONDS)
                .readTimeout(timeout.toLong(), TimeUnit.SECONDS)
                .cookieJar(persistentCookieJar())
                .addInterceptor(httpLoggingInterceptor(true))
                .addNetworkInterceptor(ProgressInterceptor())
    }

    fun newAppHttpProgressBuilder(uploadListener: ProgressInterceptor.UploadListener?,
                                  downloadListener: ProgressInterceptor.DownloadListener?): OkHttpClient.Builder {
        return OkHttpClient.Builder()
                .connectTimeout(timeout.toLong(), TimeUnit.SECONDS)
                .readTimeout(timeout.toLong(), TimeUnit.SECONDS)
                .cookieJar(persistentCookieJar())
                .addInterceptor(httpLoggingInterceptor(true))
                .addNetworkInterceptor(ProgressInterceptor(uploadListener, downloadListener))
    }

    /**
     * http请求的logger拦截器
     *
     * @return Interceptor
     */
    fun httpLoggingInterceptor(basic: Boolean): Interceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(if (basic) HttpLoggingInterceptor.Level.BASIC else httpLogLevel!!)
        return interceptor
    }

    /**
     * 用SharedPreferences持久化保存Cookie的CookieJar
     *
     * @return CookieJar
     */
    fun persistentCookieJar(): CookieJar {
        return PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(context))
    }

    fun newObjectMapper(): ObjectMapper {
        return ObjectMapper().registerModule(KotlinModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
    }

    fun baseRetrofit(baseUrl: String?): Retrofit {
        return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(newAppHttpBuilder().build())
                .addConverterFactory(JacksonConverterFactory.create(newObjectMapper()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

    fun baseRetrofit(baseUrl: String?, mapper: ObjectMapper?): Retrofit {
        return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(newAppHttpBuilder().build())
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

    fun baseRetrofit(baseUrl: String?, vararg interceptors: Interceptor): Retrofit {
        val builder = newAppHttpBuilder()
        builder.interceptors().addAll(0, interceptors.toList())
        return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(builder.build())
                .addConverterFactory(JacksonConverterFactory.create(newObjectMapper()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

    fun progressRetrofit(baseUrl: String?): Retrofit {
        return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(newAppHttpProgressBuilder().build())
                .addConverterFactory(JacksonConverterFactory.create(newObjectMapper()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

    fun progressRetrofit(baseUrl: String?, vararg interceptors: Interceptor): Retrofit {
        val builder = newAppHttpProgressBuilder()
        builder.interceptors().addAll(0, interceptors.toList())
        return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(builder.build())
                .addConverterFactory(JacksonConverterFactory.create(newObjectMapper()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

    fun progressRetrofit(baseUrl: String?,
                         uploadListener: ProgressInterceptor.UploadListener?,
                         downloadListener: ProgressInterceptor.DownloadListener?): Retrofit {
        return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(newAppHttpProgressBuilder(uploadListener, downloadListener).build())
                .addConverterFactory(JacksonConverterFactory.create(newObjectMapper()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

    fun setUploadListener(retrofit: Retrofit, upLoadListener: ProgressInterceptor.UploadListener?): Retrofit {
        val client = retrofit.callFactory() as OkHttpClient
        for (interceptor in client.networkInterceptors) {
            if (interceptor is ProgressInterceptor) {
                interceptor.setUploadListener(upLoadListener)
                break
            }
        }
        return retrofit
    }

    fun setDownloadListener(retrofit: Retrofit, downLoadListener: ProgressInterceptor.DownloadListener?): Retrofit {
        val client = retrofit.callFactory() as OkHttpClient
        for (interceptor in client.networkInterceptors) {
            if (interceptor is ProgressInterceptor) {
                interceptor.setDownloadListener(downLoadListener)
                break
            }
        }
        return retrofit
    }

    fun paramMultiPart(name: String, content: String): MultipartBody.Part {
        return MultipartBody.Part.createFormData(name, content)
    }

    fun fileMultiPart(name: String, uploadFile: File): MultipartBody.Part {
        var type: String? = ""
        val filename = uploadFile.name
        val extension = getFileExtension(uploadFile.path)
        if (!TextUtils.isEmpty(extension)) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        if (TextUtils.isEmpty(type)) {
            type = "application/octet-stream"
        }
        return MultipartBody.Part.createFormData(name, filename, RequestBody.create(type?.toMediaTypeOrNull(), uploadFile))
    }

    inline fun <reified T> parseErrorBody(e: Throwable): T? {
        try {
            if (e is HttpException) {
                val errorBody = e.response()?.errorBody()?.charStream()
                return newObjectMapper().readValue(errorBody!!)
            } else {
                return null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }


    fun parseErrorBodyMsg(e: Throwable, vararg fieldNames: String): String {
        try {
            if (e is HttpException) {
                val code = e.code()
                val errorBody = e.response()?.errorBody()?.string() ?: "{}"
                val jo = JSONObject(errorBody)
                var info: String? = null
                for (fieldName in fieldNames) {
                    if (!jo.isNull(fieldName)) {
                        info = jo.getString(fieldName)
                        break
                    }
                }
                return "$code: $info"
            } else {
                return e.localizedMessage
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "未知错误"
    }

    fun toastErrorBodyMsg(e: Throwable, vararg fieldNames: String) {
        ToastUtils.showShort(parseErrorBodyMsg(e, *fieldNames))
    }


    inline fun <reified T> parseErrorBodyMsg(e: Throwable, vararg props: KMutableProperty1<T, String?>): String {
        val namesArray = props.map { it.name }.toTypedArray()
        return parseErrorBodyMsg(e, *namesArray)
    }


    inline fun <reified T> toastErrorBodyMsg(e: Throwable, vararg props: KMutableProperty1<T, String?>) {
        ToastUtils.showShort(parseErrorBodyMsg(e, *props))
    }
}