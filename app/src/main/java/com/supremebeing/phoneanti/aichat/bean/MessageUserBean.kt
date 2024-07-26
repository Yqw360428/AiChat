package com.supremebeing.phoneanti.aichat.bean

data class MessageUserBean(
    var startTime : Long? = 0,
    val data : MessageUsers ? = null
) : BaseBean(){
    override fun toString() : String {
        return "MessageUserBean(startTime=$startTime, data=$data)"
    }
}
