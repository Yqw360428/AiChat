package com.supremebeing.phoneanti.aichat.socket

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.blankj.utilcode.util.LogUtils
import com.google.gson.Gson
import com.supremebeing.phoneanti.aichat.bean.BalanceUpdateBean
import com.supremebeing.phoneanti.aichat.bean.ConnectSocketBean
import com.supremebeing.phoneanti.aichat.bean.SocketMessage
import com.supremebeing.phoneanti.aichat.event.RxBus
import com.supremebeing.phoneanti.aichat.event.RxEvents
import com.supremebeing.phoneanti.aichat.network.server.MessageCode
import com.supremebeing.phoneanti.aichat.network.server.MessageStatus
import com.supremebeing.phoneanti.aichat.sql.AppDBHelp
import com.supremebeing.phoneanti.aichat.sql.imEntity.ChatMessage
import com.supremebeing.phoneanti.aichat.tool.AIP
import com.supremebeing.phoneanti.aichat.tool.globalMsgAction
import org.json.JSONObject

class SocketReceiver : BroadcastReceiver(){

    //监听
    private var messageListener: (() -> Unit)? = null

    fun setOnMessageListener(listener: (() -> Unit)) {
        this.messageListener = listener
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if(globalMsgAction == action){
            val message = intent.getStringExtra("message")
            LogUtils.i("==============ChatMessageReceiver:$message")
            if (message!=null){
                val jsonObject = JSONObject(message)
                val event_code = jsonObject.optInt("event_code")
                when(event_code){
                    SocketCode.SUCCESS.code -> { //连接成功
                        val connectSocketBean = Gson().fromJson(message,ConnectSocketBean::class.java)
                        RxBus.post(RxEvents.BindSocket(connectSocketBean.data.client_id))
                    }

                    SocketCode.NEW_MESSAGE_AI.code -> {  //ai主动打招呼
                        messageListener?.invoke()
                    }

                    SocketCode.NEW_MESSAGE.code -> { //有消息来了
                        messageListener?.invoke()
                        val messages = Gson().fromJson(message, SocketMessage::class.java)
//                        APreferences.setMessageTime(connectSocketBean.data.sent_at)
//                        APreferences.setMessageEndTime(connectSocketBean.data.updated_at)
                        val message = ChatMessage(
                            messages.data.msg_id,
                            if (messages.data.sender_id == AIP.userId
                            ) messages.data.sender_id else messages.data.to,
                            if (messages.data.sender_id == AIP.userId
                            ) messages.data.to else messages.data.sender_id,
                            messages.data.content.text,"",
                            System.currentTimeMillis() / 1000,
                            messages.data.content.type,
                            0L,
                            if (messages.data.sender_id == AIP.userId
                            ) 2 else 1,
                            "",
                            messages.data.sent_at,
                            MessageStatus.SUCCESS.code,
                            "",
                            0L,
                            1,
                            0,
                            ""
                            ,AIP.head,
                            messages.data.content.is_ended
                        )
                        when (message.message_type) {
                            MessageCode.TEXTMESSAGE.code -> {//文本消息
                                AppDBHelp.saveMessage(message)
                            }
                        }
                        RxBus.post(RxEvents.RefreshMessage("1"))
                    }

                    SocketCode.UPDATE_ACCOUNT.code -> {//余额变动
                        val gson = Gson()
                        val balanceUpdateBean = gson.fromJson(
                            message,
                            BalanceUpdateBean::class.java
                        )

                        if (balanceUpdateBean!=null){
                            if (balanceUpdateBean.data!=null){
                                AIP.balance = balanceUpdateBean.data.balance.toString()
                            }
                        }
                        RxBus.post(RxEvents.BalanceUpdate::class)
                    }
                    SocketCode.REFRESH_USERINFO.code -> {//更新用户信息
                        RxBus.post(RxEvents.UserUpdate::class)
                    }

                    SocketCode.RIGHTS.code -> {//更新用户权益
                        RxBus.post(RxEvents.UserRights())
                    }
                }
            }

        }

    }


}