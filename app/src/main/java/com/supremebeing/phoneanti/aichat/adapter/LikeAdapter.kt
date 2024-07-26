package com.supremebeing.phoneanti.aichat.adapter

import android.annotation.SuppressLint
import com.bumptech.glide.Glide
import com.supremebeing.phoneanti.aichat.R
import com.supremebeing.phoneanti.aichat.base.ArchAdapter
import com.supremebeing.phoneanti.aichat.bean.FollowingBean
import com.supremebeing.phoneanti.aichat.databinding.AdapterLikeBinding
import com.supremebeing.phoneanti.aichat.tool.setOnSingleClick

class LikeAdapter(data : MutableList<FollowingBean.Data.Follower>) : ArchAdapter<AdapterLikeBinding,FollowingBean.Data.Follower>(data) {
    @SuppressLint("SetTextI18n")
    override fun convert(binding : AdapterLikeBinding, item : FollowingBean.Data.Follower) {
        Glide.with(context)
            .load(item.profile?.head)
            .placeholder(R.drawable.place)
            .error(R.drawable.place)
            .into(binding.likeIcon)

        binding.likeName.text = item.profile?.nickname
        binding.likeId.text = context.getString(R.string.id)+item.id.toString()

        binding.likeChat.setOnSingleClick {
            getOnItemChildClickListener()?.onItemChildClick(this,it,getItemPosition(item))
        }
    }
}