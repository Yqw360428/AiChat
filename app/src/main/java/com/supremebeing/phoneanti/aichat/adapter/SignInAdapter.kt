package com.supremebeing.phoneanti.aichat.adapter

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.supremebeing.phoneanti.aichat.R
import com.supremebeing.phoneanti.aichat.bean.SignBean
import com.supremebeing.phoneanti.aichat.tool.gone
import com.supremebeing.phoneanti.aichat.tool.visible

/**
 * ynw'\
 * 签到
 */
class SignInAdapter(data: MutableList<MultiItemEntity>) : BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder>(data){
    init {
        addItemType(1, R.layout.adapter_sign1)
        addItemType(2, R.layout.adapter_sign2)
    }
    //今天签到位置
    private var todayPosition=0
    fun setTodayPosition(i:Int){
        todayPosition=i
    }

    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseViewHolder, item: MultiItemEntity) {
        when (holder.itemViewType) {
            1 -> {
                val bean = item as SignBean.Sign.Checkins
                val icon = holder.getView<AppCompatImageView>(R.id.sign1_icon)
                val check = holder.getView<AppCompatImageView>(R.id.sign1_check)
                val coin = holder.getView<AppCompatTextView>(R.id.sign1_coin)
                setImage(item,icon)
                coin.text = "x${bean.token_amount}"
                if (bean.checked){
                    check.visible()
                }else{
                    check.gone()
                }
            }

            2 -> {
                val bean = item as SignBean.Sign.Checkins
                val icon = holder.getView<AppCompatImageView>(R.id.sign2_icon)
                val check = holder.getView<AppCompatImageView>(R.id.sign2_check)
                val coin = holder.getView<AppCompatTextView>(R.id.sign2_coin)
                coin.text = "x${bean.token_amount}"
                setImage(item,icon)

                if (bean.checked){
                    check.visible()
                }else{
                    check.gone()
                }

            }
        }
    }

    private fun setImage(item : MultiItemEntity,view : AppCompatImageView){
        when(getItemPosition(item)){
            0->view.setImageResource(R.drawable.si1)
            1->view.setImageResource(R.drawable.si2)
            2->view.setImageResource(R.drawable.si3)
            3->view.setImageResource(R.drawable.si4)
            4->view.setImageResource(R.drawable.si5)
            5->view.setImageResource(R.drawable.si6)
        }
    }
}