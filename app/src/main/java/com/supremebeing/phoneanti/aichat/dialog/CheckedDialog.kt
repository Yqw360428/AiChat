package com.supremebeing.phoneanti.aichat.dialog

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.lifecycleScope
import com.supremebeing.phoneanti.aichat.base.ArchCenterDialog
import com.supremebeing.phoneanti.aichat.databinding.DialogCheckedBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CheckedDialog(context : Context) : ArchCenterDialog<DialogCheckedBinding>(context) {
    private var coin = 0
    var closeListener : (()->Unit)? = null
    override fun initView() {
        lifecycleScope.launch {
            delay(2000)
            dismiss()
            closeListener?.invoke()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        binding.checkedCoin.text = "+$coin"
    }

    fun setCoin(coin : Int){
        this.coin = coin
    }
}