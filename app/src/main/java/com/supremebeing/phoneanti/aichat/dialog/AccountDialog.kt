package com.supremebeing.phoneanti.aichat.dialog

import android.content.Context
import com.supremebeing.phoneanti.aichat.base.ArchBottomDialog
import com.supremebeing.phoneanti.aichat.databinding.DialogAccountBinding
import com.supremebeing.phoneanti.aichat.tool.setOnSingleClick
import com.supremebeing.phoneanti.aichat.ui.main.MainActivity

class AccountDialog(context : Context) : ArchBottomDialog<DialogAccountBinding>(context) {
    private var listener : ((Boolean)->Unit)? = null
    fun onListener(listener : ((Boolean)->Unit)){
        this.listener = listener
    }

    override fun initView() {
        binding.root.setPadding(0,0,0,(activity as MainActivity).getBarHeight())
        binding.accountOut.setOnSingleClick {
            listener?.invoke(false)
            dismiss()
        }

        binding.accountDelete.setOnSingleClick {
            listener?.invoke(true)
            dismiss()
        }
    }
}