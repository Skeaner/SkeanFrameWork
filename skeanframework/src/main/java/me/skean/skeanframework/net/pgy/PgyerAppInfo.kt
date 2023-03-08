package me.skean.skeanframework.net.pgy

/**
 * Created by Skean on 2023/3/8.
 */
data class PgyerAppInfo(
    val appKey: String,
    val buildBuildVersion: String,
    val buildDescription: String,
    val buildFileKey: String,
    val buildFileSize: String,
    val buildHaveNewVersion: Boolean,
    val buildIcon: String,
    val buildKey: String,
    val buildName: String,
    val buildUpdateDescription: String,
    val buildVersion: String,
    val buildVersionNo: String,
    val downloadURL: String,
    val forceUpdateVersion: String,
    val forceUpdateVersionNo: String,
    val needForceUpdate: Boolean
)
