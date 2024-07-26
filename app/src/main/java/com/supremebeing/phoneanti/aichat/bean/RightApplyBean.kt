package com.supremebeing.phoneanti.aichat.bean

data class RightApplyBean(val data : Data? = null) : BaseBean(){
    data class Data(
        val token : Int? = null
    ){
        override fun toString() : String {
            return "Data(token=$token)"
        }
    }

    override fun toString() : String {
        return "RightApplyBean(data=$data)"
    }

}
