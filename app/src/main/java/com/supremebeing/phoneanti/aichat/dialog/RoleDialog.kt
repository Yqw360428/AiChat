package com.supremebeing.phoneanti.aichat.dialog

import android.content.Context
import com.bumptech.glide.Glide
import com.lxj.xpopup.util.XPopupUtils
import com.supremebeing.phoneanti.aichat.R
import com.supremebeing.phoneanti.aichat.base.ArchCenterDialog
import com.supremebeing.phoneanti.aichat.databinding.DialogRoleBinding
import com.supremebeing.phoneanti.aichat.tool.setGradientFont
import com.supremebeing.phoneanti.aichat.tool.setOnSingleClick
import com.supremebeing.phoneanti.aichat.tool.toastShort

class RoleDialog(context : Context) : ArchCenterDialog<DialogRoleBinding>(context) {

    override fun getMaxWidth() : Int = XPopupUtils.getAppWidth(context)

    var contentListener : ((String)->Unit)? = null
    var headIcon : String? = null

    override fun initView() {
        setGradientFont(binding.roleText, intArrayOf(R.color.ff93e6, R.color.af60ff))

        binding.roleClose.setOnSingleClick {
            dismiss()
        }

        binding.roleSubmit.setOnSingleClick {
            val content = binding.roleEdit.text.toString()
            if (content.isBlank()){
                R.string.please_enter_the_evaluation_content.toastShort
                return@setOnSingleClick
            }
            contentListener?.invoke(content)
        }

        Glide.with(context)
            .load(headIcon)
            .error(R.drawable.place)
            .placeholder(R.drawable.place)
            .into(binding.roleHead)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        popupInfo.isMoveUpToKeyboard = true
    }
}