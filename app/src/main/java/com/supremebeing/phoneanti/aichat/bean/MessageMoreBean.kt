package com.supremebeing.phoneanti.aichat.bean

data class MessageMoreBean(
    val aiId : Int,
    val cover : String?,
    val content : String?,
    val head : String?,
    val name : String?,
    var read : Int
){
    override fun toString() : String {
        return "MessageMoreBean(aiId=$aiId, cover='$cover', content='$content', head='$head')"
    }
}
