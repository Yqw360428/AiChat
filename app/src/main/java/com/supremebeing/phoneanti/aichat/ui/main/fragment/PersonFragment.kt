package com.supremebeing.phoneanti.aichat.ui.main.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import com.blankj.utilcode.util.LogUtils
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.supremebeing.phoneanti.aichat.BuildConfig
import com.supremebeing.phoneanti.aichat.R
import com.supremebeing.phoneanti.aichat.base.ArchFragment
import com.supremebeing.phoneanti.aichat.databinding.FragmentPersonBinding
import com.supremebeing.phoneanti.aichat.dialog.AccountDialog
import com.supremebeing.phoneanti.aichat.dialog.SignDialog
import com.supremebeing.phoneanti.aichat.event.RxBus
import com.supremebeing.phoneanti.aichat.event.RxEvents
import com.supremebeing.phoneanti.aichat.network.server.HttpCode
import com.supremebeing.phoneanti.aichat.tool.AIP
import com.supremebeing.phoneanti.aichat.tool.isFastClick
import com.supremebeing.phoneanti.aichat.tool.setOnSingleClick
import com.supremebeing.phoneanti.aichat.tool.toastShort
import com.supremebeing.phoneanti.aichat.ui.help.CardActivity
import com.supremebeing.phoneanti.aichat.ui.help.FeedBackActivity
import com.supremebeing.phoneanti.aichat.ui.help.StoreActivity
import com.supremebeing.phoneanti.aichat.ui.help.VipActivity
import com.supremebeing.phoneanti.aichat.ui.help.WebActivity
import com.supremebeing.phoneanti.aichat.ui.main.vm.MainViewModel
import com.supremebeing.phoneanti.aichat.ui.start.LoginActivity
import com.supremebeing.phoneanti.aichat.utils.ActivityUtil
import com.supremebeing.phoneanti.aichat.utils.AdUtil
import com.supremebeing.phoneanti.aichat.utils.netToast

class PersonFragment : ArchFragment<MainViewModel, FragmentPersonBinding>() {
    companion object {
        fun newInstance() = PersonFragment().apply {
            arguments = Bundle()
        }
    }

    private var signList = mutableListOf<MultiItemEntity>()
    private lateinit var signDialog : SignDialog
    private lateinit var accountDialog : AccountDialog
    private var userName : String? = null
    private var isSigned = false

    private lateinit var googleSignInClient : GoogleSignInClient

    override fun initView() {
        signDialog = SignDialog(requireContext())
        accountDialog = AccountDialog(requireContext())
        googleSignInClient = GoogleSignIn.getClient(
            requireContext(), GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN
            ).requestEmail().build()
        )
        observer()

        binding.personSign.setOnSingleClick {
            showLoading()
            viewModel.getSign()
            AdUtil.loadAd(requireContext()){}
        }

        binding.personTop.setOnSingleClick {
            launch(VipActivity::class.java)
        }

        binding.personEmail.setOnSingleClick {
            accountDialog.showDialog(true)
        }

        binding.personName.setOnEditorActionListener { _, actionId, _ ->
            val userName = binding.personName.text.toString()
            if (actionId == EditorInfo.IME_ACTION_SEND){
                if (isFastClick()){
                    if (userName.isNotBlank()){
                        showLoading()
                        this.userName = userName
                        viewModel.updateUser(userName)
                    }
                }else{
                    R.string.send_too_fast.toastShort
                }
            }
            false
        }

