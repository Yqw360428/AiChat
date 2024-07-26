package com.supremebeing.phoneanti.aichat

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.LogUtils
import com.supremebeing.phoneanti.aichat.base.ArchViewModel
import com.supremebeing.phoneanti.aichat.bean.BabyEquityBean
import com.supremebeing.phoneanti.aichat.bean.MessageUserBean
import com.supremebeing.phoneanti.aichat.bean.NotifyBean
import com.supremebeing.phoneanti.aichat.bean.ReceiveMessageBean
import com.supremebeing.phoneanti.aichat.event.RxBus
import com.supremebeing.phoneanti.aichat.event.RxEvents
import com.supremebeing.phoneanti.aichat.network.client.RetrofitClient
import com.supremebeing.phoneanti.aichat.network.server.HttpCode
import com.supremebeing.phoneanti.aichat.network.server.MessageCode
import com.supremebeing.phoneanti.aichat.network.server.MessageStatus
import com.supremebeing.phoneanti.aichat.sql.AppDBHelp
import com.supremebeing.phoneanti.aichat.sql.imEntity.ChatMessage
import com.supremebeing.phoneanti.aichat.sql.imEntity.UserInfo
import com.supremebeing.phoneanti.aichat.tool.AIP
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject


/**
 *   Name: MainViewModel
 *   CreateBy: ynw
 *
 */
class AppViewModel : ArchViewModel() {
    val receiveMessageData = MutableLiveData<ReceiveMessageBean?>()
    val messageUserData = MutableLiveData<MessageUserBean?>()
    val rightsAndInterestsData = MutableLiveData<BabyEquityBean?>()
    val userIds = mutableListOf<Int>()

    /**
     * 根据时间限制获取消息
     */
    fun getServiceMessageForTime(
        other_id : Int?,
        start_at : Long?,
        end_at : Long?,
        limit_person : Int?,
        per_page : Int?,
        currentTime : Long
    ) {
        rxHttpSubscribe(RetrofitClient.serviceApi.receiveMessage(
            other_id,
            start_at,
            end_at,
            limit_person,
            per_page
        ),
            {
                if (start_at == null) {
                    it.startTime = 0L
                } else {
                    it.startTime = start_at
                }
                it.currentTime = currentTime
                receiveMessageData.postValue(it)
            }, {
                receiveMessageData.postValue(getErrorBean(it, ReceiveMessageBean::class.java))
            })
    }

    /**
     * 消息用户
     */
    fun messageUserData(userIds : MutableList<Int>, start_at : Long) {
        val json = JSONObject()
        val array = JSONArray()
        userIds.forEach { userId ->
            array.put(userId)
        }
        json.put("ids", array)
        val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        rxHttpSubscribe(RetrofitClient.serviceApi.messageUserData(requestBody),
            {
                it.startTime = start_at
                messageUserData.postValue(it)
            },
            {
                messageUserData.postValue(getErrorBean(it, MessageUserBean::class.java))
            })
    }

    /**
     * 获取权益详情
     */
    fun getRightData() {
        rxHttpSubscribe(RetrofitClient.serviceApi.rightsAndInterestsData(),
            {
                rightsAndInterestsData.postValue(it)
            },
            {
                rightsAndInterestsData.postValue(getErrorBean(it, BabyEquityBean::class.java))
            }
        )
    }

