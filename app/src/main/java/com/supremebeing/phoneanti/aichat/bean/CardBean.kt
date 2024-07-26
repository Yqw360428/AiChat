package com.supremebeing.phoneanti.aichat.bean

data class CardBean(val data : Data) : BaseBean(){
    data class Data(
        val medias : ArrayList<Medias>,
        val unlock_all : UnlockAll? = null
    ){
        data class Medias(
            val cover: String?=null,
            val created_at: Int?=null,
            val id: Int?=null,
            val type: Int?=null,
            var is_unlocked : Boolean,
            val unlock: Unlock?=null,
            val url: String?=null,
            val user_id : Int
        ){
            data class Unlock(
                val hearts : Int? = null,
                val token : Int? = null
            ){
                override fun toString() : String {
                    return "Unlock(hearts=$hearts, token=$token)"
                }
            }
            override fun toString() : String {
                return "Medias(cover=$cover, created_at=$created_at, id=$id, type=$type, unlock=$unlock, url=$url)"
            }

        }
        data class UnlockAll(
            val token : Int?,
            val original_token : Int?
        ){

        }

        override fun toString() : String {
            return "Data(medias=$medias)"
        }

    }

    override fun toString() : String {
        return "CardBean(data=$data)"
    }

}
