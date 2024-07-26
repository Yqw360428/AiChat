package com.supremebeing.phoneanti.aichat.ui.start

import android.os.Bundle
import android.util.Patterns
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.LinearLayoutCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.supremebeing.phoneanti.aichat.R
import com.supremebeing.phoneanti.aichat.base.ArchActivity
import com.supremebeing.phoneanti.aichat.databinding.ActivityRegisterBinding
import com.supremebeing.phoneanti.aichat.event.RxBus
import com.supremebeing.phoneanti.aichat.event.RxEvents
import com.supremebeing.phoneanti.aichat.network.server.HttpCode
import com.supremebeing.phoneanti.aichat.tool.AIP
import com.supremebeing.phoneanti.aichat.tool.gone
import com.supremebeing.phoneanti.aichat.tool.setOnSingleClick
import com.supremebeing.phoneanti.aichat.tool.toastShort
import com.supremebeing.phoneanti.aichat.tool.visible
import com.supremebeing.phoneanti.aichat.ui.main.MainActivity
import com.supremebeing.phoneanti.aichat.ui.start.vm.StartViewModel
import com.supremebeing.phoneanti.aichat.utils.SoftKeyBoardListener
import com.supremebeing.phoneanti.aichat.utils.SolarUtil
import com.supremebeing.phoneanti.aichat.utils.monthList
import com.supremebeing.phoneanti.aichat.utils.netToast
import com.supremebeing.phoneanti.aichat.utils.nowDate
import java.util.Calendar


class RegisterActivity : ArchActivity<StartViewModel, ActivityRegisterBinding>() {
    private lateinit var googleClient : GoogleSignInClient
    private var age = 18
    private var year = 0
    private var month = 0
    private var day = 0
    private var login : String = ""
    private var step = 1

