package com.supremebeing.phoneanti.aichat.event

import com.supremebeing.phoneanti.aichat.bean.NotifyBean
import com.supremebeing.phoneanti.aichat.bean.SignBean


/**
 *@ClassName: RxEvents
 *@Description: event事件
 *@Author: Yqw
 *@Date: 2022/12/3 21:28
**/
object RxEvents {
    //尺度改变
    class ShowNsfw(val nsfw : Int)
    //绑定socket
    class BindSocket(val clientId : String)
    //登陆成功
    class LoginSuccess(val login : String)
    //用户权益更新
    class UserRights
    //获取历史消息
    class HistoryMessage(val history : Int)
    //刷新消息
    class RefreshMessage(val refresh : String)
    //刷新消息列表
    class RefreshMessageList(val refresh : String)
    //刷新所有权益
    class RefreshAllRights(val refresh : String)
    //余额变动
    class BalanceUpdate
    //用户信息更新
    class UserUpdate
    //全局签到弹窗
    class GlobalSign(val signBean : SignBean)
    //签到
    class DailySign(val sign : Boolean)
    //未登录
    class NoLogin
    //签到获得钻石
    class SignReward(val coin : Int)
    //消息通知
    class ShowNotify(val notifyBean : NotifyBean)
    //显示消息红点
    class ShowDot
}