package com.supremebeing.phoneanti.aichat.dialog

import android.content.Context
import com.lxj.xpopup.util.XPopupUtils
import com.supremebeing.phoneanti.aichat.R
import com.supremebeing.phoneanti.aichat.base.ArchCenterDialog
import com.supremebeing.phoneanti.aichat.databinding.DialogUpdateBinding
import com.supremebeing.phoneanti.aichat.tool.gone
import com.supremebeing.phoneanti.aichat.tool.setGradientFont
import com.supremebeing.phoneanti.aichat.tool.setOnSingleClick
import com.supremebeing.phoneanti.aichat.tool.visible

class UpdateDialog(context : Context) : ArchCenterDialog<DialogUpdateBinding>(context){
    var updateListener : (()->Unit)? = null
    var closeListener : (()->Unit)? = null
    private var version : String? = null
    private var type = 0

    override fun getMaxWidth() : Int = XPopupUtils.getAppWidth(context)

    override fun initView() {
        popupInfo.maxWidth = windowDecorView.width
        popupInfo.isDismissOnBackPressed = false
        binding.updateClose.setOnSingleClick {
            closeListener?.invoke()
            dismiss()
        }

        binding.updateBtn.setOnSingleClick {
            updateListener?.invoke()
            dismiss()
        }

        setGradientFont(binding.updateText, intArrayOf(R.color.ff93e6,R.color.af60ff))
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        binding.updateVersion.text = context.getString(R.string.update_to_s,version)

        if (type == 1){
            binding.updateClose.gone()
        }else{
            binding.updateClose.visible()
        }
    }


    fun setData(version : String?){
        this.version = version
    }

    fun setType(type : Int){
        this.type = type
    }
}