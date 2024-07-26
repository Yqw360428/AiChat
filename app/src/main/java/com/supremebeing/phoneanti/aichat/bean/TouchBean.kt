package com.supremebeing.phoneanti.aichat.bean

data class TouchBean(val data : Touch) : BaseBean(){
    data class Touch(var str : String? = "")
}
