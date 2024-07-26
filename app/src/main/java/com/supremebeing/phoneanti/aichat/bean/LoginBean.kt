package com.supremebeing.phoneanti.aichat.bean

data class LoginBean(val data : Login) : BaseBean(){
    data class Login(
        var id : Int,
        val api_token : String,
        val email : String?,
        val profile : Profile,
        val role : Int,
        val balance : Int
    ) : BaseBean(){
        override fun toString() : String {
            return "LoginBean(id=$id, api_token='$api_token', email='$email', profile=$profile, role=$role, balance=$balance)"
        }
    }
}

