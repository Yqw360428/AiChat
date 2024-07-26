package com.supremebeing.phoneanti.aichat.ui.start.vm

import android.os.Build
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.LogUtils
import com.supremebeing.phoneanti.aichat.BuildConfig
import com.supremebeing.phoneanti.aichat.base.ArchViewModel
import com.supremebeing.phoneanti.aichat.bean.BaseBean
import com.supremebeing.phoneanti.aichat.bean.InitBean
import com.supremebeing.phoneanti.aichat.bean.LoginBean
import com.supremebeing.phoneanti.aichat.network.client.RetrofitClient
import com.supremebeing.phoneanti.aichat.tool.app_id
import com.supremebeing.phoneanti.aichat.tool.isDebug
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Locale
import java.util.TimeZone

class StartViewModel : ArchViewModel() {
    val loginData = MutableLiveData<LoginBean>()
    val initData = MutableLiveData<InitBean>()
    val emailData = MutableLiveData<BaseBean>()
    val emailLoginData = MutableLiveData<LoginBean>()

    fun login(
        googleId : String,
        name : String? = null,
        age : String? = null,
        gender : Int? = null
    ) {
        val googleJson = JSONObject()
        googleJson.put("id", googleId)
        val json = JSONObject()
        name?.let {
            json.put("nickname", name)
        }
        age?.let {
            json.put("age", age)
        }
        gender?.let {
            json.put("gender",gender)
        }

        json.put("third_token", googleJson)
        json.put("app_id", app_id)
        json.put("udid", DeviceUtils.getUniqueDeviceId())
        rxHttpSubscribe(
            RetrofitClient.serviceApi.login(
                json.toString().toRequestBody("application/json".toMediaTypeOrNull())
            ),
            {
                loginData.postValue(it)
            },
            {
                loginData.postValue(getErrorBean(it, LoginBean::class.java))
            }
        )
    }

    fun checkEmail(email : String) {
        rxHttpSubscribe(RetrofitClient.serviceApi.checkEmail(email),
            {
                emailData.postValue(it)
            },
            {
                emailData.postValue(getErrorBean(it, BaseBean::class.java))
            }
        )
    }

    fun emailLogin(
        email : String,
        pwd : String,
        name : String? = null,
        age : String? = null,
        gender : Int? = null
    ) {
        val json = JSONObject()
        json.put("app_id", app_id)
        json.put("udid", DeviceUtils.getUniqueDeviceId())
        json.put("email", email)
        json.put("password", md5(pwd))
        name?.let {
            json.put("nickname", name)
        }
        age?.let {
            json.put("age", age)
        }
        gender?.let {
            json.put("gender",gender)
        }

        rxHttpSubscribe(RetrofitClient.serviceApi.login(
            json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        ),
            {
                emailLoginData.postValue(it)
            },
            {
                emailLoginData.postValue(getErrorBean(it, LoginBean::class.java))
            }
        )
    }


    fun init(sim : Boolean, vpn : Boolean) {
        val localTimeZone = TimeZone.getDefault()
        val json = JSONObject()
        json.put("app_id", app_id)

        json.put("version", BuildConfig.VERSION_NAME)

        val jsonArray = JSONArray()
        val childJson = JSONObject()
        childJson.put("udid", DeviceUtils.getUniqueDeviceId())
        childJson.put("name", Build.PRODUCT)
        val child2Json = JSONObject()
        child2Json.put("language", Locale.getDefault().language)
//        child2Json.put("language", "en")
        child2Json.put("timezone", localTimeZone.id)
        childJson.put("system", child2Json)
        val child3Json = JSONObject()
        child3Json.put("vpn", vpn)
        child3Json.put("sim", sim)
        childJson.put("network", child3Json)
        jsonArray.put(childJson)
        json.put("device", childJson)

        val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        rxHttpSubscribe(RetrofitClient.serviceApi.systemData(requestBody),
            {
                initData.postValue(it)
            },
            {
                initData.postValue(getErrorBean(it, InitBean::class.java))
            })
    }

    //密码加密
    fun md5(plainText: String): String {
        var re_md5 = String()
        try {
            val md = MessageDigest.getInstance("MD5")
            md.update(plainText.toByteArray())
            val b = md.digest()
            var i: Int
            val buf = StringBuffer("")
            for (offset in b.indices) {
                i = b[offset].toInt()
                if (i < 0) i += 256
                if (i < 16) buf.append("0")
                buf.append(Integer.toHexString(i))
            }
            re_md5 = buf.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return re_md5
    }
}