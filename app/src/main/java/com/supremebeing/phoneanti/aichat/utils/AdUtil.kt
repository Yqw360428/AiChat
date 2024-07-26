package com.supremebeing.phoneanti.aichat.utils

import android.app.Activity
import android.content.Context
import android.text.format.DateUtils
import com.blankj.utilcode.util.LogUtils
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.supremebeing.phoneanti.aichat.App
import com.supremebeing.phoneanti.aichat.R
import com.supremebeing.phoneanti.aichat.tool.AIP
import com.supremebeing.phoneanti.aichat.tool.admobId
import com.supremebeing.phoneanti.aichat.tool.toastShort

object AdUtil {
    class AdBean(var rewardedAd : RewardedAd? = null,var time : Long = 0L)
    var errorString : String? = null
    private var adBean : AdBean? = null

    fun loadAd(context : Context,loaded : ()->Unit){
        if (adBean != null){
            if (adBean!!.rewardedAd != null){
                loaded.invoke()
                return
            }
        }
//        if (checkAdExpired()) return
        RewardedAd.load(context, admobId, AdRequest.Builder().build(),object : RewardedAdLoadCallback(){
            override fun onAdLoaded(ad : RewardedAd) {
                adBean = AdBean(ad,System.currentTimeMillis())
                loaded.invoke()
            }

            override fun onAdFailedToLoad(error : LoadAdError) {
                errorString = error.message
                adBean = AdBean(null)
                LogUtils.e("=====>广告加载失败:$errorString")
            }
        })
    }

    fun showAd(activity : Activity,earnReward : ()-> Unit){
        if (adBean != null){
            if (adBean!!.rewardedAd != null){
                adBean!!.rewardedAd?.show(activity){
                    AIP.adCount++
                    AIP.lastAdTime = System.currentTimeMillis()
                    earnReward.invoke()
                    adBean = null
                    if (AIP.showAd && AIP.adCount < AIP.maxAdCount){
                        loadAd(App.instance){}
                    }
                }
                adBean!!.rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback(){
                    override fun onAdImpression() {
                        super.onAdImpression()
                        SolarUtil.showRewardAd()
                    }

                    override fun onAdClicked() {
                        super.onAdClicked()
                        SolarUtil.clickAd()
                    }
                }
            }else{
                activity.getString(R.string.ad_prepared).toastShort
            }
        }else{
            activity.getString(R.string.ad_prepared).toastShort
        }
    }

//    private fun checkAdExpired() : Boolean{
//        adBean?.let {
//            it.rewardedAd?.let {_ ->
//                if (System.currentTimeMillis() - it.time>= DateUtils.HOUR_IN_MILLIS){
//                    it.rewardedAd = null
//                    it.time = 0L
//                    return false
//                }else{
//                    return true
//                }
//            }
//        }
//        return false
//    }
}