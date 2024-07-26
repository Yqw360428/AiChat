package com.supremebeing.phoneanti.aichat.adapter

import com.bumptech.glide.Glide
import com.supremebeing.phoneanti.aichat.R
import com.supremebeing.phoneanti.aichat.base.ArchAdapter
import com.supremebeing.phoneanti.aichat.bean.MessageMoreBean
import com.supremebeing.phoneanti.aichat.databinding.AdapterMessageBinding
import com.supremebeing.phoneanti.aichat.sql.imEntity.UserInfo
import com.supremebeing.phoneanti.aichat.tool.gone
import com.supremebeing.phoneanti.aichat.tool.visible

class UserAdapter(data : MutableList<MessageMoreBean>) : ArchAdapter<AdapterMessageBinding,MessageMoreBean>(data) {
    override fun convert(binding : AdapterMessageBinding, item : MessageMoreBean) {
        Glide.with(context)
            .load(item.head)
            .placeholder(R.drawable.place)
            .error(R.drawable.place)
            .into(binding.meIcon)

        binding.meName.text = item.name
        binding.meContent.text = item.content

        if (item.read>0){
            binding.meRead.visible()
            binding.meRead.text = item.read.toString()
        }else{
            binding.meRead.gone()
        }
    }
}