    override fun initView() {
        intent.extras?.getString("login")?.let {
            login = it
            when (login) {
                "login" -> {
                    binding.registerEmail.visible()
                    binding.registerPwd.visible()
                    refreshTop(hint = R.string.sign_in_to_your_account)
                    binding.registerBtn.text = getString(R.string.sign_in)
                    showInput(binding.registerEmail)
                }

                "register" -> {
                    binding.registerEmail.visible()
                    refreshTop(R.string._1_4, R.string.please_enter_your_email)
                    showInput(binding.registerEmail)
                }

                "google" -> {
                    binding.registerName.visible()
                    refreshTop(R.string._1_2, R.string.what_is_your_name)
                    showInput(binding.registerName)
                }
            }
        }
        googleClient = GoogleSignIn.getClient(
            this, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        )

        binding.registerBtn.setOnSingleClick {
            if (login == "login") {
                val email = binding.registerEmail.text.toString()
                val pwd = binding.registerPwd.text.toString()
                if (! confirmEmailLegal(email) || email.isBlank()) {
                    getString(R.string.please_enter_the_correct_email).toastShort
                    return@setOnSingleClick
                }
                if (pwd.length < 6 || pwd.length > 12) {
                    getString(R.string.pwd_hint).toastShort
                    return@setOnSingleClick
                }
                showLoading()
                viewModel.emailLogin(email, pwd)
            } else {
                when (step) {
                    1 -> {
                        if (login == "register") {
                            val email = binding.registerEmail.text.toString()
                            if (! confirmEmailLegal(email) || email.isBlank()) {
                                getString(R.string.please_enter_the_correct_email).toastShort
                                return@setOnSingleClick
                            }
                            binding.registerEmail.gone()
                            binding.registerPwd.visible()
                            showInput(binding.registerPwd)
                            refreshTop(R.string._2_4, R.string.set_your_password)
                        } else {
                            val name = binding.registerName.text.toString()
                            if (name.isBlank()) {
                                getString(R.string.please_enter_your_nickname).toastShort
                                return@setOnSingleClick
                            }
                            binding.registerName.gone()
                            binding.registerDate.visible()
                            refreshTop(R.string._2_2, R.string.choose_your_birthday)
                        }
                    }

                    2 -> {
                        if (login == "register") {
                            val pwd = binding.registerPwd.text.toString()
                            if (pwd.length < 6 || pwd.length > 12) {
                                getString(R.string.pwd_hint).toastShort
                                return@setOnSingleClick
                            }
                            binding.registerPwd.gone()
                            binding.registerName.visible()
                            showInput(binding.registerName)
                            refreshTop(R.string._3_4, R.string.what_is_your_name)
                        } else {
                            val userName = binding.registerName.text.toString()
                            if (isAdult() < 18) {
                                getString(R.string.age_hint).toastShort
                                return@setOnSingleClick
                            }
                            showLoading()
                            viewModel.login(AIP.googleId, userName, age.toString(),1)
                            return@setOnSingleClick
                        }
                    }

                    3 -> {
                        val name = binding.registerName.text.toString()
                        if (name.isBlank()) {
                            getString(R.string.please_enter_your_nickname).toastShort
                            return@setOnSingleClick
                        }
                        binding.registerName.gone()
                        binding.registerDate.visible()
                        refreshTop(R.string._4_4, R.string.choose_your_birthday)
                    }

                    4 -> {
                        if (isAdult() < 18) {
                            getString(R.string.age_hint).toastShort
                            return@setOnSingleClick
                        }
                        showLoading()
                        viewModel.emailLogin(
                            binding.registerEmail.text.toString(),
                            binding.registerPwd.text.toString(),
                            binding.registerName.text.toString(),
                            age.toString(),
                            1
                        )
                    }
                }
                step ++
            }
        }

        viewModel.emailLoginData.observe(this) { login ->
            dismissLoading()
            when (login.error_code) {
                HttpCode.SUCCESS.code -> {
                    login.data.let {
                        AIP.token = it.api_token
                        AIP.isLogin = true
                        AIP.userId = it.id
                        AIP.role = it.role
                        AIP.balance = it.balance.toString()
                        AIP.userEmail = binding.registerEmail.text.toString()
                        it.profile.let { profile ->
                            AIP.userName = profile.nickname
                            AIP.gender = profile.gender
                            if (profile.head.isNotBlank()) AIP.head = profile.head
                        }
                        if (this.login == "login"){
                            SolarUtil.login("email",1)
                        }else{
                            SolarUtil.register("email",1)
                        }
                        RxBus.post(RxEvents.LoginSuccess("1"))
                        if (it.api_token.isNotBlank()) {
                            if (it.role != 2) {
                                AIP.isGoogle = false
                                launch(MainActivity::class.java)
                                this.finish()
                            } else {
                                getString(R.string.sorry_only_users_can_log_in).toastShort
                            }
                        }
                    }
                }

                HttpCode.UNREGISTER.code -> {
                    if (this.login == "login"){
                        SolarUtil.login("email",2)
                    }else{
                        SolarUtil.register("email",2)
                    }
                    getString(R.string.user_not_registered).toastShort
                    this.finish()
                    launch(RegisterActivity::class.java, Bundle().apply {
                        putString("login", "register")
                    })
                }

                HttpCode.EMAIL_USE.code -> {
                    if (this.login == "login"){
                        SolarUtil.login("email",2)
                    }else{
                        SolarUtil.register("email",2)
                    }
                    getString(R.string.the_email_is_already_occupied).toastShort
                }

                HttpCode.ACCOUNT_ERROR.code -> {
                    if (this.login == "login"){
                        SolarUtil.login("email",2)
                    }else{
                        SolarUtil.register("email",2)
                    }
                    getString(R.string.account_or_password_error).toastShort
                }

                HttpCode.ACCOUNT_FROZEN.code -> {
                    if (this.login == "login"){
                        SolarUtil.login("email",2)
                    }else{
                        SolarUtil.register("email",2)
                    }
                    getString(R.string.the_user_or_device_has_been_frozen).toastShort
                }

                HttpCode.MORE_REGISTER.code -> {
                    if (this.login == "login"){
                        SolarUtil.login("email",2)
                    }else{
                        SolarUtil.register("email",2)
                    }
                    getString(R.string.too_many_users_registered).toastShort
                }

                else -> {
                    if (this.login == "login"){
                        SolarUtil.login("email",2)
                    }else{
                        SolarUtil.register("email",2)
                    }
                    netToast()
                }
            }
        }

        binding.wheelYear.yearStart = nowDate().first - 99
        binding.wheelYear.yearEnd = nowDate().first
        binding.wheelYear.post {
            binding.wheelYear.selectedItemPosition = 20
        }
        binding.wheelMonth.data = monthList
        binding.wheelMonth.post {
            binding.wheelMonth.selectedItemPosition = nowDate().second
        }
        binding.wheelDay.setYearAndMonth(
            binding.wheelYear.currentYear,
            binding.wheelMonth.currentItemPosition + 1
        )
        binding.wheelDay.selectedDay = nowDate().third

        binding.wheelYear.setOnItemSelectedListener { _, data, _ ->
            year = data as Int
        }

        binding.wheelMonth.setOnItemSelectedListener { _, _, position ->
            month = position
        }

        binding.wheelDay.setOnItemSelectedListener { _, _, position ->
            day = position + 1
        }

        viewModel.loginData.observe(this) { login ->
            dismissLoading()
            if (login.error_code == HttpCode.SUCCESS.code) {
                login.data.let {
                    AIP.token = it.api_token
                    AIP.isLogin = true
                    AIP.userId = it.id
                    AIP.role = it.role
                    AIP.balance = it.balance.toString()
                    AIP.userEmail = it.email ?: ""
                    it.profile.let { profile ->
                        AIP.userName = profile.nickname
                        AIP.gender = profile.gender
                        if (profile.head.isNotBlank()) AIP.head = profile.head
                    }
                    SolarUtil.register("google",1)
                    RxBus.post(RxEvents.LoginSuccess("1"))
                    if (it.api_token.isNotBlank()) {
                        if (it.role != 2) {
                            AIP.isGoogle = true
                            launch(MainActivity::class.java)
                            this.finish()
                        } else {
                            getString(R.string.sorry_only_users_can_log_in).toastShort
                        }
                    }
                }
            } else {
                login.message?.toastShort
                SolarUtil.register("google",2)
            }
        }

        binding.back.setOnSingleClick {
            if (login == "login") this.finish()
            else backStep()
            step --
        }

        SoftKeyBoardListener(this).onSoftKeyBoardListener {
            val lp = binding.registerBtn.layoutParams as LinearLayoutCompat.LayoutParams
            if (it>0){
                lp.bottomMargin = it
            }else{
                lp.bottomMargin = 20
            }
            binding.registerBtn.layoutParams = lp
        }

    }

