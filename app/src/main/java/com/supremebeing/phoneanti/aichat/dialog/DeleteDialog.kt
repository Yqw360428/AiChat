package com.supremebeing.phoneanti.aichat.dialog

import android.content.Context
import com.supremebeing.phoneanti.aichat.R
import com.supremebeing.phoneanti.aichat.base.ArchBottomDialog
import com.supremebeing.phoneanti.aichat.databinding.DialogDeleteBinding
import com.supremebeing.phoneanti.aichat.tool.setOnSingleClick
import com.supremebeing.phoneanti.aichat.ui.chat.ChatActivity
import com.supremebeing.phoneanti.aichat.ui.chat.InfoActivity

class DeleteDialog(context : Context) : ArchBottomDialog<DialogDeleteBinding>(context) {
    var onListener : ((Int)->Unit)? = null
    private var type = 0
    private var price = 0

    override fun initView() {
        if (activity is ChatActivity){
            binding.root.setPadding(0,0,0,(activity as ChatActivity).getBarHeight())
        }else{
            binding.root.setPadding(0,0,0,(activity as InfoActivity).getBarHeight())
        }

        binding.deleteBtn.setOnSingleClick {
            onListener?.invoke(type)
            dismiss()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        when(type){
            0->{
                //删除消息
                binding.deleteIcon.setImageResource(R.drawable.di_de)
                binding.deleteText.text = context.getString(R.string.delete_hint)
            }
            1->{
                //解锁卡片
                binding.deleteIcon.setImageResource(R.drawable.di_buy)
                binding.deleteText.text = context.getString(R.string.buy_hint,price)
            }
//            2->{
//                //通知权限
//                binding.deleteIcon.setImageResource(R.drawable.di_no)
//                binding.deleteText.text = context.getString(R.string.noti_hint)
//            }
        }
    }

    fun setType(type : Int){
        this.type = type
    }

    fun setPrice(price : Int){
        this.price = price
    }
}