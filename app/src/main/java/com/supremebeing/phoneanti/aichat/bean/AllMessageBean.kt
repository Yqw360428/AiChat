package com.supremebeing.phoneanti.aichat.bean

data class AllMessageBean(var data : AllMessage) : BaseBean(){
    data class AllMessage(var str : String? = ""){
        override fun toString() : String {
            return "AllMessage(str=$str)"
        }
    }
}