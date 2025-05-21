package me.skean.skeanframework.net

import com.blankj.utilcode.util.ReflectUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSource
import okio.ForwardingSource
import okio.buffer
import retrofit2.Call
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.concurrent.CancellationException

/**
 * Created by Skean on 2025/05/21.
 */
class ProgressDownloadFlow(private val rawCall: Call<*>, private val savedFile: File, private val progressInterval: Int = 5) {
    private var newCall: okhttp3.Call? = null
    private var totalBytes = 0L
    private var savedBytes = 0L
    private var currentPercentage = 0
    private var newPercentage = 0

    fun produce(): Flow<Int> {
        return channelFlow<Int> {
            val obj = ReflectUtils.reflect(rawCall).field("delegate").get<Any>()
            val rawClient = ReflectUtils.reflect(obj).field("callFactory").get<OkHttpClient>()
            val newClient = rawClient.newBuilder().addInterceptor { chain ->
                val res = chain.proceed(chain.request())
                if (res.body==null) return@addInterceptor res
                res.newBuilder().body(object : ResponseBody() {
                    private val body: ResponseBody
                    private var bufferedSource: BufferedSource? = null

                    init {
                        body = res.body!!
                        totalBytes = body.contentLength()
                    }

                    override fun contentLength(): Long = body.contentLength()

                    override fun contentType(): MediaType? = body.contentType()

                    override fun source(): BufferedSource {
                        if (bufferedSource == null) {
                            bufferedSource = object : ForwardingSource(body.source()) {
                                override fun read(sink: Buffer, byteCount: Long): Long {
                                    val bytesRead = super.read(sink, byteCount)
                                    savedBytes += if (bytesRead != -1L) bytesRead else 0
                                    if (totalBytes != 0L) {
                                        newPercentage = ((savedBytes * 100L) / totalBytes).toInt()
                                    }
                                    return bytesRead
                                }
                            }.buffer();
                        }
                        return bufferedSource!!
                    }
                }).build()
            }.build()
            val data = ByteArray(8192)
            var ins: InputStream? = null
            var fos: FileOutputStream? = null
            try {
                newCall = newClient.newCall(rawCall.request())
                val response: Response = newCall!!.execute()
                if (response.code == 200) {
                    ins = response.body!!.byteStream()
                    fos = FileOutputStream(savedFile)
                    var bytes: Int
                    while ((ins.read(data).also { bytes = it }) != -1) {
                        fos.write(data, 0, bytes)
                        if (newPercentage - currentPercentage >= progressInterval) {
                            currentPercentage = newPercentage
                            send(currentPercentage)
                        }
                    }
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
                try {
                    ins!!.close()
                    fos!!.close()
                } catch (e: Exception) {
                }
            }
        }
            .flowOn(Dispatchers.IO)
            .onCompletion {
                val isCancelled = it is CancellationException
                if (isCancelled && newCall?.isCanceled() == false && newCall!!.isExecuted()) {
                    newCall?.cancel()
                }
            }
    }

}