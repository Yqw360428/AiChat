package com.supremebeing.phoneanti.aichat.sql.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.supremebeing.phoneanti.aichat.sql.imEntity.UserInfo

@Dao
interface UserInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) //插入用户数据，策略：覆盖旧数据
    fun insertUser(user: UserInfo)

    @Query("select * from UserInfo where userId==:user_id") //查询用户信息
    fun getUserInfo(user_id : Int): UserInfo

    @Query("select * from UserInfo order by sendTime") //查询所有用户信息
    suspend fun getAllUserInfo(): MutableList<UserInfo>

    @Query("update UserInfo set read=:read where userId==:user_id") //修改用户未读消息
    fun updateRead(user_id: Int, read: Int)

    @Query("update UserInfo set is_call=:is_call where userId==:user_id") //未接电话
    fun updateCallMiss(user_id: Int, is_call: Int)

    @Query("update UserInfo set userCover=:userCover where userId==:user_id") //修改用户封面
    fun updateCover(user_id: Int, userCover: String?)

    @Query("update UserInfo set intro=:intro where userId==:user_id") //修改简介
    fun updateIntro(user_id: Int, intro: String?)

    @Delete //删除用户数据
    fun deleteAccount(user: UserInfo)

    @Query("delete from UserInfo")
    fun deleteAll()
}