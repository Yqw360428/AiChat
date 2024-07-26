package com.supremebeing.phoneanti.aichat.bean

data class ReceiveMessageBean(
    val data: ReceiveMessage?,
    var currentTime: Long =0L,
    var startTime: Long = 0L
) : BaseBean(){

}