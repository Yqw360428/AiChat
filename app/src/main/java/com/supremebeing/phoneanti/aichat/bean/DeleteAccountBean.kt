package com.supremebeing.phoneanti.aichat.bean

data class DeleteAccountBean(val data : Data) : BaseBean(){
    data class Data(
        val email : String,
        val skey : String
    ){
        override fun toString() : String {
            return "Data(email='$email', skey='$skey')"
        }
    }

    override fun toString() : String {
        return "DeleteAccountBean(data=$data)"
    }

}
