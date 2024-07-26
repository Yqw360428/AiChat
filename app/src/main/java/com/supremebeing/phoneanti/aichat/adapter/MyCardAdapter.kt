package com.supremebeing.phoneanti.aichat.adapter

import com.bumptech.glide.Glide
import com.supremebeing.phoneanti.aichat.R
import com.supremebeing.phoneanti.aichat.base.ArchAdapter
import com.supremebeing.phoneanti.aichat.bean.CardBean
import com.supremebeing.phoneanti.aichat.databinding.AdapterMyCardBinding

class MyCardAdapter(data : MutableList<CardBean.Data.Medias>) : ArchAdapter<AdapterMyCardBinding,CardBean.Data.Medias>(data) {
    override fun convert(binding : AdapterMyCardBinding, item : CardBean.Data.Medias) {
        Glide.with(context)
            .load(item.cover)
            .placeholder(R.drawable.place)
            .error(R.drawable.place)
            .into(binding.myCardImage)
    }
}