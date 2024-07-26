package com.supremebeing.phoneanti.aichat.bean


data class BalanceUpdateBean(val data: BalanceUp) : BaseBean(){
    data class BalanceUp(
        var balance: Int = 0,
        val balanced_at: Int = 0,
        val biz_code: Int = 0,
        val biz: Biz? = null
    ){
        data class Biz(
            var id: Int = 0,
            val type: Int = 0,
            val hunting_finished: Boolean = false,
            val direction: Int = 0,
            val duration: Int = 0,
            val token: Int = 0,
            val call_token: Int = 0,
            val gift_token: Int = 0,
            val remind_duration: Int = 0,
            val is_in_free: Boolean =
                false,
            val notify_recharge: NotifyRecharge?=
                null,
            val other_people: OtherPeopleBiz? =
                null,
            val started_at: Long
        ){
            data class NotifyRecharge(
                var is_in_free: Boolean = false,
                val content: String? = null,
                val recharge:FirstRecharge?=null
            )
            data class OtherPeopleBiz(
                var id: Int = 0,
                val profile: OtherProfile? = null,
                val is_online: Boolean = false,
                val is_busy: Boolean = false,
                var role:Int
            ){
                data class OtherProfile(
                    var id:Int,
                    var nickname: String? = null,
                    val gender: Int = 0,
                    val age: Int = 0,
                    val head: String? = null
                )
            }
        }
    }
}
