package com.supremebeing.phoneanti.aichat.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.supremebeing.phoneanti.aichat.R
import com.supremebeing.phoneanti.aichat.bean.NotifyBean
import com.supremebeing.phoneanti.aichat.ui.main.MainActivity

object NotifyUtil {

    fun showNotify(context : Context,bean : NotifyBean){
        Glide.with(context)
            .asBitmap()
            .load(bean.head)
            .circleCrop()
            .into(object : CustomTarget<Bitmap>(){
                override fun onResourceReady(
                    resource : Bitmap,
                    transition : Transition<in Bitmap>?
                ) {
                    val remoteViews = RemoteViews(context.packageName,R.layout.notify_item)
                    remoteViews.apply {
                        setImageViewBitmap(R.id.head,resource)
                        setTextViewText(R.id.name,bean.name)
                        setTextViewText(R.id.content,bean.content)
                    }
                    val pendingIntent = getPendingIntentActivity(context,bean.id,bean.name)
//                    remoteViews.setOnClickPendingIntent(R.id.btn, pendingIntent)
                    show(context,remoteViews,pendingIntent)
                }

                override fun onLoadCleared(placeholder : Drawable?) {}
            })
    }

    private fun show(context : Context,remoteViews : RemoteViews,pendingIntent : PendingIntent){
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 创建通知渠道（仅适用于 Android O 及以上版本）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("samanth_ai", "samanth_ai", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
        // 创建通知
        val notification = NotificationCompat.Builder(context,"samanth_ai").apply {
            setCustomContentView(remoteViews)
            setCustomBigContentView(remoteViews)
            setCustomHeadsUpContentView(remoteViews)
            setSmallIcon(context.applicationInfo.icon)
            setVibrate(null)
            setSound(null)
            setAutoCancel(true)
            setContentIntent(pendingIntent)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            priority = NotificationCompat.PRIORITY_MAX
        }.build()
        // 发送通知
        notificationManager.notify(1, notification)
    }

    fun getPendingIntentActivity(
        context : Context?,
        id : Int,
        name : String?
    ) : PendingIntent {
        return if (Build.VERSION.SDK_INT >= 31) {
            PendingIntent.getActivity(
                context,
                id,
                Intent(context, MainActivity::class.java).apply {
                    putExtras(Bundle().apply {
                        putInt("id", id)
                        putString("name",name)
                    })
                },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getActivity(
                context,
                id,
                Intent(context, MainActivity::class.java).apply {
                    putExtras(Bundle().apply {
                        putInt("id", id)
                        putString("name",name)
                    })
                },
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }

}