    fun messageReceive(receiveMessage : ReceiveMessageBean, context : Context) : MutableList<Int> {
        if (receiveMessage.data != null) {
            if (! receiveMessage.data.messages.isNullOrEmpty()) {
                var maxTime = 0L
                var oldTime = receiveMessage.data.messages !![0].sent_at
                receiveMessage.data.messages?.forEach { messageBean ->

                    if (messageBean.sent_at > maxTime) {
                        maxTime = messageBean.sent_at
                    }
                    //匹配最老的时间
                    if (oldTime > messageBean.sent_at) {
                        oldTime = messageBean.sent_at
                    }

                    if (messageBean.sender_id == AIP.userId) {
                        userIds.add(messageBean.to)
                    } else {
                        userIds.add(messageBean.sender_id)
                    }
                }
                if (receiveMessage.startTime == 0L) {
                    AIP.messageEndTime = oldTime //设置历史消息请求时间，（最老的时间）
                }

                AIP.messageTime = maxTime+1//设置下次消息请求的时间

                receiveMessage.data.messages?.forEach { messages ->
                    val message : ChatMessage
                    if (receiveMessage.startTime == 0L) {
                        message = ChatMessage(
                            messages.msg_id,
                            if (messages.sender_id == AIP.userId
                            ) messages.sender_id else messages.to,
                            if (messages.sender_id == AIP.userId
                            ) messages.to else messages.sender_id,
                            messages.content.text, "",
                            System.currentTimeMillis() / 1000,
                            messages.content.type,
                            0L,
                            if (messages.sender_id == AIP.userId
                            ) 2 else 1,
                            messages.content.url,
                            messages.sent_at,
                            MessageStatus.SUCCESS.code,
                            messages.content.media_id,
                            0L,
                            messages.content.gift_id,
                            0,
                            "",
                            AIP.head,
                            messages.content.is_ended
                        )
                    } else {
                        message = ChatMessage(
                            messages.msg_id,
                            if (messages.sender_id == AIP.userId
                            ) messages.sender_id else messages.to,
                            if (messages.sender_id == AIP.userId
                            ) messages.to else messages.sender_id,
                            messages.content.text, "",
                            messages.read_at,
                            messages.content.type,
                            0L,
                            if (messages.sender_id == AIP.userId
                            ) 2 else 1,
                            messages.content.url,
                            messages.sent_at,
                            MessageStatus.SUCCESS.code,
                            messages.content.media_id,
                            0L,
                            messages.content.gift_id,
                            0,
                            "",
                            AIP.head,
                            messages.content.is_ended
                        )
                    }

                    when (message.message_type) {
                        MessageCode.TEXTMESSAGE.code -> {//文本消息
                            AppDBHelp.saveMessage(message)
                        }
                    }
                }


                //去重
                //将List元素注入到Set
                val set : Set<Int> = HashSet(userIds)
                //清空List集合
                userIds.clear()
                //set集合的元素添加到List
                userIds.addAll(set)
            }

        }
        return userIds
    }

    fun conversationUser(messageUsers : MessageUserBean, context : Context) {
        if (! messageUsers.data?.users.isNullOrEmpty()) {
            runBlocking {
                messageUsers.data?.users?.forEach { user ->
                    var nowOtherId = 0
                    val list = mutableListOf<ChatMessage>()
                    val chatMessages = AppDBHelp.getChatMessages(AIP.userId, user.id)
                    if (chatMessages != null) {
                        list.addAll(chatMessages)
                    }
                    list.reverse()
                    var readNum = 0
                    list.forEach { message ->
                        if (message.read == 0L && message.isComing == 1) {
                            readNum ++
                        }
                        if (message.sendTime !! - messageUsers.startTime !! >= 0) {
                            nowOtherId = message.other_user_id
                        }
                    }
                    if (user.profile != null) {
                        val profile = user.profile
                        if (list.isNotEmpty()) {
                            val messageBean = list[0]
                            when (messageBean.message_type) {
                                MessageCode.TEXTMESSAGE.code -> {
                                    val userInfo = AppDBHelp.getUserInfoForId(user.id)
                                    var isCall = 1
                                    if (userInfo != null) {
                                        isCall = userInfo.is_call
                                    }
                                    AppDBHelp.saveUserInfo(
                                        UserInfo(
                                            user.id,
                                            profile.head.ifBlank { "" },
                                            profile.nickname.ifBlank { "" },
                                            messageBean.content,
                                            0,
                                            user.role,
                                            readNum,
                                            messageBean.sendTime,
                                            MessageCode.TEXTMESSAGE.code,
                                            isCall
                                        )
                                    )
                                }
                            }

                        }

                    } else {
                        if (list.isNotEmpty()) {
                            val messageBean = list[0]
                            when (messageBean.message_type) {
                                MessageCode.TEXTMESSAGE.code -> {
                                    val userInfo = AppDBHelp.getUserInfoForId(user.id)
                                    var isCall = 1
                                    if (userInfo != null) {
                                        isCall = userInfo.is_call
                                    }
                                    AppDBHelp.saveUserInfo(
                                        UserInfo(
                                            user.id,
                                            "",
                                            user.id.toString(),
                                            messageBean.content,
                                            0,
                                            user.role,
                                            readNum,
                                            messageBean.sendTime,
                                            MessageCode.TEXTMESSAGE.code,
                                            isCall
                                        )
                                    )
                                }
                            }

                        }
                    }
                    if (messageUsers.startTime != 0L && nowOtherId > 0 && readNum>0) {
                        val userInfo = AppDBHelp.getUserInfoForId(nowOtherId)
                        if (userInfo.userId == AIP.aiId) return@forEach
                        RxBus.post(
                            RxEvents.ShowNotify(
                                NotifyBean(
                                    userInfo.headImage,
                                    userInfo.nickName,
                                    userInfo.content,
                                    userInfo.userId
                                )
                            )
                        )
                    }
                }
            }
        }
    }

}