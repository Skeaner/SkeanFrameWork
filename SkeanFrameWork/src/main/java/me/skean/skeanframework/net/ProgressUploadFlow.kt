package me.skean.skeanframework.net

import com.blankj.utilcode.util.ReflectUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer
import okio.BufferedSink
import okio.BufferedSource
import okio.ForwardingSink
import okio.ForwardingSource
import okio.buffer
import retrofit2.Call
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.CancellationException
import kotlin.coroutines.suspendCoroutine

/**
 * Created by Skean on 2025/05/21.
 */
class ProgressUploadFlow(private val rawCall: Call<*>, private val progressInterval: Int = 5) {
    private var newCall: okhttp3.Call? = null
    private var totalBytes = 0L
    private var savedBytes = 0L
    private var currentPercentage = 0
    private var newPercentage = 0
    private var running = true;
    fun produce(): Flow<Int> {
        return channelFlow<Int> {
            val obj = ReflectUtils.reflect(rawCall).field("delegate").get<Any>()
            val rawClient = ReflectUtils.reflect(obj).field("callFactory").get<OkHttpClient>()

            val newClient = rawClient.newBuilder().addInterceptor { chain ->
                val req = chain.request()
                if (req.body == null) {
                    return@addInterceptor chain.proceed(req)
                }
                val newReq = req.newBuilder().method(req.method, object : RequestBody() {
                    private val body: RequestBody
                    private var bufferedSink: BufferedSink? = null

                    init {
                        body = req.body!!
                        totalBytes = body.contentLength()
                    }

                    override fun contentLength(): Long = body.contentLength()

                    override fun contentType(): MediaType? = body.contentType()

                    override fun writeTo(sink: BufferedSink) {
                        if (bufferedSink == null) {
                            val progressSink: ForwardingSink = object : ForwardingSink(sink) {
                                @Throws(IOException::class)
                                override fun write(source: Buffer, byteCount: Long) {
                                    super.write(source, byteCount)
                                    savedBytes += if (byteCount != -1L) byteCount else 0
                                    if (totalBytes != 0L) {
                                        newPercentage = ((savedBytes * 100L) / totalBytes).toInt()
                                    }
                                }
                            }
                            bufferedSink = progressSink.buffer()
                            body.writeTo(bufferedSink!!)
                            bufferedSink!!.flush()
                        }
                    }

                }).build()
                return@addInterceptor chain.proceed(newReq);
            }.build()
            GlobalScope.launch(Dispatchers.IO) {
                while (running) {
                    if (newPercentage - currentPercentage >= progressInterval) {
                        currentPercentage = newPercentage
                        send(currentPercentage)
                    }
                }
            }
            try {
                newCall = newClient.newCall(rawCall.request())
                val response: Response = newCall!!.execute()
                running = false
                if (response.code == 200) {
                    if (savedBytes != totalBytes) {
                        throw RuntimeException("下载出错, 下载大小与文件大小不符")
                    }
                } else {
                    throw RuntimeException(response.code.toString() + response.message)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            } finally {
            }
        }
            .flowOn(Dispatchers.IO)
            .onCompletion {
                running = false
                val isCancelled = it is CancellationException
                if (isCancelled && newCall?.isCanceled() == false && newCall!!.isExecuted()) {
                    newCall?.cancel()
                }
            }
    }

}