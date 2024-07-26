package com.supremebeing.phoneanti.aichat.sql

import com.supremebeing.phoneanti.aichat.App
import com.supremebeing.phoneanti.aichat.R
import com.supremebeing.phoneanti.aichat.network.server.MessageCode
import com.supremebeing.phoneanti.aichat.sql.imEntity.ChatMessage
import com.supremebeing.phoneanti.aichat.sql.imEntity.UserInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

/**
 * 获取聊天室所有消息
 */
fun getAllMessage(ownUserId: Int, otherUserId: Int): MutableList<ChatMessage> {
    val list = mutableListOf<ChatMessage>()
    runBlocking {
        val chatMessages = AppDBHelp.getChatMessages(ownUserId, otherUserId)
        if (chatMessages != null) {
            list.addAll(chatMessages)
        }
    }
    return list
}
suspend fun getAllMessageFirst(ownUserId: Int, otherUserId: Int): Deferred<MutableList<ChatMessage>> {
    return CoroutineScope(Dispatchers.IO).async {
        val list = mutableListOf<ChatMessage>()
        val chatMessages = AppDBHelp.getChatMessages(ownUserId, otherUserId)
        if (chatMessages != null) {
            list.addAll(chatMessages)
        }
        list
    }
}


/**
 * 保存消息
 */
fun saveMessage(messageBean: ChatMessage) {
    AppDBHelp.saveMessage(messageBean)
}

/**
 * 保存用户消息
 */
fun saveUserMessage(msg: UserInfo) {
    AppDBHelp.saveUserInfo(msg)
}

/**
 * 删除消息
 */
fun deleteMessage(messageBean: ChatMessage) {
    AppDBHelp.deleteMessage(messageBean)
}

fun deleteMessageOfOtherId(id:Int){
    AppDBHelp.deleteMessageForOtherId(id)
}

/**
 * 修改消息发送状态
 */
fun updateMessageStatus(messageId: String, messageStatus: Int) {
    AppDBHelp.updateMessageStatus(messageId, messageStatus)
}

/**
 * 消息表未读消息改为已读
 */
fun setReadMessage(message_id: String, read: Long) {
    AppDBHelp.setChatMessageRead(message_id, read)
}

/**
 * 用户表未读消息改为已读
 */
fun setUserReadMessage(id: Int, read: Int) {
    AppDBHelp.setUserRead(id, read)
}

/**
 * 保存用户信息及历史记录列表
 */
fun saveUser(
    messageBean: ChatMessage,
    userUrl: String,
    otherUserId: Int,
    nickName: String,
    role: Int
) {
    when (messageBean.message_type) {
        MessageCode.TEXTMESSAGE.code -> {
            AppDBHelp.saveUserInfo(
                UserInfo(
                    otherUserId,
                    userUrl,
                    nickName,
                    messageBean.content,
                    0,
                    role,
                    0,
                    messageBean.sendTime,
                    0,
                    0
                )
            )
        }

        MessageCode.PICTUREMESSAGE.code -> {
            AppDBHelp.saveUserInfo(
                UserInfo(
                    otherUserId,
                    userUrl,
                    nickName,
                    App.instance.getString(R.string.image_msg),
                    0,
                    role,
                    0,
                    messageBean.sendTime,
                    0,
                    0
                )
            )
        }

        MessageCode.VOICEMESSAGE.code -> {
            AppDBHelp.saveUserInfo(
                UserInfo(
                    otherUserId,
                    userUrl,
                    nickName,
                    App.instance.getString(R.string.voice_msg),
                    0,
                    role,
                    0,
                    messageBean.sendTime,
                    0,
                    0
                )
            )
        }

        MessageCode.GIFTEMESSAGE.code -> {
            AppDBHelp.saveUserInfo(
                UserInfo(
                    otherUserId,
                    userUrl,
                    nickName,
                    App.instance.getString(R.string.gift_msg_),
                    0,
                    role,
                    0,
                    messageBean.sendTime,
                    0,
                    0
                )
            )
        }
    }
}