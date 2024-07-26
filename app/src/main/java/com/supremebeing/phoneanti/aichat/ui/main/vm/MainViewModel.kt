package com.supremebeing.phoneanti.aichat.ui.main.vm

import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.LogUtils
import com.supremebeing.phoneanti.aichat.base.ArchViewModel
import com.supremebeing.phoneanti.aichat.bean.AnchorBean
import com.supremebeing.phoneanti.aichat.bean.BaseBean
import com.supremebeing.phoneanti.aichat.bean.DailyBean
import com.supremebeing.phoneanti.aichat.bean.IniBean
import com.supremebeing.phoneanti.aichat.bean.LikeBean
import com.supremebeing.phoneanti.aichat.bean.RechargeBean
import com.supremebeing.phoneanti.aichat.bean.RightApplyBean
import com.supremebeing.phoneanti.aichat.bean.SignBean
import com.supremebeing.phoneanti.aichat.event.RxBus
import com.supremebeing.phoneanti.aichat.event.RxEvents
import com.supremebeing.phoneanti.aichat.network.client.RetrofitClient
import com.supremebeing.phoneanti.aichat.network.server.HttpCode
import com.supremebeing.phoneanti.aichat.utils.SolarUtil
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

class MainViewModel : ArchViewModel() {
    val anchorData = MutableLiveData<AnchorBean>()
    val anchor1Data = MutableLiveData<AnchorBean>()
    val anchorRecommendData = MutableLiveData<AnchorBean>()
    val initData = MutableLiveData<IniBean>()
    val bindData = MutableLiveData<BaseBean>()
    val signData = MutableLiveData<SignBean>()
    val dailySignData = MutableLiveData<DailyBean>()
    val signOutData = MutableLiveData<BaseBean>()
    val deleteAccountData = MutableLiveData<BaseBean>()
    var userData = MutableLiveData<BaseBean>()
    val rightApplyData = MutableLiveData<RightApplyBean>()
    val rechargeData = MutableLiveData<RechargeBean>()
    val allAnchorData = MutableLiveData<AnchorBean>()

    /**
     * 所有ai数据
     */
    fun requestAllAiData(nsfw : Int, page : Int) {
        rxHttpSubscribe(RetrofitClient.serviceApi.aiList(
            null,
            null,
            if (nsfw == 0) null else nsfw,
            10,
            page
        ),
            {
                allAnchorData.postValue(it)
            },
            {
                allAnchorData.postValue(getErrorBean(it, AnchorBean::class.java))
            }
        )
    }

    /**
     * ai列表数据(女性)
     */
    fun requestAiData(gender : String? = null, type : String? = null, nsfw : Int, page : Int) {
        rxHttpSubscribe(
            RetrofitClient.serviceApi.aiList(gender, type, if (nsfw == 0) null else nsfw, 10, page),
            {
                anchorData.postValue(it)
            },
            {
                anchorData.postValue(getErrorBean(it, AnchorBean::class.java))
            }
        )
    }

    /**
     * ai列表数据(男性)
     */
    fun requestAiData1(gender : String? = null, type : String? = null, nsfw : Int, page : Int) {
        rxHttpSubscribe(
            RetrofitClient.serviceApi.aiList(gender, type, if (nsfw == 0) null else nsfw, 10, page),
            {
                anchor1Data.postValue(it)
            },
            {
                anchor1Data.postValue(getErrorBean(it, AnchorBean::class.java))
            }
        )
    }

    /**
     * ai推荐列表数据
     */
    fun requestRecommendAiData(
        gender : String? = null,
        type : String? = null,
        nsfw : Int,
        page : Int
    ) {
        rxHttpSubscribe(
            RetrofitClient.serviceApi.aiList(gender, type, if (nsfw == 0) null else nsfw, 10, page),
            {
                anchorRecommendData.postValue(it)
            },
            {
                anchorRecommendData.postValue(getErrorBean(it, AnchorBean::class.java))
            }
        )
    }

    /**
     * 获取配置信息
     */
    fun init() {
        rxHttpSubscribe(RetrofitClient.serviceApi.initData(),
            {
                initData.postValue(it)
            },
            {
                initData.postValue(getErrorBean(it, IniBean::class.java))
            })
    }

