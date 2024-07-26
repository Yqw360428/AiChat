package com.supremebeing.phoneanti.aichat.network.server

enum class MessageCode(val code: Int) {
    PERSONAL(1),  //接收者是用户
    GROUP(2), //接收者是群主
    TEXTMESSAGE(101),//文本消息
    PICTUREMESSAGE(201),//图片消息
    VOICEMESSAGE(202),//语音消息
    VIDEOEMESSAGE(203),//视频消息
    GIFTEMESSAGE(301),//礼物消息
    CALLESSAGE(501),//未接来电消息
}