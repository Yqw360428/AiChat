package com.supremebeing.phoneanti.aichat.bean

data class DailyBean(val data : Daily) : BaseBean(){
    data class Daily(val token : Int){
        override fun toString() : String {
            return "Daily(token=$token)"
        }
    }

    override fun toString() : String {
        return "DailyBean(data=$data)"
    }

}
