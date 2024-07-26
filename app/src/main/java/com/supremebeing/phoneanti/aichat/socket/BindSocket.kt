package com.supremebeing.phoneanti.aichat.socket

import android.content.ComponentName
import android.content.Context
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log

object BindSocket {
    private var client: SocketClient? = null
    private var binder: WebSocketClientService.JWebSocketClientBinder? = null
    private var webSClientService: WebSocketClientService? = null
    private var isBound = false

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            Log.e("MainActivity", "服务与活动成功绑定")
            binder = iBinder as WebSocketClientService.JWebSocketClientBinder
            webSClientService = binder !!.service
            client = webSClientService !!.client
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            Log.e("MainActivity", "服务与活动成功断开")
        }

    }


    fun initSocket(context: Context){
        //启动服务
        startJWebSClientService(context)
        //绑定服务
        bindService(context)

    }


    fun sendMsg(message: String){
        if (webSClientService !=null){
            webSClientService?.sendMsg(message)
        }
    }


    /**
     * 绑定服务
     */
    private fun bindService(context: Context) {
        val bindIntent = Intent(
                context,
                WebSocketClientService::class.java
        )
        isBound = context.bindService(bindIntent, serviceConnection, BIND_AUTO_CREATE)
    }

    /**
     * 取消绑定
     */
    fun unBindService(context: Context) {
        webSClientService?.onDestroy()
        if (isBound){
            context.unbindService(serviceConnection)
        }

//        context.stopService(bindIntent)
    }

    /**
     * 启动服务（websocket客户端服务）
     */
    private fun startJWebSClientService(context: Context) {
        val intent = Intent(context, WebSocketClientService::class.java)
        context.startService(intent)
    }



}