package com.supremebeing.phoneanti.aichat.utils

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import com.blankj.utilcode.util.LogUtils
import com.supremebeing.phoneanti.aichat.tool.AIP
import com.supremebeing.phoneanti.aichat.ui.main.MainActivity
import com.supremebeing.phoneanti.aichat.ui.start.LoginActivity
import com.supremebeing.phoneanti.aichat.ui.start.RegisterActivity
import com.supremebeing.phoneanti.aichat.ui.start.StartActivity
import java.util.Locale

object ActivityUtil {
    var activityCount = 0
    var hotLaunch = false
    private var mainLaunch = false
    private var activityList = arrayListOf<Activity>()
    var currentActivityName : String? = null

    fun initApp(application : Application){
        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks{
            override fun onActivityCreated(activity : Activity, savedInstanceState : Bundle?) {
                activityList.add(activity)
                if (activity is MainActivity){
                    mainLaunch = true
                }
                currentActivityName = activity.localClassName
//                checkLanguage(activity)
            }

            override fun onActivityStarted(activity : Activity) {
                currentActivityName = activity.localClassName
                activityCount ++
                if (activityCount == 1){
                    if (mainLaunch){
                        hotLaunch = true
                        //热启动
                        if (activity !is StartActivity){
//                            hotLoading(activity)
                        }
                    }else{
                        //冷启动

                    }
                }
            }

            override fun onActivityResumed(activity : Activity) {
                currentActivityName = activity.localClassName
//                checkLanguage(activity)
            }

            override fun onActivityPaused(activity : Activity) {
            }

            override fun onActivityStopped(activity : Activity) {
                activityCount --
            }

            override fun onActivitySaveInstanceState(activity : Activity, outState : Bundle) {

            }

            override fun onActivityDestroyed(activity : Activity) {
                if (activity is MainActivity){
                    mainLaunch = false
                }
                if (activityList.contains(activity)){
                    activityList.remove(activity)
                }

            }

        })

        AIP.localCode = Locale.getDefault().language
    }

    private fun checkLanguage(activity : Activity){
        val code = Locale.getDefault().language
        if (AIP.localCode.isNotBlank()){
            if (AIP.localCode == code) return
            remoAllActivity()
            activity.startActivity(Intent(activity,StartActivity::class.java))
        }
        AIP.localCode = code
    }

    fun startToMain(activity: Activity) {
        if (!mainLaunch){
            if (AIP.isLogin){
                activity.startActivity(Intent(activity,MainActivity::class.java))
            }else{
                activity.startActivity(Intent(activity,LoginActivity::class.java))
            }
        }
//        activity.startActivity(Intent(activity,RegisterActivity::class.java))
        activity.finish()
    }

    private fun hotLoading(activity : Activity){
        activity.startActivity(Intent(activity, StartActivity::class.java))
    }

    fun remoAllActivity(){
        activityList.forEach {
            it.finish()
        }
    }

}