@file:JvmName("IntentKey")

package me.skean.framework.example.constant

import me.skean.framework.example.BuildConfig

/**
 * 一些在Intent中使用的Key
 */
const val AUTHORITY = BuildConfig.APPLICATION_ID
const val ACTION_PREFIX = AUTHORITY + ".actions."
const val EXTRA_PREFIX = AUTHORITY + ".extras."

///////////////////////////////////////////////////////////////////////////
// 下载更新
///////////////////////////////////////////////////////////////////////////
const val ACTION_DOWNLOAD_APP = ACTION_PREFIX + "ACTION_DOWNLOAD_APP"
const val EXTRA_DOWNLOAD_URL = EXTRA_PREFIX + "EXTRA_DOWNLOAD_URL"