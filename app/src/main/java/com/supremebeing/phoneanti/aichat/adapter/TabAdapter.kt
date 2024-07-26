package com.supremebeing.phoneanti.aichat.adapter

import com.supremebeing.phoneanti.aichat.R
import com.supremebeing.phoneanti.aichat.base.ArchAdapter
import com.supremebeing.phoneanti.aichat.bean.TabBean
import com.supremebeing.phoneanti.aichat.databinding.AdapterTabBinding
import com.supremebeing.phoneanti.aichat.tool.getColor

class TabAdapter(data : MutableList<TabBean>) : ArchAdapter<AdapterTabBinding,TabBean>(data) {
    override fun convert(binding : AdapterTabBinding, item : TabBean) {
        binding.tabText.text = item.tab
        binding.tabText.setTextColor(if (item.select) R.color.white.getColor() else R.color._6f5388.getColor())
    }
}