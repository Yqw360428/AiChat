package com.supremebeing.phoneanti.aichat.network.server

/**
 * 网络请求返回码
 */
enum class MessageStatus(val code: Int) {
    SENDING(1),  //发送中
    SUCCESS(2), //发送成功
    FAILE(3) //发送发送失败
}