package com.supremebeing.phoneanti.aichat

import android.app.Application
import android.content.IntentFilter
import android.os.Build
import androidx.lifecycle.ProcessLifecycleOwner
import com.blankj.utilcode.util.LogUtils
import com.facebook.FacebookSdk
import com.facebook.LoggingBehavior
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.onesignal.OneSignal
import com.reyun.solar.engine.SolarEngineConfig
import com.reyun.solar.engine.SolarEngineManager
import com.supremebeing.phoneanti.aichat.socket.SocketReceiver
import com.supremebeing.phoneanti.aichat.sql.AppDBHelp
import com.supremebeing.phoneanti.aichat.tool.appKey
import com.supremebeing.phoneanti.aichat.tool.globalMsgAction
import com.supremebeing.phoneanti.aichat.tool.oneSingle_Id
import com.supremebeing.phoneanti.aichat.tool.userCode
import com.supremebeing.phoneanti.aichat.utils.ActivityUtil
import kotlin.properties.Delegates

class App : Application() {
    companion object {
        var instance : App by Delegates.notNull()
        var aiSend = false
    }

    private var chatMessageReceiver : SocketReceiver? = null

    override fun onCreate() {
        super.onCreate()
        if (isMain()) {
            instance = this
           //初始化firebase
            Firebase.initialize(this)
           //初始化facebook
//            FacebookSdk.setAutoInitEnabled(true)
//            FacebookSdk.fullyInitialize()
//            FacebookSdk.sdkInitialize(this)
//            AppEventsLogger.activateApp(this)
//            FacebookSdk.setAutoLogAppEventsEnabled(true)
//            FacebookSdk.setIsDebugEnabled(true)
//            FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS)

            //热力引擎
            SolarEngineManager.getInstance().preInit(this, appKey)
            val config = SolarEngineConfig.Builder()
//                .logEnabled() //开启本地调试日志
//                .isDebugModel(true)
                .build()
            SolarEngineManager.getInstance()
                .initialize(this, appKey,config)

            ActivityUtil.initApp(this)

            //注册消息广播
            chatMessageReceiver = SocketReceiver()
            val filter = IntentFilter(globalMsgAction)
            registerReceiver(chatMessageReceiver, filter)
            AppDBHelp.initApplication(this)
            registerController()
            initOneSingle()
        }

    }

    private fun initOneSingle(){
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)
        // OneSignal Initialization
        OneSignal.initWithContext(this)
        OneSignal.setAppId(oneSingle_Id)

        OneSignal.setNotificationWillShowInForegroundHandler { notificationReceivedEvent ->
            val data = notificationReceivedEvent.notification.additionalData
            notificationReceivedEvent.complete(null)
        }
    }

    private fun registerController() {
        MessageBar.register(ProcessLifecycleOwner.get(), this)

        chatMessageReceiver?.setOnMessageListener {
            MessageBar.initMessage()
        }

    }

    private fun isMain() : Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getProcessName() == packageName
        } else {
            val pid = android.os.Process.myPid()
            val processes =
                getSystemService(android.app.ActivityManager::class.java).runningAppProcesses
            var processName = ""
            for (p in processes) {
                if (p.pid == pid) {
                    processName = p.processName
                    break
                }
            }
            processName == packageName
        }
    }
}