    /**
     * 显示键盘
     *
     * @param et 输入焦点
     */
    private fun showInput(et : AppCompatEditText) {
        et.requestFocus()
    }

    private fun backStep() {
        when (step) {
            1 -> this.finish()
            2 -> {
                if (login == "register") {
                    binding.registerPwd.gone()
                    binding.registerPwd.setText("")
                    binding.registerEmail.visible()
                    showInput(binding.registerEmail)
                    refreshTop(R.string._1_4, R.string.please_enter_your_email)
                } else {
                    binding.registerDate.gone()
                    binding.registerName.visible()
                    showInput(binding.registerName)
                    refreshTop(R.string._1_2, R.string.what_is_your_name)
                }
            }

            3 -> {
                binding.registerName.gone()
                binding.registerPwd.visible()
                showInput(binding.registerPwd)
                refreshTop(R.string._2_4, R.string.set_your_password)
            }

            4 -> {
                binding.registerDate.gone()
                binding.registerName.visible()
                showInput(binding.registerName)
                refreshTop(R.string._3_4, R.string.what_is_your_name)
            }
        }
    }

    private fun refreshTop(top : Int = 0, hint : Int) {
        if (top == 0) binding.registerTop.gone() else binding.registerTop.text = getString(top)
        binding.registerHint.text = getString(hint)
    }

    private fun isAdult() : Int {
        if (year == 0) year = nowDate().first - 19
        val birthCalendar = Calendar.getInstance()
        birthCalendar.set(year, month, day)
        val currentCalendar = Calendar.getInstance()
        age = currentCalendar.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)
        if (currentCalendar.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
            age --
        }
        return age
    }

    /**
     * 验证邮箱是否合法
     */
    private fun confirmEmailLegal(email : String) : Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

}