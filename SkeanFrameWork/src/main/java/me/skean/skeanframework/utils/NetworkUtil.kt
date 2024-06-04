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
import me.skean.skeanframework.event.NotAuthorisedEvent
import me.skean.skeanframework.net.ProgressInterceptor
import me.skean.skeanframework.net.ProgressRequestBody
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.internal.platform.Platform
import okhttp3.logging.HttpLoggingInterceptor
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import java.io.File
import java.lang.reflect.Proxy
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.*
import kotlin.reflect.*
import  io.reactivex.Observable
import me.skean.skeanframework.net.BaseUrl
import me.skean.skeanframework.net.ProgressResponseObservable
import retrofit2.Call

//import retrofit2.converter.jackson.JacksonConverterFactory;
/**
 * 网络框架工具类
 */
@SuppressLint("StaticFieldLeak")
object NetworkUtil {

    var timeout = 15
    private var httpLogLevel: HttpLoggingInterceptor.Level? = null
    private var context: Context? = null

    @JvmStatic
    fun init(context: Context, httpLogLevel: HttpLoggingInterceptor.Level?) {
        NetworkUtil.httpLogLevel = httpLogLevel
        this.context = context
    }

    @JvmStatic
    @JvmOverloads
    inline fun <reified T> createService(timeoutSeconds: Int = timeout): T {
        val baseUrl = getBaseUrlForClass(T::class.java)
        val retrofit = baseRetrofit(baseUrl, timeoutSeconds)
        return retrofit.create(T::class.java)
    }

    @JvmStatic
    @JvmOverloads
    inline fun <reified T> createUnsafeService(timeoutSeconds: Int = timeout): T {
        val baseUrl = getBaseUrlForClass(T::class.java)
        val retrofit = unsafeBaseRetrofit(baseUrl, timeoutSeconds)
        return retrofit.create(T::class.java)
    }

    @JvmStatic
    @JvmOverloads
    fun <T> createService(clazz: Class<T>, timeoutSeconds: Int = timeout): T {
        val baseUrl = getBaseUrlForClass(clazz)
        val retrofit = baseRetrofit(baseUrl, timeoutSeconds)
        return retrofit.create(clazz)
    }


    @JvmStatic
    @JvmOverloads
    fun <T> createUnsafeService(clazz: Class<T>, timeoutSeconds: Int = timeout): T {
        val baseUrl = getBaseUrlForClass(clazz)
        val retrofit = unsafeBaseRetrofit(baseUrl, timeoutSeconds)
        return retrofit.create(clazz)
    }

    @JvmStatic
    @JvmOverloads
    inline fun <reified T> createService(vararg interceptors: Interceptor, timeoutSeconds: Int = timeout): T {
        val baseUrl = getBaseUrlForClass(T::class.java)
        val retrofit = baseRetrofit(baseUrl, interceptors.toList(), timeoutSeconds)
        return retrofit.create(T::class.java)
    }

    @JvmStatic
    @JvmOverloads
    inline fun <reified T> createUnsafeService(vararg interceptors: Interceptor, timeoutSeconds: Int = timeout): T {
        val baseUrl = getBaseUrlForClass(T::class.java)
        val retrofit = unsafeBaseRetrofit(baseUrl, interceptors.toList(), timeoutSeconds)
        return retrofit.create(T::class.java)
    }

    @JvmStatic
    @JvmOverloads
    fun <T> createService(clazz: Class<T>, vararg interceptors: Interceptor, timeoutSeconds: Int = timeout): T {
        val baseUrl = getBaseUrlForClass(clazz)
        val retrofit = baseRetrofit(baseUrl, interceptors.toList(), timeoutSeconds)
        return retrofit.create(clazz)
    }


    @JvmStatic
    @JvmOverloads
    fun <T> createUnsafeService(clazz: Class<T>, vararg interceptors: Interceptor, timeoutSeconds: Int = timeout): T {
        val baseUrl = getBaseUrlForClass(clazz)
        val retrofit = unsafeBaseRetrofit(baseUrl, interceptors.toList(), timeoutSeconds)
        return retrofit.create(clazz)
    }


    inline fun <reified T> getBaseUrl(): String {
        val clazz = T::class.java
        return getBaseUrlForClass(clazz)
    }

