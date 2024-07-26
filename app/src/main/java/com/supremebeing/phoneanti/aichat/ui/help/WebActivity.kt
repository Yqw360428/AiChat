package com.supremebeing.phoneanti.aichat.ui.help

import android.annotation.SuppressLint
import android.view.KeyEvent
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.supremebeing.phoneanti.aichat.R
import com.supremebeing.phoneanti.aichat.base.ArchActivity
import com.supremebeing.phoneanti.aichat.databinding.ActivityWebBinding
import com.supremebeing.phoneanti.aichat.tool.setOnSingleClick
import com.supremebeing.phoneanti.aichat.ui.help.vm.HelpViewModel

class WebActivity : ArchActivity<HelpViewModel,ActivityWebBinding>() {
    private val privacyUrl = "https://sites.google.com/view/samanthai-privacy-policy/%E9%A6%96%E9%A1%B5"
    private var userAgreement = "https://sites.google.com/view/samanthai-terms-of-use/%E9%A6%96%E9%A1%B5"

    @SuppressLint("SetJavaScriptEnabled", "SetTextI18n")
    override fun initView() {
        binding.webBack.setOnSingleClick {
            this.finish()
        }

        binding.webView.settings.apply {
            javaScriptEnabled = true
            useWideViewPort = true
            loadWithOverviewMode = true
            layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
        }

        binding.webView.webViewClient = object : WebViewClient(){
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                view?.loadUrl(request?.url.toString())
                return true
            }
        }

        binding.webView.webChromeClient = object : WebChromeClient(){
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress < 100){
                    binding.progressBar.progress = newProgress
                }else{
                    binding.progressBar.progress = 0
                }

            }
        }
        val type = intent.getIntExtra("type",1)
        if (type == 1){
            binding.webBack.text = getString(R.string.terms_of_use)
            binding.webView.loadUrl(userAgreement)
        }else{
            binding.webBack.text = getString(R.string.privacy_policy)
            binding.webView.loadUrl(privacyUrl)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event!!.action == KeyEvent.ACTION_DOWN) {
            this.finish()
            return  true
        }
        return super.onKeyDown(keyCode, event)
    }
}