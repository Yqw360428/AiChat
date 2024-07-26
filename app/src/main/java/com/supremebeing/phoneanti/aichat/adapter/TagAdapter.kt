package com.supremebeing.phoneanti.aichat.adapter

import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.supremebeing.phoneanti.aichat.R
import com.supremebeing.phoneanti.aichat.network.server.TagCode
import com.supremebeing.phoneanti.aichat.tool.getDrawable
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter

class TagAdapter(val type : Int = 1,data : MutableList<Pair<Int, String>>) : TagAdapter<Pair<Int, String>>(data) {
    override fun getView(parent : FlowLayout?, position : Int, item : Pair<Int, String>?) : View {
        val textView = LayoutInflater.from(parent?.context)
            .inflate(if (type == 1) R.layout.adapter_tag1 else R.layout.adapter_tag, parent, false) as AppCompatTextView
        val back = when (item?.first) {
            TagCode.NSFW.code -> {
                textView.setFire()
                R.drawable.shape_hot
            }

            TagCode.VIP.code -> {
                textView.setFire()
                R.drawable.shape_vip
            }

            TagCode.NEW.code -> {
                textView.setFire()
                R.drawable.shape_new
            }

            TagCode.HOT.code -> {
                textView.setFire(true)
                R.drawable.shape_nsfw
            }

            TagCode.STYLE.code -> {
                textView.setFire()
                R.drawable.shape_sexual
            }

            else -> R.drawable.shape_nsfw
        }

        textView.background = back.getDrawable()

        textView.text = item?.second
        return textView
    }

    private fun AppCompatTextView.setFire(fire : Boolean = false){
        if (fire){
            this.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.h2, 0, 0, 0)
        }else{
            this.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
        }
    }
}