package com.supremebeing.phoneanti.aichat.socket

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.os.*
import android.util.Log
import com.blankj.utilcode.util.LogUtils
import com.supremebeing.phoneanti.aichat.tool.globalMsgAction
import com.supremebeing.phoneanti.aichat.tool.socketIP
import org.java_websocket.WebSocket
import org.java_websocket.framing.Framedata
import org.java_websocket.handshake.ServerHandshake
import java.net.URI


class WebSocketClientService : Service() {
    var client: SocketClient? = null
    private val mBinder = JWebSocketClientBinder()
    var wakeLock: PowerManager.WakeLock? = null //锁屏唤醒
    //获取电源锁，保持该服务在屏幕熄灭时仍然获取CPU时，保持运行

    @SuppressLint("InvalidWakeLockTag", "WakelockTimeout")
    private fun acquireWakeLock() {
        if (null == wakeLock) {
            val pm = this.getSystemService(POWER_SERVICE) as PowerManager
            wakeLock = pm.newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK or PowerManager.ON_AFTER_RELEASE,
                    "PostLocationService"
            )
            if (null != wakeLock) {
                wakeLock!!.acquire()
            }
        }
    }

    //用于Activity和service通讯
    inner class JWebSocketClientBinder : Binder() {
        val service: WebSocketClientService
            get() = this@WebSocketClientService
    }

    override fun onBind(intent: Intent): IBinder {
        return mBinder
    }

    override fun onCreate() {
        super.onCreate()
    }

    @SuppressLint("ObsoleteSdkInt")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //初始化websocket
        initSocketClient()
        mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE) //开启心跳检测
        Log.e("JWebSocketClientService", Build.VERSION.SDK_INT.toString())

        acquireWakeLock()

        return START_STICKY
    }

    override fun onDestroy() {
        closeConnect()
        super.onDestroy()
    }

    /**
     * 初始化websocket连接
     */
    private fun initSocketClient() {
//        val uri = URI.create(if (AppPreferences.getInstance().getSocketUrl().isNullOrBlank()) AppConfig.socketIP else AppPreferences.getInstance().getSocketUrl())
        val uri = URI.create(socketIP)  //设置消息模块WWS连接
        client = object : SocketClient(uri) {
            override fun onMessage(message: String) {
                Log.e("JWebSocketClientService", "收到的消息：$message")
                if (message.isNotBlank()){
                    val intent = Intent()
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.action = globalMsgAction
                    intent.putExtra("message", message)
                    sendBroadcast(intent)
                }
            }

            override fun onOpen(handshakedata: ServerHandshake) {
                super.onOpen(handshakedata)
                Log.e("JWebSocketClientService", "websocket连接成功")
            }


            override fun onWebsocketPing(conn: WebSocket?, f: Framedata?) {
                super.onWebsocketPing(conn, f)
//                Log.e("JWebSocketClientService", "ping" + Gson().toJson(f))
            }

            override fun onWebsocketPong(conn: WebSocket?, f: Framedata?) {
                super.onWebsocketPong(conn, f)
//                Log.e("JWebSocketClientService", "pong" + Gson().toJson(f))
            }

        }
        connect()
    }

    /**
     * 连接websocket
     */
    private fun connect() {
        object : Thread() {
            override fun run() {
                try {
                    //connectBlocking多出一个等待操作，会先连接再发送，否则未连接发送会报错
                    client!!.connectBlocking()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

    /**
     * 发送消息
     *
     * @param msg
     */
    fun sendMsg(msg: String) {
        if (null != client ) {
//            Log.e("JWebSocketClientService", "发送的消息：$msg")
            client!!.send(msg)
        }
    }

    /**
     * 断开连接
     */
    private fun closeConnect() {
        try {
            if (null != client) {
                client!!.close()
                mHandler.removeCallbacks(heartBeatRunnable)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            client = null
        }
    }


    private val mHandler = Handler()
    private val heartBeatRunnable: Runnable by lazy {
        object : Runnable {
            override fun run() {
                Log.e("JWebSocketClientService", "心跳包检测websocket连接状态" + "client:" + client)
                if (client != null) {
                    if (client!!.isClosed) {
                        reconnectWs()
                    }else{
                        try {
                            client!!.sendPing()
                            sendMsg("ping")
                        }catch (e: Exception){
                            LogUtils.i("/////JWebSocketClientService+异常:" + e.message)
                        }


                    }

                } else {
//                Log.e("JWebSocketClientService", "心跳包检测websocket连接状态2次" + "client1:" + client)
                    //如果client已为空，重新初始化连接
                    client = null
                    initSocketClient()
                }
                //每隔一定的时间，对长连接进行一次心跳检测
                mHandler.postDelayed(this, HEART_BEAT_RATE)
            }
        }
    }


    /**
     * 开启重连
     */
    private fun reconnectWs() {
        mHandler.removeCallbacks(heartBeatRunnable)
        object : Thread() {
            override fun run() {
                try {
                    Log.e("JWebSocketClientService", "开启重连")
                    if (client!=null){
                        client!!.reconnectBlocking()
                    }

                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

    companion object {
        private const val GRAY_SERVICE_ID = 1001

        //    -------------------------------------websocket心跳检测------------------------------------------------
        private const val HEART_BEAT_RATE = (10 * 1000 //每隔10秒进行一次对长连接的心跳检测
                ).toLong()

    }

}