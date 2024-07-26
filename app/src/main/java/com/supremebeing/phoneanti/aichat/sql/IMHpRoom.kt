package com.supremebeing.phoneanti.aichat.sql

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.supremebeing.phoneanti.aichat.sql.dao.ImMessageDao
import com.supremebeing.phoneanti.aichat.sql.dao.UserInfoDao
import com.supremebeing.phoneanti.aichat.sql.imEntity.ChatMessage
import com.supremebeing.phoneanti.aichat.sql.imEntity.UserInfo

@Database(entities = [UserInfo::class, ChatMessage::class], version = 1, exportSchema = false)
abstract class IMHpRoom:RoomDatabase() {
    abstract fun getUserDao(): UserInfoDao
    abstract fun getMessageDao(): ImMessageDao

    companion object{
        @Volatile
        private var room: IMHpRoom?=null

        @JvmStatic
        fun getInstance(context: Application): IMHpRoom {
             if (room == null) {
                synchronized(IMHpRoom::class.java){
                    if (room ==null){
                        room = Room.databaseBuilder(context, IMHpRoom::class.java, "NaughtyBaby.db")
                            .allowMainThreadQueries() //允许主线程存取数据库
//                            .addMigrations(merge1To2)
//                            .fallbackToDestructiveMigration()// 如果没有匹配到Migration，则直接删除所有的表，重新创建表
                            .build()
                    }
                }
            }
            return room !!
        }

        /**
         * 数据库升级，版本1->2
         */
        private val merge1To2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE ChatMessage ADD COLUMN is_delete Boolean")
            }
        }

        /**
         * 数据库升级，版本2->3
         */
        private val merge2To3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE UserInfo ADD COLUMN intro TEXT")
            }
        }

        fun closeDb(){
            room?.close()
        }
    }
}