    @JvmStatic
    fun getBaseUrlForClass(clazz: Class<*>): String {
        val baseUrlField =
            clazz.declaredFields.find { it.isAnnotationPresent(BaseUrl::class.java) && it.type.isAssignableFrom(String::class.java) }
        if (baseUrlField == null) {
            throw  java.lang.RuntimeException("请给j接口定义一个带 @BaseUrl 注释的 String 属性!")
        } else {
            val url = baseUrlField.get(null) as String
            return url
        }
    }

    @JvmStatic
    @JvmOverloads
    fun newAppHttpBuilder(timeoutSeconds: Int = timeout): OkHttpClient.Builder {
        return OkHttpClient.Builder()
            .connectTimeout(timeoutSeconds.toLong(), TimeUnit.SECONDS)
            .readTimeout(timeoutSeconds.toLong(), TimeUnit.SECONDS)
            .authenticator(tokenAuthenticator())
            .cookieJar(persistentCookieJar())
            .addInterceptor(httpLoggingInterceptor(false))
    }

    @JvmStatic
    @JvmOverloads
    fun newUnsafeAppHttpBuilder(timeoutSeconds: Int = timeout): OkHttpClient.Builder {
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
            val builder = newAppHttpBuilder(timeoutSeconds)
            builder.sslSocketFactory(sslSocketFactory, (trustAllCerts[0] as X509TrustManager))
            builder.hostnameVerifier({ hostname: String?, session: SSLSession? -> true })
            builder
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun newAppHttpProgressBuilder(timeoutSeconds: Int = timeout): OkHttpClient.Builder {
        return OkHttpClient.Builder()
            .connectTimeout(timeoutSeconds.toLong(), TimeUnit.SECONDS)
            .readTimeout(timeoutSeconds.toLong(), TimeUnit.SECONDS)
            .authenticator(tokenAuthenticator())
            .cookieJar(persistentCookieJar())
            .addInterceptor(httpLoggingInterceptor(true))
            .addNetworkInterceptor(ProgressInterceptor())
    }


    @JvmStatic
    @JvmOverloads
    fun newUnsafeAppHttpProgressBuilder(timeoutSeconds: Int = timeout): OkHttpClient.Builder {
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
            val builder = newAppHttpProgressBuilder(timeoutSeconds)
            builder.sslSocketFactory(sslSocketFactory, (trustAllCerts[0] as X509TrustManager))
            builder.hostnameVerifier({ hostname: String?, session: SSLSession? -> true })
            builder
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun newAppHttpProgressBuilder(
        uploadListener: ProgressInterceptor.UploadListener?,
        downloadListener: ProgressInterceptor.DownloadListener?,
        timeoutSeconds: Int = timeout
    ): OkHttpClient.Builder {
        return OkHttpClient.Builder()
            .connectTimeout(timeoutSeconds.toLong(), TimeUnit.SECONDS)
            .readTimeout(timeoutSeconds.toLong(), TimeUnit.SECONDS)
            .authenticator(tokenAuthenticator())
            .cookieJar(persistentCookieJar())
            .addInterceptor(httpLoggingInterceptor(true))
            .addNetworkInterceptor(ProgressInterceptor(uploadListener, downloadListener))
    }

    @JvmStatic
    @JvmOverloads
    fun newUnsafeAppHttpProgressBuilder(
        uploadListener: ProgressInterceptor.UploadListener?,
        downloadListener: ProgressInterceptor.DownloadListener?,
        timeoutSeconds: Int = timeout
    ): OkHttpClient.Builder {
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
            val builder = newAppHttpProgressBuilder(uploadListener, downloadListener, timeoutSeconds)
            builder.sslSocketFactory(sslSocketFactory, (trustAllCerts[0] as X509TrustManager))
            builder.hostnameVerifier({ hostname: String?, session: SSLSession? -> true })
            builder
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    @JvmStatic
    fun tokenAuthenticator(): Authenticator {
        return object : Authenticator {
            override fun authenticate(route: Route?, response: Response): Request {
                EventBus.getDefault().post(NotAuthorisedEvent(response))
                return response.request
            }

        }
    }

    /**
     * http请求的logger拦截器
     *
     * @return Interceptor
     */
    @JvmStatic
    fun httpLoggingInterceptor(basic: Boolean): Interceptor {
        val interceptor = HttpLoggingInterceptor(logger = object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                message.split("\r\n").forEach {
                    if (it.contains("�")) {
                        Platform.get().log("<BINARY DATA>")
                    } else {
                        Platform.get().log(it)
                    }
                }
            }
        })
        interceptor.setLevel(if (basic) HttpLoggingInterceptor.Level.BASIC else httpLogLevel!!)
        return interceptor
    }

    /**
     * 用SharedPreferences持久化保存Cookie的CookieJar
     *
     * @return CookieJar
     */
    @JvmStatic
    fun persistentCookieJar(): CookieJar {
        return PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(context))
    }

