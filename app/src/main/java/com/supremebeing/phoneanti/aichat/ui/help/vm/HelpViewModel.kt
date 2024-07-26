package com.supremebeing.phoneanti.aichat.ui.help.vm

import androidx.lifecycle.MutableLiveData
import com.supremebeing.phoneanti.aichat.base.ArchViewModel
import com.supremebeing.phoneanti.aichat.bean.BaseBean
import com.supremebeing.phoneanti.aichat.bean.CardBean
import com.supremebeing.phoneanti.aichat.bean.FollowingBean
import com.supremebeing.phoneanti.aichat.bean.RechargeBean
import com.supremebeing.phoneanti.aichat.bean.RightApplyBean
import com.supremebeing.phoneanti.aichat.bean.TokensBean
import com.supremebeing.phoneanti.aichat.event.RxBus
import com.supremebeing.phoneanti.aichat.event.RxEvents
import com.supremebeing.phoneanti.aichat.network.client.RetrofitClient
import com.supremebeing.phoneanti.aichat.network.server.HttpCode
import com.supremebeing.phoneanti.aichat.utils.SolarUtil
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class HelpViewModel : ArchViewModel() {
    val feedBackData = MutableLiveData<BaseBean>()
    val followingData = MutableLiveData<FollowingBean>()
    val tokensData = MutableLiveData<TokensBean>()
    val rightApplyData = MutableLiveData<RightApplyBean>()
    val rechargeData = MutableLiveData<RechargeBean>()
    val rightRechargeData = MutableLiveData<BaseBean>()
    val myCardData = MutableLiveData<CardBean>()

    /**
     * 意见反馈
     */
    fun feedBack(content : String) {
        val json = JSONObject()
        json.put("content", content)
        val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        rxHttpSubscribe(RetrofitClient.serviceApi.feedBack(requestBody),
            { feedBackData.postValue(it) },
            { feedBackData.postValue(getErrorBean(it, BaseBean::class.java)) }
        )
    }

    /**
     * 收藏列表
     */
    fun following(page : Int) {
        rxHttpSubscribe(RetrofitClient.serviceApi.followingData(10, page),
            {
                followingData.postValue(it)
            },
            {
                followingData.postValue(getErrorBean(it, FollowingBean::class.java))
            }
        )
    }

    /**
     * 充值套餐
     */
    fun tokensList() {
        rxHttpSubscribe(RetrofitClient.serviceApi.tokenList(),
            {
                tokensData.postValue(it)
            },
            {
                tokensData.postValue(getErrorBean(it, TokensBean::class.java))
            }
        )
    }

    /**
     * 充值
     */
    fun recharge(
        json : JSONObject,
        gid : Int,
        code : String,
        saveEvent : Boolean = false,
        isRepeatOrder : Boolean = false,
        priceType : String,
        price : Double
    ) {
        val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        rxHttpSubscribe(RetrofitClient.serviceApi.recharge(requestBody),
            {
                it.gid = gid
                it.saveEvent = saveEvent
                it.code = code
                it.receipt = json.toString()
                it.isRepeat = isRepeatOrder
                rechargeData.postValue(it)
            },
            {
                rechargeData.postValue(getErrorBean(it, RechargeBean::class.java))
            }
        )
    }

    /**
     * 看广告领金币
     */
    fun getReward() {
        val json = JSONObject()
        json.put("code", "watch_video_draw_token")
        val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        rxHttpSubscribe(RetrofitClient.serviceApi.getRightApply(requestBody),
            {
                rightApplyData.postValue(it)
            },
            {
                rightApplyData.postValue(getErrorBean(it, RightApplyBean::class.java))
            }
        )
    }

    /**
     * 享受权益
     */
    fun rightApply(id : Int?, code : String?) {
        val json = JSONObject()
        json.put("id", id)
        json.put("code", code)
        val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        rxHttpSubscribe(RetrofitClient.serviceApi.getRightApply(requestBody),
            {
                rightRechargeData.postValue(it)
            },
            {
                rightRechargeData.postValue(getErrorBean(it, BaseBean::class.java))
            }
        )
    }

    /**
     * 我的卡片
     */
    fun myCard(page : Int) {
        rxHttpSubscribe(RetrofitClient.serviceApi.myCard(page),
            {
                myCardData.postValue(it)
            },
            {
                myCardData.postValue(getErrorBean(it, CardBean::class.java))
            }
        )
    }
}