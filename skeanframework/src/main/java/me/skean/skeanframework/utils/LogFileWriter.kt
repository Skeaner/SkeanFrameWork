package me.skean.skeanframework.utils

import android.util.Log
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.LogUtils
import java.io.*

/**
 * Log到文件的输出器
 */
class LogFileWriter(val maxFileSize: Long) : LogUtils.IFileWriter {

    private var deviceId: String = DeviceUtils.getAndroidID()

    override fun write(filePath: String?, content: String?) {
        var bw: BufferedWriter? = null
        try {
            val file = File(filePath)
            if (file.length() >= maxFileSize) {
                archiveFile()
            }
            FileUtils.createOrExistsFile(file)
            bw = BufferedWriter(OutputStreamWriter(FileOutputStream(filePath, true), "UTF-8"))
            bw.write(content)
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("LogUtils", "log to $filePath failed!")
        } finally {
            try {
                bw?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun archiveFile() {
        val timeTag = ContentUtil.dateTimeUnderlineSepNow()
        val files = FileUtils.listFilesInDir(LogUtils.getConfig().dir)
        for (file in files) {
            if (file.name.startsWith("new")) {
                val newName = file.name.replace("new", "device_${deviceId}_archive_${timeTag}_log")
                FileUtils.rename(file, newName)
            }
        }
    }
}