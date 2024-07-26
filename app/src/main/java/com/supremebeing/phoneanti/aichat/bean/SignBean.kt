package com.supremebeing.phoneanti.aichat.bean

import com.chad.library.adapter.base.entity.MultiItemEntity

data class SignBean(
   val data : Sign
) : BaseBean(){
    data class Sign(
        var today_checked: Boolean = false,
        val checkins: ArrayList<Checkins>? = null
    ){
        data class Checkins(
            var name: String? = null,
            val token_amount: Int = 0,
            var checked: Boolean = false,
            override var itemType: Int = 1
        ) : MultiItemEntity
    }
}
