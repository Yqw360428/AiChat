package com.supremebeing.phoneanti.aichat.ui.start

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import com.blankj.utilcode.util.LogUtils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.supremebeing.phoneanti.aichat.R
import com.supremebeing.phoneanti.aichat.base.ArchActivity
import com.supremebeing.phoneanti.aichat.databinding.ActivityLoginBinding
import com.supremebeing.phoneanti.aichat.event.RxBus
import com.supremebeing.phoneanti.aichat.event.RxEvents
import com.supremebeing.phoneanti.aichat.network.server.HttpCode
import com.supremebeing.phoneanti.aichat.tool.AIP
import com.supremebeing.phoneanti.aichat.tool.getColor
import com.supremebeing.phoneanti.aichat.tool.setOnSingleClick
import com.supremebeing.phoneanti.aichat.tool.toastShort
import com.supremebeing.phoneanti.aichat.ui.help.WebActivity
import com.supremebeing.phoneanti.aichat.ui.main.MainActivity
import com.supremebeing.phoneanti.aichat.ui.start.vm.StartViewModel
import com.supremebeing.phoneanti.aichat.utils.SolarUtil
import com.supremebeing.phoneanti.aichat.utils.netToast

@Suppress("DEPRECATION")
class LoginActivity : ArchActivity<StartViewModel, ActivityLoginBinding>() {
    private lateinit var googleClient : GoogleSignInClient
    private val googleCode = 111

    override fun initView() {
        setClick()
//        LogUtils.e("=====>SHA1:${getAppSignSha1()}")

        binding.loginPrivacy.setPadding(0,0,0,getNavigationBarHeight()+20)

        googleClient = GoogleSignIn.getClient(
            this, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        )

        binding.loginGoogle.setOnSingleClick {
            showLoading()
            startActivityForResult(googleClient.signInIntent,googleCode)
        }

        binding.loginSignIn.setOnSingleClick {
            launch(RegisterActivity::class.java,Bundle().apply {
                putString("login","login")
            })
        }

        binding.loginSignUp.setOnSingleClick {
            launch(RegisterActivity::class.java,Bundle().apply {
                putString("login","register")
            })
        }

        viewModel.loginData.observe(this){login->
            dismissLoading()
            when (login.error_code) {
                HttpCode.SUCCESS.code -> {
                    login.data.let {
                        AIP.token = it.api_token
                        AIP.isLogin = true
                        AIP.userId = it.id
                        AIP.role = it.role
                        AIP.balance = it.balance.toString()
                        AIP.userEmail = it.email ?: ""
                        it.profile.let {profile->
                            AIP.userName = profile.nickname
                            AIP.gender = profile.gender
                            if (profile.head.isNotBlank()) AIP.head = profile.head
                        }
                        SolarUtil.login("google",1)
                        RxBus.post(RxEvents.LoginSuccess("1"))
                        if (it.api_token.isNotBlank()){
                            if (it.role != 2){
                                launch(MainActivity::class.java)
                                this.finish()
                            }else{
                                "Sorry,only users can log in!".toastShort
                            }
                        }
                    }
                }
                6001 -> {
                    login.message?.toastShort
                    launch(RegisterActivity::class.java,Bundle().apply {
                        putString("login","google")
                    })
                    this.finish()
                }
                else -> {
                    login?.message?.toastShort
                    SolarUtil.login("google",2)
                    netToast()
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == googleCode){
            dismissLoading()
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)
                val googleId = account.id
                googleId?.let {
                    AIP.googleId = it
                    showLoading()
                    viewModel.login(it)
                }
            }catch (e : ApiException){
                when(e.statusCode){
                    7-> netToast()
                }
            }

        }
    }

    private fun setClick() {
        val fullText = getString(R.string.login_hint2)
        val clickableText1 = getString(R.string.terms_of_use)
        val clickableText2 = getString(R.string.privacy_policy)

        val spannableString = SpannableString(fullText)

        val clickableSpan1 = object : ClickableSpan() {
            override fun onClick(widget : View) {
                // 处理第一个点击事件
                launch(WebActivity::class.java, Bundle().apply {
                    putInt("type",1)
                })
            }

            override fun updateDrawState(ds : TextPaint) {
                super.updateDrawState(ds)
                // 设置第一个可点击字符串的样式，比如颜色和下划线
                ds.color = R.color.white.getColor()
                ds.isUnderlineText = true
            }
        }

        val clickableSpan2 = object : ClickableSpan() {
            override fun onClick(widget : View) {
                // 处理第二个点击事件
                launch(WebActivity::class.java, Bundle().apply {
                    putInt("type",2)
                })
            }

            override fun updateDrawState(ds : TextPaint) {
                super.updateDrawState(ds)
                // 设置第二个可点击字符串的样式，比如颜色和下划线
                ds.color = R.color.white.getColor()
                ds.isUnderlineText = true
            }
        }

        val startIndex1 = fullText.indexOf(clickableText1)
        if (startIndex1 != - 1) {
            // 将第一个ClickableSpan应用于第一个可点击字符串的部分
            spannableString.setSpan(
                clickableSpan1,
                startIndex1,
                startIndex1 + clickableText1.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        val startIndex2 = fullText.indexOf(clickableText2)
        if (startIndex2 != - 1) {
            // 将第二个ClickableSpan应用于第二个可点击字符串的部分
            spannableString.setSpan(
                clickableSpan2,
                startIndex2,
                startIndex2 + clickableText2.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        binding.loginPrivacy.text = spannableString
        binding.loginPrivacy.movementMethod = LinkMovementMethod.getInstance()

    }
}