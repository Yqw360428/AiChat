package com.supremebeing.phoneanti.aichat.bean

data class ConnectSocketBean(
    val data : Connect
) : BaseBean(){
    data class Connect(val client_id : String){
        override fun toString() : String {
            return "Connect(client_id='$client_id')"
        }
    }

    override fun toString() : String {
        return "ConnectSocketBean(data=$data)"
    }

}
