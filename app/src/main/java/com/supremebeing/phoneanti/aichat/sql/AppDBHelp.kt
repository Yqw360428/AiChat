package com.supremebeing.phoneanti.aichat.sql

import android.app.Application
import com.supremebeing.phoneanti.aichat.sql.dao.ImMessageDao
import com.supremebeing.phoneanti.aichat.sql.dao.UserInfoDao
import com.supremebeing.phoneanti.aichat.sql.imEntity.ChatMessage
import com.supremebeing.phoneanti.aichat.sql.imEntity.UserInfo

/**
 * ynw
 * 数据库创建
 *
 * 数据库操作外部方法
 */
object AppDBHelp {
    private var context:Application?=null

    fun initApplication(context: Application) {
        AppDBHelp.context = context
    }

    private val messageDao: ImMessageDao
        get() {
            return IMHpRoom.getInstance(context !!).getMessageDao()
        }

    private val userDao: UserInfoDao
        get() {
            return IMHpRoom.getInstance(context !!).getUserDao()
        }

    fun closeDb(){
        IMHpRoom.getInstance(context !!).close()
    }

    fun clearDb(){
//        deleteAllMessage()
        deleteAllChat()
    }

    /**
     * 保存消息
     */
    fun saveMessage(msg: ChatMessage){
        messageDao.insertMessage(msg)
    }

    /**
     * 删除消息
     */
    fun deleteMessage(msg: ChatMessage){
        messageDao.deleteMessage(msg)
    }

    /**
     * 修改消息发送状态
     */
    fun updateMessageStatus(message_id: String, message_status: Int) {
        messageDao.updateMessageStatus(message_id, message_status)
    }

    /**
     * 修改消息
     */
    fun updateMessageTranslate(message_id: String, t: String) {

        messageDao.updateMessageTrans(message_id, t)
    }

    fun updateAllMessageTrans() {
        messageDao.updateAllMessageTrans("")
    }

    /**
     * 获取聊天室所有消息
     */
    suspend fun getChatMessages(ownUserId: Int, otherUserId: Int): MutableList<ChatMessage>? {
          return messageDao.queryMessage(otherUserId, ownUserId)
    }

    /**
     * 根据消息id获取消息
     */
    fun getChatMessageForId(messageId:String): MutableList<ChatMessage>{
        return messageDao.getMessages(messageId)
    }

    /**
     * 分页查询消息
     */
    fun getChatMessageForPage(page:Int,ownUserId: Int, otherUserId: Int): MutableList<ChatMessage>?{
        return messageDao.queryMessagePage(page,otherUserId, ownUserId)
    }

    /**
     * 消息设置为已读
     */
    fun setChatMessageRead(msgId:String,read: Long){
        messageDao.updateRead(msgId, read)
    }

    ////////////////////////用户//////////////////////////////
    /**
     * 保存用户信息
     */
    fun saveUserInfo(info: UserInfo){
        userDao.insertUser(info)
    }

    /**
     * 获取用户数据
     */
    fun getUserInfoForId(uid:Int): UserInfo {
        return userDao.getUserInfo(uid)
    }

    fun deleteAllMessage(){
        return messageDao.deleteAllMessage()
    }

    fun deleteMessageForOtherId(id:Int){
        return messageDao.deleteMessageForUid(id)
    }

    /**
     * 删除所有会话
     */
    fun deleteAllChat(){
        return userDao.deleteAll()
    }

    /**
     * 用户未读消息设置已读
     */
    fun setUserRead(uId: Int, read: Int){
        userDao.updateRead(uId,read)
    }

    /**
     * 用户封面修改
     */
    fun setUserCover(uId: Int, cover: String?){
        userDao.updateCover(uId,cover)
    }

    fun setUserIntro(uId: Int, intro: String?){
        userDao.updateIntro(uId,intro)
    }

    /**
     * 获取所有用户信息
     */
    suspend fun getUserInfo():MutableList<UserInfo>{
        return userDao.getAllUserInfo()
    }

}