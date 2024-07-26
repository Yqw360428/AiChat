package com.supremebeing.phoneanti.aichat.dialog

import android.annotation.SuppressLint
import android.content.Context
import com.lxj.xpopup.util.XPopupUtils
import com.supremebeing.phoneanti.aichat.R
import com.supremebeing.phoneanti.aichat.base.ArchCenterDialog
import com.supremebeing.phoneanti.aichat.bean.BabyEquityBean
import com.supremebeing.phoneanti.aichat.databinding.DialogFirstRechargeBinding
import com.supremebeing.phoneanti.aichat.tool.setOnSingleClick

class FirstRechargeDialog(context : Context) : ArchCenterDialog<DialogFirstRechargeBinding>(context) {
    private var data : BabyEquityBean.Data.Rights? = null
    var getListener : ((String,String)->Unit)? = null

    override fun getMaxWidth() : Int = XPopupUtils.getAppWidth(context)

    override fun initView() {

        binding.firstPrice.setOnSingleClick {
            data?.right?.desc?.let {
                getListener?.invoke(it.price,it.token_code)
            }
           dismiss()
        }

        binding.firstClose.setOnSingleClick {
            dismiss()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        data?.let {
            binding.firstCoin2.text = "x${it.right.desc.token_amount}"
            binding.firstPrice.text = context.getString(R.string.first_price,it.right.desc.price)
        }
    }

    fun setData(data : BabyEquityBean.Data.Rights){
        this.data = data
    }
}