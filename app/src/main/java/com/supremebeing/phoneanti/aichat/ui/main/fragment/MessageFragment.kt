package com.supremebeing.phoneanti.aichat.ui.main.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.format.DateUtils
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.LogUtils
import com.supremebeing.phoneanti.aichat.R
import com.supremebeing.phoneanti.aichat.adapter.UserAdapter
import com.supremebeing.phoneanti.aichat.base.ArchFragment
import com.supremebeing.phoneanti.aichat.bean.MessageMoreBean
import com.supremebeing.phoneanti.aichat.databinding.FragmentMessageBinding
import com.supremebeing.phoneanti.aichat.event.RxBus
import com.supremebeing.phoneanti.aichat.event.RxEvents
import com.supremebeing.phoneanti.aichat.network.server.HttpCode
import com.supremebeing.phoneanti.aichat.sql.AppDBHelp
import com.supremebeing.phoneanti.aichat.sql.getAllMessage
import com.supremebeing.phoneanti.aichat.tool.AIP
import com.supremebeing.phoneanti.aichat.tool.gone
import com.supremebeing.phoneanti.aichat.tool.setOnSingleClick
import com.supremebeing.phoneanti.aichat.tool.toastShort
import com.supremebeing.phoneanti.aichat.tool.visible
import com.supremebeing.phoneanti.aichat.ui.chat.ChatActivity
import com.supremebeing.phoneanti.aichat.ui.help.LikeActivity
import com.supremebeing.phoneanti.aichat.ui.help.StoreActivity
import com.supremebeing.phoneanti.aichat.ui.main.MainActivity
import com.supremebeing.phoneanti.aichat.ui.main.vm.MainViewModel
import com.supremebeing.phoneanti.aichat.utils.AdUtil
import com.supremebeing.phoneanti.aichat.utils.netToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MessageFragment : ArchFragment<MainViewModel, FragmentMessageBinding>() {
    companion object {
        fun newInstance() = MessageFragment().apply {
            arguments = Bundle()
        }
    }

    private lateinit var userAdapter : UserAdapter
    private var chatList = mutableListOf<MessageMoreBean>()

    override fun initView() {
        observer()
        binding.messageFavorite.setOnSingleClick {
            launch(LikeActivity::class.java)
        }

        userAdapter = UserAdapter(chatList)
        binding.recyclerViewMessage.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewMessage.adapter = userAdapter

        refreshMessage()

        userAdapter.setOnItemClickListener { adapter, _, position ->
            val data = adapter.data[position] as MessageMoreBean
            launch(ChatActivity::class.java, Bundle().apply {
                putString("cover", data.cover)
                putInt("id", data.aiId)
            })
            AIP.aiName = data.name ?: getString(R.string.unknown)
        }

        binding.messageGo.setOnSingleClick {
            (activity as MainActivity).switchFragment(1)
        }

        binding.messageCoin.setOnSingleClick {
            launch(StoreActivity::class.java)
        }

        binding.messageMore.setOnSingleClick {
            AdUtil.showAd(requireActivity()) {
                showLoading()
                viewModel.getReward()
                binding.messageMore.gone()
            }
        }

        viewModel.rightApplyData.observe(this) {
            dismissLoading()
            if (it.error_code == HttpCode.SUCCESS.code) {
                if (it.data != null){
                    if (it.data.token != null){
                        getString(R.string.congratulations, it.data.token).toastShort
                    }
                }
            } else {
                netToast()
            }
        }

        lifecycleScope.launch {
            while (isActive){
                checkReward()
                delay(DateUtils.MINUTE_IN_MILLIS)
            }
        }
    }

    private fun observer() {
        //更新金币余额
        subscribeEvent(RxBus.receive(RxEvents.BalanceUpdate::class.java).subscribe {
            binding.messageCoin.text = AIP.balance
        })

        //更新消息列表
        subscribeEvent(RxBus.receive(RxEvents.RefreshMessageList::class.java).subscribe {
            refreshMessage()
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refreshMessage() {
//        userList.clear()
        lifecycleScope.launch {
            val list = AppDBHelp.getUserInfo()
            chatList.clear()
            if (list.size>0){
                list.reverse()
                list.forEach {
                    val allList = getAllMessage(AIP.userId, it.userId)
                    if (allList.size>0){
                        chatList.add(
                            MessageMoreBean(
                                it.userId,
                                it.userCover,
                                if (allList.last().isComing == 1) allList.last().content else if (allList.size >= 2) allList[allList.size - 2].content else "",
                                it.headImage,
                                it.nickName,
                                it.read
                            )
                        )
                    }
                }
            }
//            for (i in 0 until list.size){
//                if (list[i].role== UserCode.SUPPORT.code){
//                    list.add(0,list[i])
//                    list.removeAt(i+1)
//                }
//            }
            withContext(Dispatchers.Main) {
//                userList.addAll(list)
                if (chatList.size == 0) {
                    binding.messageGo.visible()
                    binding.recyclerViewMessage.gone()
                } else {
                    binding.messageGo.gone()
                    binding.recyclerViewMessage.visible()
                }
                userAdapter.notifyDataSetChanged()
            }
            RxBus.post(RxEvents.ShowDot())
        }
    }

    private fun checkReward() {
        if (! AIP.showAd) return
        if (AIP.adCount >= AIP.maxAdCount) return
        if (System.currentTimeMillis() - AIP.lastAdTime < AIP.adInternal * DateUtils.MINUTE_IN_MILLIS) return

        AdUtil.loadAd(requireContext()) {
            binding.messageMore.visible()
        }
    }

    override fun onResume() {
        super.onResume()
        refreshSomeData()
    }

    private fun refreshSomeData() {
        binding.messageCoin.text = AIP.balance
    }

    override fun onHiddenChanged(hidden : Boolean) {
        super.onHiddenChanged(hidden)
        if (! hidden) {
            refreshSomeData()
        }
    }

}