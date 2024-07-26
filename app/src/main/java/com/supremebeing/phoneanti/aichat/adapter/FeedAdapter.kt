package com.supremebeing.phoneanti.aichat.adapter

import com.supremebeing.phoneanti.aichat.R
import com.supremebeing.phoneanti.aichat.base.ArchAdapter
import com.supremebeing.phoneanti.aichat.bean.FeedBean
import com.supremebeing.phoneanti.aichat.databinding.AdapterFeedBinding
import com.supremebeing.phoneanti.aichat.tool.getDrawable

class FeedAdapter(data : MutableList<FeedBean>) : ArchAdapter<AdapterFeedBinding,FeedBean>(data) {
    override fun convert(binding : AdapterFeedBinding, item : FeedBean) {
        binding.feedText.text = item.text

        if (item.select){
            binding.feedText.background = R.drawable.shape_r5_562e78.getDrawable()
        }else{
            binding.feedText.background = R.drawable.shape_r5_white10.getDrawable()
        }
    }
}