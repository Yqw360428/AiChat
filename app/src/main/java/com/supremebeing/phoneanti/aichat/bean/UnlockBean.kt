package com.supremebeing.phoneanti.aichat.bean

data class UnlockBean(val data : Data) : BaseBean(){
    data class Data(val media : Media){
        data class Media(
            val id : Int,
            val type : Int,
            val cover : String,
            val url : String,
            val created_at : Long
        ){
            override fun toString() : String {
                return "Media(id=$id, type=$type, cover='$cover', url='$url', created_at=$created_at)"
            }
        }

        override fun toString() : String {
            return "Data(media=$media)"
        }

    }
    override fun toString() : String {
        return "UnLockBean(data=$data)"
    }

}
