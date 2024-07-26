package com.supremebeing.phoneanti.aichat.bean

import com.supremebeing.phoneanti.aichat.sql.imEntity.ChatMessage

data class TranslateBean(val data : Msg,var position : Int,var messageBean : ChatMessage) : BaseBean(){
    data class Msg(var r : String){
        override fun toString() : String {
            return "Msg(r='$r')"
        }
    }

    override fun toString() : String {
        return "TranslateBean(data=$data, position=$position, messageBean=$messageBean)"
    }

}
