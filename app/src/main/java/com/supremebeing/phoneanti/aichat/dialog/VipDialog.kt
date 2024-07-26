package com.supremebeing.phoneanti.aichat.dialog

import android.content.Context
import com.supremebeing.phoneanti.aichat.base.ArchBottomDialog
import com.supremebeing.phoneanti.aichat.databinding.DialogVipBinding
import com.supremebeing.phoneanti.aichat.tool.setOnSingleClick

class VipDialog(context : Context) : ArchBottomDialog<DialogVipBinding>(context) {
    override fun initView() {
        binding.vipLet.setOnSingleClick {

        }
    }
}