        binding.personContact.setOnSingleClick {
            if (AIP.contactUrl.isNotBlank()){
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(AIP.contactUrl))
                if (intent.resolveActivity(requireContext().packageManager) != null){
                    runCatching {
                        startActivity(intent)
                    }.onFailure {
                        R.string.no_available_browser_applications.toastShort
                    }
                }else{
                    R.string.no_available_browser_applications.toastShort
                }
            }else{
                netToast()
            }
        }

        binding.personFeed.setOnSingleClick {
            launch(FeedBackActivity::class.java)
        }

        binding.personTerms.setOnSingleClick {
            launch(WebActivity::class.java,Bundle().apply {
                putInt("type",1)
            })
        }

        binding.personPrivacy.setOnSingleClick {
            launch(WebActivity::class.java,Bundle().apply {
                putInt("type",2)
            })
        }

        binding.personCoin.setOnSingleClick {
            launch(StoreActivity::class.java)
        }

        binding.personCard.setOnSingleClick {
            launch(CardActivity::class.java)
        }
    }

    private fun observer() {
        //获取签到数据
        viewModel.signData.observe(this) {
            dismissLoading()
            if (it.error_code == HttpCode.SUCCESS.code) {
                it.data.checkins?.last()?.itemType = 2
                if (it.data.checkins != null) {
                    signList.clear()
                    signList.addAll(it.data.checkins)
                    signDialog.setData(signList)
                    signDialog.showDialog(true)
                }
                isSigned = it.data.today_checked
            } else {
                netToast()
            }
        }

        //签到完成
        viewModel.dailySignData.observe(this) {
            dismissLoading()
            if (it.error_code == HttpCode.SUCCESS.code) {
                getString(R.string.successfully_signed_in).toastShort
                signDialog.dismiss()
                binding.personCoin.text = AIP.balance
            } else {
                netToast()
            }
        }

        //显示余额
        subscribeEvent(RxBus.receive(RxEvents.BalanceUpdate::class.java).subscribe {
            binding.personCoin.text = AIP.balance
        })

        //点击签到
        signDialog.signListener = {
            if (isSigned){
                getString(R.string.checke_ined).toastShort
            }else{
                showLoading()
                if (it){
                    AdUtil.showAd(requireActivity()){
                        //签到
                        viewModel.dailyCheck(true)
                    }
                }else{
                    //签到
                    viewModel.dailyCheck(false)
                }
            }
        }

        //退出登录
        viewModel.signOutData.observe(this){
            dismissLoading()
            if (it.error_code == HttpCode.SUCCESS.code){
                AIP.token = ""
                AIP.isLogin = false
                AIP.userId = 0
                AIP.role = 0
                AIP.balance = ""
                AIP.userEmail = ""
                AIP.userName = ""
                AIP.gender = 0
                AIP.head = ""
                AIP.messageTime = 0L
                ActivityUtil.remoAllActivity()
                launch(LoginActivity::class.java)
            }
        }

        //删除账号
        viewModel.deleteAccountData.observe(this){
            dismissLoading()
            if (it.error_code == HttpCode.SUCCESS.code){
                AIP.token = ""
                AIP.isLogin = false
                AIP.userId = 0
                AIP.role = 0
                AIP.balance = ""
                AIP.userEmail = ""
                AIP.userName = ""
                AIP.gender = 0
                AIP.head = ""
                AIP.messageTime = 0L
//                revokeAccess()
                ActivityUtil.remoAllActivity()
                launch(LoginActivity::class.java)
            }
        }

        //退出和删除账号
        accountDialog.onListener {type->
//            val account = GoogleSignIn.getLastSignedInAccount(requireContext())
//            if (account != null){
//
//            }else{
//                getString(R.string.exit_hint).toastShort
//            }
            showLoading()
            if (type){
                if (AIP.isGoogle){
                    googleSignInClient.signOut().addOnCompleteListener {
                        viewModel.deleteAccount()
                    }
                }else{
                    viewModel.deleteAccount()
                }
            }else{
                //退出登录
                if (AIP.isGoogle){
                    googleSignInClient.signOut().addOnCompleteListener {
                        viewModel.signOut()
                    }
                }else{
                    viewModel.signOut()
                }

            }
        }

        //用户信息更改
        viewModel.userData.observe(this){
            dismissLoading()
            if (it.error_code == HttpCode.SUCCESS.code){
                AIP.userName = userName!!
            }
        }

    }

//    private fun revokeAccess() {
//        googleSignInClient.revokeAccess()
//            .addOnCompleteListener(requireActivity()) {
//                LogUtils.d("=====>注销账号")
//            }
//    }

    override fun onHiddenChanged(hidden : Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden){
            binding.personCoin.text = AIP.balance
            binding.personId.text = AIP.userId.toString()
            binding.personEmailText.text = AIP.userEmail
            binding.personName.setText(AIP.userName)
            binding.personVersion.text = BuildConfig.VERSION_NAME
        }
    }

    override fun onResume() {
        super.onResume()
        binding.personCoin.text = AIP.balance
        binding.personId.text = AIP.userId.toString()
        binding.personEmailText.text = AIP.userEmail
        binding.personName.setText(AIP.userName)
        binding.personVersion.text = BuildConfig.VERSION_NAME
    }
}