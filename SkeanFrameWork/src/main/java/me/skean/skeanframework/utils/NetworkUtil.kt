package me.skean.skeanframework.utils

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.webkit.MimeTypeMap
import cn.numeron.okhttp.log.LogLevel
import cn.numeron.okhttp.log.TextLogInterceptor
import com.blankj.utilcode.util.FileUtils.getFileExtension
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.Flow
import me.skean.skeanframework.net.BaseUrl
import me.skean.skeanframework.net.FileIOApi
import me.skean.skeanframework.net.ProgressDownloadFlow
import me.skean.skeanframework.net.ProgressDownloadObservable
import me.skean.skeanframework.net.ProgressUploadFlow
import me.skean.skeanframework.net.ProgressUploadObservable
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
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

    var timeout = 20
    private var logLevel: LogLevel = LogLevel.HEADERS
    private lateinit var context: Context

    @JvmStatic
    fun init(context: Context, logLevel: LogLevel) {
        NetworkUtil.logLevel = logLevel
        this.context = context
    }

    @JvmStatic
    @JvmOverloads
    inline fun <reified T> createService(timeoutSec: Int = timeout, om: ObjectMapper? = null, vararg interceptors: Interceptor): T {
        val baseUrl = getBaseUrlForClass(T::class.java)
        val retrofit = newRetrofit(baseUrl, timeoutSec = timeoutSec, om = om, interceptors = interceptors)
        return retrofit.create(T::class.java)
    }

    @JvmStatic
    @JvmOverloads
    fun <T> createService(clazz: Class<T>, timeoutSec: Int = timeout, om: ObjectMapper? = null, vararg interceptors: Interceptor): T {
        val baseUrl = getBaseUrlForClass(clazz)
        val retrofit = newRetrofit(baseUrl, timeoutSec = timeoutSec, om = om, interceptors = interceptors)
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
            throw java.lang.RuntimeException("请给j接口定义一个带 @BaseUrl 注释的 String 属性!")
        } else {
            val url = baseUrlField.get(null) as String
            return url
        }
    }


    @JvmStatic
    @JvmOverloads
    fun newRetrofit(baseUrl: String, timeoutSec: Int = timeout, om: ObjectMapper? = null, vararg interceptors: Interceptor): Retrofit {
        val builder = newOkhttpBuilder(timeoutSec)
        builder.interceptors().addAll(0, interceptors.toList())
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(builder.build())
            .addConverterFactory(JacksonConverterFactory.create(om ?: newObjectMapper()))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
    }

    @JvmStatic
    fun OkHttpClient.Builder.trustAllSslCer(): OkHttpClient.Builder {
        val trustAllCerts = arrayOf<TrustManager>(
            @SuppressLint("CustomX509TrustManager")
            object : X509TrustManager {
                @SuppressLint("TrustAllX509TrustManager")
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                }

                @SuppressLint("TrustAllX509TrustManager")
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
        return this.sslSocketFactory(sslSocketFactory, (trustAllCerts[0] as X509TrustManager))
            .hostnameVerifier { hostname: String?, session: SSLSession? -> true }
    }

    @JvmStatic
    @JvmOverloads
    fun newOkhttpBuilder(timeoutSec: Int = timeout): OkHttpClient.Builder {
        return OkHttpClient.Builder()
            .connectTimeout(timeoutSec.toLong(), TimeUnit.SECONDS)
            .readTimeout(timeoutSec.toLong(), TimeUnit.SECONDS)
            .cookieJar(newPersistentCookieJar())
            .addHttpLogInterceptor()
            .trustAllSslCer()
    }


    @JvmStatic
    fun OkHttpClient.Builder.addHttpLogInterceptor(): OkHttpClient.Builder {
        this.addInterceptor(
            TextLogInterceptor().setRequestLevel(logLevel).setResponseLevel(logLevel)
        )
        return this
    }

    /**
     * 用SharedPreferences持久化保存Cookie的CookieJar
     */
    @JvmStatic
    fun newPersistentCookieJar(): CookieJar {
        return PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(context))
    }

    @JvmStatic
    fun newObjectMapper(): ObjectMapper {
        return ObjectMapper().registerModule(KotlinModule.Builder().build())
            .registerModule(JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
    }

    @JvmStatic
    fun newParamPart(name: String, content: String): MultipartBody.Part {
        return MultipartBody.Part.createFormData(name, content)
    }

    @JvmStatic
    fun String.toMultiPart(name: String): MultipartBody.Part {
        return newParamPart(name, this)
    }


    @JvmStatic
    @JvmOverloads
    fun newFilePart(name: String = "file", uploadFile: File): MultipartBody.Part {
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
    @JvmOverloads
    fun File.toMultiPart(name: String = "file"): MultipartBody.Part {
        return newFilePart(name, this)
    }

    @JvmStatic
    @JvmOverloads
    fun Call<*>.toProgressDownloadObservable(savedFile: File, progressInterval: Int = 5): Observable<Int> {
        return ProgressDownloadObservable(this, savedFile, progressInterval)
    }

    @JvmStatic
    @JvmOverloads
    fun downloadObservable(url: String, savedFile: File, progressInterval: Int = 5): Observable<Int> =
        createService<FileIOApi>().downloadToCall(url).toProgressDownloadObservable(savedFile, progressInterval)


    @JvmStatic
    @JvmOverloads
    fun Call<*>.toProgressUploadObservable(progressInterval: Int = 5): Observable<Int> {
        return ProgressUploadObservable(this, progressInterval)
    }

    @JvmStatic
    @JvmOverloads
    fun uploadObservable(url: String, uploadFile: File, progressInterval: Int = 5): Observable<Int> =
        createService<FileIOApi>().uploadToCall(url, uploadFile.toMultiPart()).toProgressUploadObservable(progressInterval)

    @JvmStatic
    @JvmOverloads
    fun Call<*>.toProgressDownloadFlow(savedFile: File, progressInterval: Int = 5): Flow<Int> {
        return ProgressDownloadFlow(this, savedFile, progressInterval).produce()
    }

    @JvmStatic
    @JvmOverloads
    fun downloadFlow(url: String, savedFile: File, progressInterval: Int = 5): Flow<Int> =
        createService<FileIOApi>().downloadToCall(url).toProgressDownloadFlow(savedFile, progressInterval)

    @JvmStatic
    @JvmOverloads
    fun Call<*>.toProgressUploadFlow(progressInterval: Int = 5): Flow<Int> {
        return ProgressUploadFlow(this, progressInterval).produce()
    }

    @JvmStatic
    @JvmOverloads
    fun uploadFlow(url: String, uploadFile: File, progressInterval: Int = 5): Flow<Int> =
        createService<FileIOApi>().uploadToCall(url, uploadFile.toMultiPart()).toProgressUploadFlow(progressInterval)

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
                var fields = fieldNames;
                if (fields.isEmpty()) {
                    fields = arrayOf("msg", "message", "err", "errMessage", "errorMessage")
                }
                for (field in fields) {
                    if (jo.has(field)) {
                        info = "${jo.get(field)}"
                        break
                    }
                }
                return "$code: $info"
            } else {
                return e.message ?: "未知错误"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "未知错误"
    }


    @JvmStatic
    inline fun <reified T> parseErrorBodyMsg(e: Throwable, vararg props: KProperty1<T, String?>): String {
        val namesArray = props.map { it.name }.toTypedArray()
        return parseErrorBodyMsg(e, *namesArray)
    }


}