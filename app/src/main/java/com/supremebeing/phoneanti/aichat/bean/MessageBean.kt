package com.supremebeing.phoneanti.aichat.bean

data class MessageBean(
    val data : Message
) : BaseBean(){
    data class Message(
        var msg_id : String,
        val sent_at : Int = 0,
        var oldMessageId : String? = null,
        val messageStatus : Int = 0
    ){
        override fun toString() : String {
            return "Message(msg_id='$msg_id', sent_at=$sent_at, oldMessageId=$oldMessageId, messageStatus=$messageStatus)"
        }
    }
}
