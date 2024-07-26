package com.supremebeing.phoneanti.aichat.dialog

import android.content.Context
import android.content.Intent
import com.lxj.xpopup.util.XPopupUtils
import com.supremebeing.phoneanti.aichat.base.ArchCenterDialog
import com.supremebeing.phoneanti.aichat.databinding.DialogAppBinding
import com.supremebeing.phoneanti.aichat.tool.AIP
import com.supremebeing.phoneanti.aichat.tool.actionIntent
import com.supremebeing.phoneanti.aichat.tool.setOnSingleClick

class AppDialog(context : Context) : ArchCenterDialog<DialogAppBinding>(context){

    override fun getMaxWidth() : Int = XPopupUtils.getAppWidth(context)

    private var startCount = 0f

    override fun initView() {
        binding.appClose.setOnSingleClick {
            dismiss()
        }

        binding.appStart.setOnRatingChangeListener {
            startCount = it
        }

        binding.appRate.setOnSingleClick {
            AIP.rated = true
            if (startCount>=4){
                actionIntent(context)
            }
            dismiss()
        }
    }
}