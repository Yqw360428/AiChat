package com.supremebeing.phoneanti.aichat.adapter

import android.annotation.SuppressLint
import com.supremebeing.phoneanti.aichat.R
import com.supremebeing.phoneanti.aichat.base.ArchAdapter
import com.supremebeing.phoneanti.aichat.bean.TokensBean
import com.supremebeing.phoneanti.aichat.databinding.AdapterRecharge1Binding
import com.supremebeing.phoneanti.aichat.tool.getDrawable

class RechargeAdapter(data : MutableList<TokensBean.Data.Stoken.Item>) : ArchAdapter<AdapterRecharge1Binding,TokensBean.Data.Stoken.Item>(data) {
    @SuppressLint("SetTextI18n")
    override fun convert(binding : AdapterRecharge1Binding, item : TokensBean.Data.Stoken.Item) {
        binding.rechargeIcon.background = when(getItemPosition(item)){
            0-> R.drawable.st1.getDrawable()
            1-> R.drawable.st2.getDrawable()
            2-> R.drawable.st3.getDrawable()
            3-> R.drawable.st4.getDrawable()
            4-> R.drawable.st5.getDrawable()
            5-> R.drawable.st6.getDrawable()
            6-> R.drawable.st7.getDrawable()
            else-> R.drawable.st1.getDrawable()
        }

        binding.rechargePrice.text = "$"+item.price

        binding.rechargeCoin.text = when(item.type){
            2-> item.name
            3-> item.token_amount.toString()
            else-> item.token_amount.toString()
        }
    }
}