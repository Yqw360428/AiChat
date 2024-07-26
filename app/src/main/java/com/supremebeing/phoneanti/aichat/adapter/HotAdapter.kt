package com.supremebeing.phoneanti.aichat.adapter

import com.blankj.utilcode.util.LogUtils
import com.bumptech.glide.Glide
import com.supremebeing.phoneanti.aichat.R
import com.supremebeing.phoneanti.aichat.base.ArchAdapter
import com.supremebeing.phoneanti.aichat.bean.AnchorDetail
import com.supremebeing.phoneanti.aichat.databinding.AdapterHotBinding
import com.supremebeing.phoneanti.aichat.network.server.TagCode
import com.supremebeing.phoneanti.aichat.tool.gone
import com.supremebeing.phoneanti.aichat.tool.visible

class HotAdapter(data : MutableList<AnchorDetail>) : ArchAdapter<AdapterHotBinding,AnchorDetail>(data) {
//    private lateinit var tagAdapter : TagAdapter
    private val tagList = mutableListOf<Pair<Int,String>>() //tag标签数据
    private val tagAdapter = TagAdapter(2,tagList) //tag适配器，流式布局

    override fun convert(binding : AdapterHotBinding, item : AnchorDetail) {
        tagList.clear()
        if (item.is_hot) tagList.add(Pair(TagCode.HOT.code,context.getString(R.string.hot)))
        if (item.is_new) tagList.add(Pair(TagCode.NEW.code,context.getString(R.string.new_)))
        if (item.is_nsfw) tagList.add(Pair(TagCode.NSFW.code,context.getString(R.string.nsfw)))
        item.tags?.let {
            if (it.isNotEmpty()){
                tagList.add(Pair(TagCode.STYLE.code,it.first().name))
            }
        }
        if (item.need_vip) tagList.add(Pair(TagCode.VIP.code,context.getString(R.string.vip)))

        binding.recyclerViewTag.adapter = tagAdapter
        if (tagList.isEmpty()) {
            binding.recyclerViewTag.gone()
        }else{
            binding.recyclerViewTag.visible()
        }
        tagAdapter.notifyDataChanged()

        Glide.with(context)
            .load(item.profile.cover)
            .error(R.drawable.place)
            .placeholder(R.drawable.place)
            .into(binding.hotIcon)

        if (item.profile.mood.isNullOrBlank()){
            binding.hotAbout.text =  context.getString(R.string.acout_hint)
        }else{
            binding.hotAbout.text =  item.profile.mood
        }
        binding.hotName.text = item.profile.nickname
    }
}