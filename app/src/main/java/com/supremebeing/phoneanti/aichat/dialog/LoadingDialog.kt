package com.supremebeing.phoneanti.aichat.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import com.supremebeing.phoneanti.aichat.R

class LoadingDialog(context: Context) : Dialog(context, R.style.DialogStyle) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_loading)
        window!!.setGravity(Gravity.CENTER)
        setCancelable(true)
    }
}