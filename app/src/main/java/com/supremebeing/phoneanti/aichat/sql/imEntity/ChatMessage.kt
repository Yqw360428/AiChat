package com.supremebeing.phoneanti.aichat.sql.imEntity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.supremebeing.phoneanti.aichat.network.server.HttpCode
import com.supremebeing.phoneanti.aichat.network.server.MessageCode
import com.supremebeing.phoneanti.aichat.network.server.MessageStatus

/**
 * 聊天消息表
 */
@Entity
class ChatMessage(
    @PrimaryKey(autoGenerate = false) var message_id: String,//消息id
    @ColumnInfo(name = "own_user_id") var own_user_id: Int,//消息发送者id
    @ColumnInfo(name = "other_user_id") var other_user_id: Int, //接收消息者id
    @ColumnInfo(name = "content") var content: String?,//消息文本
    @ColumnInfo(name = "translate") var translate: String?,//翻译文本
    @ColumnInfo(name = "read") var read: Long,//消息读取时间
    @ColumnInfo(name = "message_type") var message_type: Int,
    @ColumnInfo(name = "audio_time") var audio_time: Long?,//
    @ColumnInfo(name = "isComing") var isComing: Int,//是否是收到的消息 1 是 else 否
    @ColumnInfo(name = "file_path") var file_path: String?,
    @ColumnInfo(name = "send_time") var sendTime: Long?,
    @ColumnInfo(name = "message_state") var message_state: Int,
    @ColumnInfo(name = "media_id") var mediaId: String?,
    @ColumnInfo(name = "voice_time") var voiceTime: Long?,
    @ColumnInfo(name = "gift_id") var giftId: Int,
    @ColumnInfo(name = "showTime") var showTime: Int,
    @ColumnInfo(name = "other_user_head") var otherUserHead: String?,
    @ColumnInfo(name = "own_user_head") var ownUserHead: String?,
    @ColumnInfo(name="is_ended") var is_ended:Boolean=false, //消息是否结束
    @ColumnInfo(name="newMessage") var newMessage:Boolean=false, //新消息
    @ColumnInfo(name = "is_delete") var is_delete : Boolean = false //是否需要删除
){

    //是否显示时间
    fun isShowTime():Boolean{
        return showTime==1
    }
    fun isContentShow():Boolean{
        return !content.isNullOrEmpty()
    }

    fun isTransLateShow():Boolean{
        return !translate.isNullOrEmpty()
    }

    //是否是文本消息
    fun isTextMessage():Boolean{return message_type== MessageCode.TEXTMESSAGE.code}
    //是否是语音消息
    fun isVoiceMessage():Boolean{return message_type==MessageCode.VOICEMESSAGE.code}
    //是否是图片消息
    fun isImageMessage():Boolean{
        return message_type==MessageCode.PICTUREMESSAGE.code
    }

    //消息发送是否成功
    fun isSendSuccess():Boolean{
        return message_state!= HttpCode.SUCCESS.code
    }

    //消息发送中
    fun isSending():Boolean{
        return message_state== MessageStatus.SENDING.code
    }

    //消息发送失败
    fun isSendError():Boolean{
        return message_state==MessageStatus.FAILE.code
    }

    override fun toString() : String {
        return "ChatMessage(message_id='$message_id', own_user_id=$own_user_id, other_user_id=$other_user_id, content=$content, translate=$translate, read=$read, message_type=$message_type, audio_time=$audio_time, isComing=$isComing, file_path=$file_path, sendTime=$sendTime, message_state=$message_state, mediaId=$mediaId, voiceTime=$voiceTime, giftId=$giftId, showTime=$showTime, otherUserHead=$otherUserHead, ownUserHead=$ownUserHead, is_ended=$is_ended, newMessage=$newMessage)"
    }
}


/**
 * 个人信息表
 */
@Entity(tableName = "UserInfo")
data class UserInfo(
    @PrimaryKey(autoGenerate = false) var userId:Int,//id
    @ColumnInfo(name = "headImage") var headImage:String?,//头像
    @ColumnInfo(name = "nickName") var nickName:String?,//昵称
    @ColumnInfo(name = "content") var content:String?,//用户最新消息
    @ColumnInfo(name = "onLineStatus") var onLineStatus:Int,//在线状态
    @ColumnInfo(name="role") var role:Int,//角色
    @ColumnInfo(name="read") var read:Int,//用户未读消息数量
    @ColumnInfo(name = "sendTime") var sendTime:Long?,// 新消失时间
    @ColumnInfo(name = "message_type") var message_type: Int, //消息类型
    @ColumnInfo(name = "is_call") var is_call: Int,
    @ColumnInfo(name = "isFollowing") var isFollowing: Boolean=false, //是否关注
    @ColumnInfo(name = "userCover") var userCover: String?=null, //用户封面
    @ColumnInfo(name = "intro") var intro: String?=null //用户
)