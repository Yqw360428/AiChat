package com.supremebeing.phoneanti.aichat.dialog

import android.content.Context
import com.supremebeing.phoneanti.aichat.base.ArchCenterDialog
import com.supremebeing.phoneanti.aichat.databinding.DialogNotificationBinding
import com.supremebeing.phoneanti.aichat.tool.setOnSingleClick

class NotificationDialog(context : Context) : ArchCenterDialog<DialogNotificationBinding>(context) {
    var notifyListener : (()->Unit)? = null
    override fun initView() {
        binding.boClose.setOnSingleClick {
            dismiss()
        }

        binding.boBtn.setOnSingleClick {
            notifyListener?.invoke()
            dismiss()
        }
    }
}