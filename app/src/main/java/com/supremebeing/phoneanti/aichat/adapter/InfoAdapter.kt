package com.supremebeing.phoneanti.aichat.adapter

import android.annotation.SuppressLint
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.supremebeing.phoneanti.aichat.R
import com.supremebeing.phoneanti.aichat.base.ArchAdapter
import com.supremebeing.phoneanti.aichat.bean.CardBean
import com.supremebeing.phoneanti.aichat.databinding.AdapterInfoBinding
import com.supremebeing.phoneanti.aichat.tool.getDrawable
import com.supremebeing.phoneanti.aichat.tool.gone
import com.supremebeing.phoneanti.aichat.tool.visible
import jp.wasabeef.glide.transformations.BlurTransformation

class InfoAdapter(data : MutableList<CardBean.Data.Medias>) : ArchAdapter<AdapterInfoBinding,CardBean.Data.Medias>(data) {
    var cover : String? = null

    @SuppressLint("SetTextI18n")
    override fun convert(binding : AdapterInfoBinding, item : CardBean.Data.Medias) {
        if (item.is_unlocked){
            //卡片已经解锁
            binding.infoPriceView.gone()
            binding.infoExpand.visible()
            Glide.with(context)
                .load(item.cover)
                .error(R.drawable.place)
                .placeholder(R.drawable.place)
                .into(binding.infoImage)
        }else{
            //卡片未解锁
            binding.infoPriceView.visible()
            binding.infoExpand.gone()
            binding.infoHeart.text = item.unlock?.hearts?.toString()
            binding.infoCoin.text = item.unlock?.token?.toString()
            Glide.with(context)
                .load(item.cover)
                .error(R.drawable.place)
                .placeholder(R.drawable.place)
                .apply(RequestOptions.bitmapTransform(BlurTransformation(4,8)))
                .into(binding.infoImage)
        }

        if (item.cover == cover){
            binding.infoView.background = R.drawable.shape_my_card.getDrawable()
            binding.infoSelect.visible()
        }else{
            binding.infoView.background = null
            binding.infoSelect.gone()
        }
    }
}