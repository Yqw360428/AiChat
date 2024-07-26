package com.supremebeing.phoneanti.aichat.utils

import com.reyun.solar.engine.SolarEngineManager
import com.supremebeing.phoneanti.aichat.tool.admobId
import org.json.JSONObject

object SolarUtil {
    //广告展示
    fun showRewardAd(){
        val admobProjectId="ca-app-pub-9047949770672731~8833065143"
        SolarEngineManager.getInstance().track("admob","admob",1,admobProjectId, admobId,5.0,"USD",true,
            JSONObject()
        )
    }

    //点击广告
    fun clickAd(){
        SolarEngineManager.getInstance().track("admob","admob",1,admobId,
            JSONObject()
        )
    }

    //订单产生
    fun orderEvent(p:Double,coinType:String){
        SolarEngineManager.getInstance().track("SamanthAiVip",p,coinType,"paypal","success", JSONObject())
    }

    //订单支付
    fun payOrder(p:Double,payStatus:Int,coinType:String = ""){
        SolarEngineManager.getInstance().track("SamanthAiVip",p,"USD","paypal","",
            "",1,payStatus,if (payStatus==1) "success" else "fail",JSONObject())
    }

    //登录
    fun login(loginType : String,status : Int){
        SolarEngineManager.getInstance().trackAppLogin(loginType,if (status == 1 ) "success" else "fail",JSONObject())
    }

    //注册
    fun register(regType : String,status : Int){
        SolarEngineManager.getInstance().trackAppRegister(regType,if (status == 1) "success" else "fail",JSONObject())
    }

}