    @JvmStatic
    fun newObjectMapper(): ObjectMapper {
        return ObjectMapper().registerModule(KotlinModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
    }

    @JvmStatic
    @JvmOverloads
    fun baseRetrofit(baseUrl: String?, timeoutSeconds: Int = timeout): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(newAppHttpBuilder(timeoutSeconds).build())
            .addConverterFactory(JacksonConverterFactory.create(newObjectMapper()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    @JvmStatic
    @JvmOverloads
    fun baseRetrofit(baseUrl: String?, mapper: ObjectMapper?, timeoutSeconds: Int = timeout): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(newAppHttpBuilder(timeoutSeconds).build())
            .addConverterFactory(JacksonConverterFactory.create(mapper))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    @JvmStatic
    @JvmOverloads
    fun baseRetrofit(baseUrl: String?, vararg interceptors: Interceptor, timeoutSeconds: Int = timeout): Retrofit {
        return baseRetrofit(baseUrl, interceptors.toList(), timeoutSeconds)
    }


    @JvmStatic
    @JvmOverloads
    fun baseRetrofit(baseUrl: String?, interceptors: List<Interceptor>, timeoutSeconds: Int = timeout): Retrofit {
        val builder = newAppHttpBuilder(timeoutSeconds)
        builder.interceptors().addAll(0, interceptors)
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(builder.build())
            .addConverterFactory(JacksonConverterFactory.create(newObjectMapper()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }


    @JvmStatic
    @JvmOverloads
    fun unsafeBaseRetrofit(baseUrl: String?, timeoutSeconds: Int = timeout): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(newUnsafeAppHttpBuilder(timeoutSeconds).build())
            .addConverterFactory(JacksonConverterFactory.create(newObjectMapper()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    @JvmStatic
    @JvmOverloads
    fun unsafeBaseRetrofit(baseUrl: String?, mapper: ObjectMapper?, timeoutSeconds: Int = timeout): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(newUnsafeAppHttpBuilder(timeoutSeconds).build())
            .addConverterFactory(JacksonConverterFactory.create(mapper))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    @JvmStatic
    @JvmOverloads
    fun unsafeBaseRetrofit(baseUrl: String?, vararg interceptors: Interceptor, timeoutSeconds: Int = timeout): Retrofit {
        return unsafeBaseRetrofit(baseUrl, interceptors.toList(), timeoutSeconds)
    }


    @JvmStatic
    @JvmOverloads
    fun unsafeBaseRetrofit(baseUrl: String?, interceptors: List<Interceptor>, timeoutSeconds: Int = timeout): Retrofit {
        val builder = newUnsafeAppHttpBuilder(timeoutSeconds)
        builder.interceptors().addAll(0, interceptors)
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(builder.build())
            .addConverterFactory(JacksonConverterFactory.create(newObjectMapper()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    @JvmStatic
    @JvmOverloads
    fun progressRetrofit(baseUrl: String?, timeoutSeconds: Int = timeout): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(newAppHttpProgressBuilder(timeoutSeconds).build())
            .addConverterFactory(JacksonConverterFactory.create(newObjectMapper()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    @JvmStatic
    @JvmOverloads
    fun progressRetrofit(baseUrl: String?, vararg interceptors: Interceptor, timeoutSeconds: Int = timeout): Retrofit {
        return progressRetrofit(baseUrl, interceptors.toList(), timeoutSeconds)
    }


    @JvmStatic
    @JvmOverloads
    fun progressRetrofit(baseUrl: String?, interceptors: List<Interceptor>, timeoutSeconds: Int = timeout): Retrofit {
        val builder = newAppHttpProgressBuilder(timeoutSeconds)
        builder.interceptors().addAll(0, interceptors)
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(builder.build())
            .addConverterFactory(JacksonConverterFactory.create(newObjectMapper()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    @JvmStatic
    @JvmOverloads
    fun progressRetrofit(
        baseUrl: String?,
        uploadListener: ProgressInterceptor.UploadListener?,
        downloadListener: ProgressInterceptor.DownloadListener?,
        timeoutSeconds: Int = timeout
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(newAppHttpProgressBuilder(uploadListener, downloadListener, timeoutSeconds).build())
            .addConverterFactory(JacksonConverterFactory.create(newObjectMapper()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }


    @JvmStatic
    @JvmOverloads
    fun unsafeProgressRetrofit(baseUrl: String?, timeoutSeconds: Int = timeout): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(newUnsafeAppHttpProgressBuilder(timeoutSeconds).build())
            .addConverterFactory(JacksonConverterFactory.create(newObjectMapper()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    @JvmStatic
    @JvmOverloads
    fun unsafeProgressRetrofit(baseUrl: String?, vararg interceptors: Interceptor, timeoutSeconds: Int = timeout): Retrofit {
        return unsafeProgressRetrofit(baseUrl, interceptors.toList(), timeoutSeconds)
    }


    @JvmStatic
    @JvmOverloads
    fun unsafeProgressRetrofit(baseUrl: String?, interceptors: List<Interceptor>, timeoutSeconds: Int = timeout): Retrofit {
        val builder = newUnsafeAppHttpProgressBuilder(timeoutSeconds)
        builder.interceptors().addAll(0, interceptors)
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(builder.build())
            .addConverterFactory(JacksonConverterFactory.create(newObjectMapper()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    @JvmStatic
    @JvmOverloads
    fun unsafeProgressRetrofit(
        baseUrl: String?,
        uploadListener: ProgressInterceptor.UploadListener?,
        downloadListener: ProgressInterceptor.DownloadListener?,
        timeoutSeconds: Int = timeout
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(newUnsafeAppHttpProgressBuilder(uploadListener, downloadListener, timeoutSeconds).build())
            .addConverterFactory(JacksonConverterFactory.create(newObjectMapper()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    @JvmStatic
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

    @JvmStatic
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

    @JvmStatic
    fun paramMultiPart(name: String, content: String): MultipartBody.Part {
        return MultipartBody.Part.createFormData(name, content)
    }

    @JvmStatic
    fun fileMultiPart(name: String = "file", uploadFile: File): MultipartBody.Part {
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

    @JvmStatic
    fun fileMultiPartWithProgress(
        name: String = "file",
        uploadFile: File,
        listener: ProgressRequestBody.UploadCallback? = null
    ): MultipartBody.Part {
        var type: String? = ""
        val filename = uploadFile.name
        val extension = getFileExtension(uploadFile.path)
        if (!TextUtils.isEmpty(extension)) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        if (TextUtils.isEmpty(type)) {
            type = "application/octet-stream"
        }
        return MultipartBody.Part.createFormData(name, filename, ProgressRequestBody(uploadFile, type, listener))
    }

    @JvmStatic
    fun downloadWithProgress(url: String, downloadFile: File) = ProgressResponseObservable(url, downloadFile)

    @JvmStatic
    fun Call<*>.excuteWithProgress(downloadFile: File): Observable<Int> {
        val url = request().url.toString()
        return ProgressResponseObservable(url, downloadFile)
    }

    @JvmStatic
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


    @JvmStatic
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

    @JvmStatic
    fun toastErrorBodyMsg(e: Throwable, vararg fieldNames: String) {
        ToastUtils.showShort(parseErrorBodyMsg(e, *fieldNames))
    }

    @JvmStatic
    inline fun <reified T> parseErrorBodyMsg(e: Throwable, vararg props: KProperty1<T, String?>): String {
        val namesArray = props.map { it.name }.toTypedArray()
        return parseErrorBodyMsg(e, *namesArray)
    }

    @JvmStatic
    inline fun <reified T> toastErrorBodyMsg(e: Throwable, vararg props: KProperty1<T, String?>) {
        ToastUtils.showShort(parseErrorBodyMsg(e, *props))
    }
}