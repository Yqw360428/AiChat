package com.supremebeing.phoneanti.aichat.tool

import android.Manifest
import android.app.Activity
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import com.supremebeing.phoneanti.aichat.BuildConfig

fun isDebug() : Boolean {
    return BuildConfig.DEBUG
//    return false
}

val permissions_wifi = arrayOf(Manifest.permission.ACCESS_WIFI_STATE)
val permissions_storage = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)

@RequiresApi(Build.VERSION_CODES.R)
fun isCheckPermission() = !Environment.isExternalStorageManager()

/**
 * 功用：检查通知推送的开关状态
 */
fun Activity.checkPushSwitchStatus() : Boolean{
    val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(this)
    return notificationManager.areNotificationsEnabled()
}

//服务器地址
var serverIP = if (isDebug()) "https://qa-ai.live4fun.xyz/api/" else "https://a.ceiai.online/api/"
var app_id = if (isDebug()) "9eadd744f99bf016b049956933e6147d" else "9c78534d81a40c5036d2b586b9a4ec96"

//socket服务器地址
var socketIP = if (isDebug()) "wss://qa-m.live4fun.xyz" else "wss://m.ceiai.online"
val admobId=if (isDebug()) "ca-app-pub-3940256099942544/5224354917" else "ca-app-pub-9047949770672731/7346870888"

//消息收到全局广播
const val globalMsgAction = "com.ACTION_NOTIFICATION_MESSAGE"

//onesingleId
const val oneSingle_Id="eb599211-9fb1-450c-aa1f-3cf4f1d2e035"

//热力引擎
const val userCode = "9ffab0a62e29a4b9"
const val appKey = "cbaac4fdf44fd168"