package com.supremebeing.phoneanti.aichat.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.bumptech.glide.Glide
import com.supremebeing.phoneanti.aichat.R
import com.supremebeing.phoneanti.aichat.weight.BarrageView

@SuppressLint("InflateParams","SetTextI18n")
class BarrageViewHolder : BarrageView.ViewHolder {

    override fun getItemView(context : Context, item : Any, index : Int) : View {
        val bean = item as String
        val view = LayoutInflater.from(context).inflate(R.layout.item_barrage,null)
        val textView = view.findViewById<AppCompatTextView>(R.id.barrage_text)
        textView.text = "\"${bean}\""
        return view
    }
}