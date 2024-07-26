package com.supremebeing.phoneanti.aichat.adapter

import com.supremebeing.phoneanti.aichat.base.ArchAdapter
import com.supremebeing.phoneanti.aichat.databinding.AdapterRecommendBinding

class RecommendAdapter(data : MutableList<String>) : ArchAdapter<AdapterRecommendBinding,String>(data) {
    override fun convert(binding : AdapterRecommendBinding, item : String) {
        binding.recommendText.text = item
    }
}