    /**
     * 绑定socket
     */
    fun bindSocket(client_id : String, userId : String = "", pushToken : String = "") {
        val json = JSONObject()
        json.put("client_id", client_id)
        json.put("onesignal_id", userId)
        json.put("push_token", pushToken)
        val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        rxHttpSubscribe(RetrofitClient.serviceApi.bindData(requestBody),
            {
                bindData.postValue(it)
            },
            {
                bindData.postValue(getErrorBean(it, BaseBean::class.java))
            })
    }

    /**
     * 签到数据
     */
    fun getSign() {
        rxHttpSubscribe(RetrofitClient.serviceApi.signList(),
            {
                signData.postValue(it)
            },
            {
                signData.postValue(getErrorBean(it, SignBean::class.java))
            })
    }

    /**
     * 签到
     */
    fun dailyCheck(double : Boolean) {
        val json = JSONObject()
        json.put("double", double)
        val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        rxHttpSubscribe(RetrofitClient.serviceApi.dailySign(requestBody),
            {
                dailySignData.postValue(it)
            },
            {
                dailySignData.postValue(getErrorBean(it, DailyBean::class.java))
            })
    }

    /**
     * 退出登录
     */
    fun signOut() {
        rxHttpSubscribe(RetrofitClient.serviceApi.signOut(),
            {
                signOutData.postValue(it)
            },
            {
                signOutData.postValue(getErrorBean(it, BaseBean::class.java))
            }
        )
    }

    /**
     * 删除账号
     */
    fun deleteAccount() {
        rxHttpSubscribe(RetrofitClient.serviceApi.deleteAccount(),
            {
                deleteAccountData.postValue(it)
            },
            {
                deleteAccountData.postValue(getErrorBean(it, BaseBean::class.java))
            }
        )
    }

    /**
     * 更新用户信息
     */
    fun updateUser(
        name : String? = null,
        age : String? = null,
        headPath : String? = null,
        mood : String? = null,
        height : String? = null,
        weight : String? = null,
        bodyType : String? = null,
        belong : String? = null,
        hairColor : String? = null,
        constellation : String? = null
    ) {
        val json = JSONObject()
        val jsonArray = JSONArray()
        val jsonAttrs = JSONObject()
        if (! name.isNullOrBlank()) {
            json.put("nickname", name)
        }

        if (age.isNullOrBlank()) {
            json.put("age", 18)
        } else {
            if (age.toInt() > 18) {
                json.put("age", age.toInt())
            } else {
                json.put("age", 18)
            }
        }

        if (! headPath.isNullOrBlank()) {
            json.put("head", headPath)
            jsonArray.put(headPath)
            json.put("photos", jsonArray)
        }

        if (! mood.isNullOrBlank()) {
            json.put("mood", mood)
        }

        if (! height.isNullOrBlank()) {
            jsonAttrs.put("height", height)
        }

        if (! weight.isNullOrBlank()) {
            jsonAttrs.put("weight", weight)
        }

        if (! bodyType.isNullOrBlank()) {
            jsonAttrs.put("body_type", bodyType)
        }

        if (! belong.isNullOrBlank()) {
            jsonAttrs.put("belong", belong)
        }

        if (! hairColor.isNullOrBlank()) {
            jsonAttrs.put("hair_color", hairColor)
        }

        if (! constellation.isNullOrBlank()) {
            jsonAttrs.put("constellation", constellation)
        }

        json.put("attrs", jsonAttrs)
        val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        rxHttpSubscribe(RetrofitClient.serviceApi.updateUserData(requestBody),
            {
                userData.postValue(it)
            },
            {
                userData.postValue(getErrorBean(it, BaseBean::class.java))
            }
        )
    }

    /**
     * 看广告领金币
     */
    fun getReward(isDouble : Boolean = false) {
        val json = JSONObject()
        if (isDouble) {
            json.put("id", "")
            json.put("double", true)
        } else {
            json.put("code", "watch_video_draw_token")
        }
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
     * 充值
     */
    fun recharge(
        json : JSONObject,
        gid : Int,
        code : String,
        saveEvent : Boolean = false,
        isRepeatOrder : Boolean = false,
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
}