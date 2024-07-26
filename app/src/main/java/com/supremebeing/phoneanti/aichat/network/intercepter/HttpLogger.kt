package com.supremebeing.phoneanti.aichat.network.intercepter

import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.LogUtils
import com.supremebeing.phoneanti.aichat.BuildConfig
import okhttp3.logging.HttpLoggingInterceptor

/**
 *   Name: HttpLogger
 *   CreateBy: Dww On 2021/8/5
 *   Copyright:
 *   Language: Kotlin
 *   Description: Http日志
 */
class HttpLogger : HttpLoggingInterceptor.Logger {

    private val messageBuffer = StringBuffer()

    override fun log(message: String) {
        if (BuildConfig.DEBUG) { //debug模式启用
            if (message.startsWith("--> POST") ||
                message.startsWith("--> GET") ||
                message.startsWith("--> PUT") ||
                message.startsWith("--> DELETE")) {
                messageBuffer.setLength(0)
                messageBuffer.append(message)
            }

            messageBuffer.append(message)

//            if ((message.startsWith("{") && (message.endsWith("}"))) ||
//                (message.startsWith("[") && message.endsWith("]"))) {
//                messageBuilder.append(JSON.toJSONString(JSON.parseObject(message), true))
//            }
            messageBuffer.append("\n")


//            LogMsg.d("=============message", message)

            if (message.startsWith("<-- END HTTP")) {
                LogUtils.d("https", messageBuffer.toString() + DeviceUtils.getUniqueDeviceId())
                messageBuffer.delete(0, messageBuffer.length)
            }
        }
    }
}