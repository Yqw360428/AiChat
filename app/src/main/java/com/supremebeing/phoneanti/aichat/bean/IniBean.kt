package com.supremebeing.phoneanti.aichat.bean

data class IniBean(
    val data : Ini
) : BaseBean(){
    data class Ini(
        var match_price: MatchPrice? = null,
        val message_price: Int = 0,
        val agora_app_id: String? = null,
        val feedback: Boolean = false,
        val chat_gift: Boolean = false,
        val join_link: String? = null,
        val search_on: Boolean = false,
        val version: String? = null,
        val contact_url:String? = "",
        val ad : Ad? = null
    ){
        data class MatchPrice(
            var both: Int = 0,
            val male: Int = 0,
            val female: Int = 0
        ){
            override fun toString() : String {
                return "MatchPrice(both=$both, male=$male, female=$female)"
            }
        }
        data class Ad(
            val enabled : Boolean,
            val max_count : Int,
            val interval_minute : Int
        ){
            override fun toString() : String {
                return "Ad(enabled=$enabled, max_count=$max_count, interval_minute=$interval_minute)"
            }
        }
    }

}
