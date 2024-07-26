package com.supremebeing.phoneanti.aichat.bean

open class BaseBean(var error_code : Int = 0, var message : String? =null){
    override fun toString() : String {
        return "BaseBean(message=$message, error_code=$error_code)"
    }
}
