package com.supremebeing.phoneanti.aichat.sql.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.supremebeing.phoneanti.aichat.sql.imEntity.ChatMessage

/**
 * 聊天消息dao
 */
@Dao
interface ImMessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessage(chat: ChatMessage)

    //删除
    @Delete
    fun deleteMessage(chat: ChatMessage)

    //修改用户未读消息
    @Query("update ChatMessage set read=:read where message_id==:message_id")
    fun updateRead(message_id: String, read: Long)

    //删除所有消息
    @Query("delete from ChatMessage")
    fun deleteAllMessage()

    //删除单个聊天用户的所有消息
    @Query("delete from ChatMessage where other_user_id=:uid")
    fun deleteMessageForUid(uid:Int)

    //修改消息发送状态
    @Query("update ChatMessage set message_state=:messageStatus where message_id==:messageId")
    fun updateMessageStatus(messageId: String, messageStatus: Int)

    //修改消息翻译
    @Query("update ChatMessage set translate=:t where message_id==:messageId")
    fun updateMessageTrans(messageId: String, t: String)

    //修改所有翻译
    @Query("update ChatMessage set translate=:t")
    fun updateAllMessageTrans(t: String)

    @Query("select * from ChatMessage where message_id==:message_id order by send_time") //根据消息id查询
    fun getMessages(message_id: String): MutableList<ChatMessage>

    //分页查询
    @Query("select * from ChatMessage where other_user_id==:otherUserId and own_user_id==:ownUserId order by send_time limit 10 offset (:page-1)*10")
    fun queryMessagePage(page:Int,otherUserId: Int,ownUserId: Int): MutableList<ChatMessage>?

    @Query("select * from ChatMessage where other_user_id==:otherUserId and own_user_id==:ownUserId order by send_time")
    fun queryMessage(otherUserId: Int,ownUserId: Int): MutableList<ChatMessage>?
}