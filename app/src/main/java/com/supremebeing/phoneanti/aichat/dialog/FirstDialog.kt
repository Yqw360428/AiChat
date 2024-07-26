package com.supremebeing.phoneanti.aichat.dialog

import android.content.Context
import com.supremebeing.phoneanti.aichat.base.ArchCenterDialog
import com.supremebeing.phoneanti.aichat.databinding.DialogFirstBinding
import com.supremebeing.phoneanti.aichat.tool.setOnSingleClick

class FirstDialog(context : Context) : ArchCenterDialog<DialogFirstBinding>(context) {
    override fun initView() {
        binding.firstClose.setOnSingleClick {
            dismiss()
        }

        binding.firstBtn.setOnSingleClick {

        }
    }
}