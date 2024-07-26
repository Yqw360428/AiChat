package com.supremebeing.phoneanti.aichat.tool

import android.content.Context
import com.supremebeing.phoneanti.aichat.App

/**
 *@ClassName: ShareHelper
 *@Description: SharePreference数据存储
 *@Author: Yqw
 *@Date: 2023/1/4 18:03
 **/
object AIP {
    private val sharedPreferences = App.instance.getSharedPreferences(
        "dbName",
        Context.MODE_PRIVATE
    )

    /**
     *@MethodName: set
     *@Description: 存储数据
     *@Author: Yqw
     *@Date: 2022/12/4 19:31
     **/
    fun set(key: String, `object`: Any) {
        when (`object`) {
            is String -> {
                sharedPreferences.edit().putString(key, `object`).apply()
            }

            is Int -> {
                sharedPreferences.edit().putInt(key, `object`).apply()
            }

            is Long -> {
                sharedPreferences.edit().putLong(key, `object`).apply()
            }

            is Float -> {
                sharedPreferences.edit().putFloat(key, `object`).apply()
            }

            is Boolean -> {
                sharedPreferences.edit().putBoolean(key, `object`).apply()
            }
        }
    }

    /**
     *@MethodName: get
     *@Description: 获取数据
     *@Author: Yqw
     *@Date: 2022/12/4 19:31
     **/
    fun get(key: String, defaultObject: Any): Any? {
        return when (defaultObject) {
            is String -> {
                sharedPreferences.getString(key, defaultObject) as String
            }

            is Int -> {
                sharedPreferences.getInt(key, defaultObject)
            }

            is Boolean -> {
                sharedPreferences.getBoolean(key, defaultObject)
            }

            is Float -> {
                sharedPreferences.getFloat(key, defaultObject)
            }

            is Long -> {
                sharedPreferences.getLong(key, defaultObject)
            }

            else -> {
                sharedPreferences.getString(key, null)
            }
        }
    }

    fun clearData(){
        sharedPreferences.edit().clear().apply()
    }

    private fun getString(key: String) : String{
        return get(key,"") as String
    }

    private fun getInt(key: String) : Int{
        return get(key,0) as Int
    }

    private fun getBoolean(key: String) : Boolean{
        return get(key,false) as Boolean
    }

    private fun getFloat(key: String) : Float{
        return get(key,0f) as Float
    }

    private fun getLong(key: String) : Long{
        return get(key,0L) as Long
    }

    var isLogin
        get() = getBoolean("isLogin")
        set(value) = set("isLogin",value)

    var show
        get() = getInt("show")
        set(value) = set("show",value)

    var click
        get() = getInt("click")
        set(value) = set("click",value)

    var adsTime
        get() = getString("adsTime")
        set(value) = set("adsTime",value)

    /**
     * 用户token
     */
    var token
        get() = getString("token")
        set(value) = set("token",value)

    /**
     * Google id
     */
    var googleId
        get() = getString("googleId")
        set(value) = set("googleId",value)

    /**
     * 用户id
     */
    var userId
        get() = getInt("userId")
        set(value) = set("userId",value)

    /**
     * 设置角色
     */
    var role
        get() = getInt("role")
        set(value) = set("role",value)

    /**
     * 账户余额
     */
    var balance
        get() = getString("balance")
        set(value) = set("balance",value)

    /**
     * 用户昵称
     */
    var userName
        get() = getString("userName")
        set(value) = set("userName",value)

    /**
     * 用户邮箱
     */
    var userEmail
        get() = getString("userEmail")
        set(value) = set("userEmail",value)

    /**
     * 用户性别
     */
    var gender
        get() = getInt("gender")
        set(value) = set("gender",value)

    /**
     * 用户头像
     */
    var head
        get() = getString("head")
        set(value) = set("head",value)

    /**
     * 用户年龄
     */
    var age
        get() = getString("age")
        set(value) = set("age",value)

    /**
     * 第一次滑动
     */
    var firstWipe
        get() = getBoolean("firstWipe")
        set(value) = set("firstWipe",value)

    /**
     * 联系链接
     */
    var contactUrl
        get() = getString("contactUrl")
        set(value) = set("contactUrl",value)

    /**
     * 记录获取历史消息的时间
     */
    var messageEndTime
        get() = getLong("messageEndTime")
        set(value) = set("messageEndTime",value)

    /**
     * 记录获取最新消息的时间
     */
    var messageTime
        get() = getLong("messageTime")
        set(value) = set("messageTime",value)

    /**
     * 机器人名字
     */
    var aiName
        get() = getString("aiName")
        set(value) = set("aiName",value)

    /**
     * 保存的Url
     */
    var apiUrl
        get() = getString("apiUrl")
        set(value) = set("apiUrl",value)

    /**
     * socketUrl
     */
    var socketUrl
        get() = getString("socketUrl")
        set(value) = set("socketUrl",value)

    /**
     * 是否是正式服
     */
    var isRelease
        get() = getBoolean("isRelease")
        set(value) = set("isRelease",value)

    /**
     * 充值小票
     */
    var rechargeTicket
        get() = getString("rechargeTicket")
        set(value) = set("rechargeTicket",value)

    /**
     * 观看广告是否显示
     */
    var showAd
        get() = getBoolean("showAd")
        set(value) = set("showAd",value)

    /**
     * 观看广告最大次数
     */
    var maxAdCount
        get() = getInt("maxAdCount")
        set(value) = set("maxAdCount",value)

    /**
     * 已经观看次数
     */
    var adCount
        get() = getInt("adCount")
        set(value) = set("adCount",value)

    /**
     * 广告时间间隔
     */
    var adInternal
        get() = getInt("adInternal")
        set(value) = set("adInternal",value)

    /**
     * 上次看广告时间
     */
    var lastAdTime
        get() = getLong("lastAdTime")
        set(value) = set("lastAdTime",value)

    /**
     * 手动关闭翻译
     */
    var closeTrans
        get() = getBoolean("closeTrans")
        set(value) = set("closeTrans",value)

    /**
     * 快速登录
     */
    var fastLogin
        get() = getBoolean("fastLogin")
        set(value) = set("fastLogin",value)

    /**
     * 应用弹窗上次弹出时间
     */
    var lastAppShow
        get() = getLong("lastAppShow")
        set(value) = set("lastAppShow",value)

    /**
     * 是否评价过
     */
    var rated
        get() = getBoolean("rated")
        set(value) = set("rated",value)

    /**
     * google登录或者邮箱登录
     * true google登录 false 邮箱登录
     */
    var isGoogle
        get() = getBoolean("isGoogle")
        set(value) = set("isGoogle",value)

    /**
     * 聊天页面aiId
     */
    var aiId
        get() = getInt("aiId")
        set(value) = set("aiId",value)

    /**
     * 本机语言
     */
    var localCode
        get() = getString("localCode")
        set(value) = set("localCode",value)

}