package me.skean.skeanframework.utils

import android.util.Log
import com.blankj.utilcode.util.LogUtils
import java.io.*

/**
 * Log到文件的输出器
 */
class LogFileWriter(val maxFileSize: Long) : LogUtils.IFileWriter {
    override fun write(filePath: String?, content: String?) {
        var bw: BufferedWriter? = null
        try {
            val file = File(filePath)

            bw = BufferedWriter(OutputStreamWriter(FileOutputStream(filePath, true), "UTF-8"))
            bw.write(content)
        }
        catch (e: IOException) {
            e.printStackTrace()
            Log.e("LogUtils", "log to $filePath failed!")
        }
        finally {
            try {
                bw?.close()
            }
            catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun archiveFile() {
        val path = LogUtils.getConfig().dir + LogUtils.getConfig().filePrefix + "_" + ContentUtil.date(System.currentTimeMillis()) + "_" + LogUtils.getConfig().processName + LogUtils.getConfig().fileExtension
    }
}