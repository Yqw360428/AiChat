package com.supremebeing.phoneanti.aichat.ui.chat

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.supremebeing.phoneanti.aichat.R
import com.supremebeing.phoneanti.aichat.base.ArchActivity
import com.supremebeing.phoneanti.aichat.databinding.ActivityBigBinding
import com.supremebeing.phoneanti.aichat.network.server.HttpCode
import com.supremebeing.phoneanti.aichat.tool.permissions_storage
import com.supremebeing.phoneanti.aichat.tool.saveImage
import com.supremebeing.phoneanti.aichat.tool.setOnSingleClick
import com.supremebeing.phoneanti.aichat.tool.toastShort
import com.supremebeing.phoneanti.aichat.ui.chat.vm.ChatViewModel
import com.supremebeing.phoneanti.aichat.utils.netToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BigActivity : ArchActivity<ChatViewModel,ActivityBigBinding>() {
    private var cover : String? = null
    private var anchorId = 0

    override fun initView() {
        hideBars()
        intent.extras?.getString("cover")?.let {
            cover = it
            Glide.with(this)
                .load(it)
                .into(binding.bigImage)
        }

        intent.extras?.getInt("anchorId")?.let {
            anchorId = it
        }

        binding.bigClose.setOnSingleClick {
            this.finish()
        }

        binding.bigSet.setOnSingleClick {
            showLoading()
            viewModel.refreshCover(anchorId,cover!!)
        }

        viewModel.coverData.observe(this){
            dismissLoading()
            if (it.error_code == HttpCode.SUCCESS.code){
                getString(R.string.set_successfully).toastShort
                lifecycleScope.launch {
                    delay(1000)
                    launch(ChatActivity::class.java,Bundle().apply {
                        putInt("id",anchorId)
                    })
                }
            }else{
                netToast()
            }
        }

        binding.bigDownload.setOnSingleClick {
            launch(permissions_storage){
                if (!it.containsValue(false)){
                    saveImage(this,cover!!)
                }else{
                    getString(R.string.agree_permission).toastShort
                }
            }

        }
    }
}