package com.supremebeing.phoneanti.aichat.ui.help

import androidx.lifecycle.lifecycleScope
import com.supremebeing.phoneanti.aichat.R
import com.supremebeing.phoneanti.aichat.base.ArchActivity
import com.supremebeing.phoneanti.aichat.databinding.ActivityFeedbackBinding
import com.supremebeing.phoneanti.aichat.network.server.HttpCode
import com.supremebeing.phoneanti.aichat.tool.setOnSingleClick
import com.supremebeing.phoneanti.aichat.tool.toastShort
import com.supremebeing.phoneanti.aichat.ui.help.vm.HelpViewModel
import com.supremebeing.phoneanti.aichat.utils.netToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FeedBackActivity : ArchActivity<HelpViewModel,ActivityFeedbackBinding>() {
    override fun initView() {

        binding.feedClose.setOnSingleClick {
            this.finish()
        }

        binding.feedSubmit.setOnSingleClick {
            val content = binding.feedContent.text.toString()
            if (content.isNotBlank()){
                showLoading()
                viewModel.feedBack(content)
            }else{
                getString(R.string.please_input_feedback_content).toastShort
            }
        }

        viewModel.feedBackData.observe(this){
            dismissLoading()
            if (it.error_code == HttpCode.SUCCESS.code){
                lifecycleScope.launch {
                    R.string.feedback_thk.toastShort
                    delay(2000)
                    finish()
                }
            }else{
                netToast()
            }
        }
    }
}