package com.supremebeing.phoneanti.aichat.ui.start

import android.content.Intent
import android.icu.number.IntegerWidth
import android.net.Uri
import android.view.KeyEvent
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.LogUtils
import com.supremebeing.phoneanti.aichat.BuildConfig
import com.supremebeing.phoneanti.aichat.R
import com.supremebeing.phoneanti.aichat.base.ArchActivity
import com.supremebeing.phoneanti.aichat.databinding.ActivityStartBinding
import com.supremebeing.phoneanti.aichat.dialog.UpdateDialog
import com.supremebeing.phoneanti.aichat.tool.AIP
import com.supremebeing.phoneanti.aichat.tool.hasSimCard
import com.supremebeing.phoneanti.aichat.tool.isDeviceInVPN
import com.supremebeing.phoneanti.aichat.tool.serverIP
import com.supremebeing.phoneanti.aichat.tool.socketIP
import com.supremebeing.phoneanti.aichat.ui.start.vm.StartViewModel
import com.supremebeing.phoneanti.aichat.utils.ActivityUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

class StartActivity : ArchActivity<StartViewModel,ActivityStartBinding>() {
    private lateinit var updateDialog : UpdateDialog
    private var upLink : String? = null

    override fun initView() {
        hideBars()


        updateDialog = UpdateDialog(this)

        lifecycleScope.launch {
            while (isActive){
                viewModel.init(hasSimCard(this@StartActivity), isDeviceInVPN())
                delay(8000)
            }
        }

//        lifecycleScope.launch {
//            withTimeoutOrNull(LaunchUtil.maxTime){
//                launch {
//                    delay(2000)
//                }
//                launch {
//
//                }
//            }
//            ActivityUtil.startToMain(this@StartActivity)
//        }

        viewModel.initData.observe(this){
            it?.apply {
                data?.last?.let { //正式服
                    if (!data.region.isNullOrEmpty()) {
                        val region = data.region!![0]
                        AIP.apiUrl = region.api
                        AIP.socketUrl = region.im
                        AIP.isRelease = false
                    } else {
                        AIP.isRelease = true
                        AIP.apiUrl = serverIP
                        AIP.socketUrl = socketIP
                    }
                    updateDialog.setData(data.last?.version)
                    upLink = data.last?.link
                    if (data.last?.version!! > BuildConfig.VERSION_NAME) {
                        if (data.last?.is_force == 1) {
                            updateDialog.setType(1)
                            updateDialog.showDialog(false)
                        } else {
                            updateDialog.showDialog(true)
                        }
                    } else {
                        AIP.apiUrl = serverIP
                        AIP.socketUrl = socketIP
                        ActivityUtil.startToMain(this@StartActivity)
                    }

                }?: let {
                    if (!data?.region.isNullOrEmpty()) {
                        AIP.isRelease = false
                        val region = data?.region!![0]
                        AIP.apiUrl = region.api
                        AIP.socketUrl = region.im
                    } else {
                        AIP.isRelease = true
                        AIP.apiUrl = serverIP
                        AIP.socketUrl = socketIP
                    }
                    ActivityUtil.startToMain(this@StartActivity)
                }
            }?:let {
                AIP.apiUrl = serverIP
                AIP.socketUrl = socketIP
                ActivityUtil.startToMain(this@StartActivity)
            }
        }

        updateDialog.updateListener = {
            if (!upLink.isNullOrBlank()){
                val uri = Uri.parse(upLink)    //设置跳转的网站
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
        }

        updateDialog.closeListener = {
            ActivityUtil.startToMain(this@StartActivity)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode== KeyEvent.KEYCODE_BACK && event!!.action == KeyEvent.ACTION_DOWN){
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}