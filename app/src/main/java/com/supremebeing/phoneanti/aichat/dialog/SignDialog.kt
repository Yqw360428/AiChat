package com.supremebeing.phoneanti.aichat.dialog

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.supremebeing.phoneanti.aichat.adapter.SignInAdapter
import com.supremebeing.phoneanti.aichat.base.ArchCenterDialog
import com.supremebeing.phoneanti.aichat.databinding.DialogSignBinding
import com.supremebeing.phoneanti.aichat.tool.setOnSingleClick
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SignDialog(context : Context) : ArchCenterDialog<DialogSignBinding>(context) {
    private var signList = mutableListOf<MultiItemEntity>()
    private lateinit var signInAdapter : SignInAdapter

    var signListener : ((Boolean)->Unit)? = null

    override fun initView() {
        signInAdapter = SignInAdapter(signList)
        binding.recyclerViewSign.layoutManager = GridLayoutManager(context,4)
        binding.recyclerViewSign.adapter = signInAdapter

        binding.getBtn1.setOnSingleClick {
            signListener?.invoke(false)
        }

        binding.getBtn.setOnSingleClick {
            signListener?.invoke(true)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        signInAdapter.notifyDataSetChanged()
    }

    fun setData(signList : MutableList<MultiItemEntity>){
        this.signList.clear()
        this.signList.addAll(signList)
    }
}