package com.supremebeing.phoneanti.aichat.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.format.DateUtils
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import com.google.gson.Gson
import com.onesignal.OSDeviceState
import com.onesignal.OneSignal
import com.supremebeing.phoneanti.aichat.MessageBar
import com.supremebeing.phoneanti.aichat.R
import com.supremebeing.phoneanti.aichat.base.ArchActivity
import com.supremebeing.phoneanti.aichat.bean.BabyEquityBean
import com.supremebeing.phoneanti.aichat.bean.BabyOrderTicketModel
import com.supremebeing.phoneanti.aichat.databinding.ActivityMainBinding
import com.supremebeing.phoneanti.aichat.dialog.FirstRechargeDialog
import com.supremebeing.phoneanti.aichat.event.RxBus
import com.supremebeing.phoneanti.aichat.event.RxEvents
import com.supremebeing.phoneanti.aichat.network.server.HttpCode
import com.supremebeing.phoneanti.aichat.network.server.RightsCode
import com.supremebeing.phoneanti.aichat.socket.BindSocket
import com.supremebeing.phoneanti.aichat.sql.AppDBHelp
import com.supremebeing.phoneanti.aichat.tool.AIP
import com.supremebeing.phoneanti.aichat.tool.gone
import com.supremebeing.phoneanti.aichat.tool.setOnSingleClick
import com.supremebeing.phoneanti.aichat.tool.visible
import com.supremebeing.phoneanti.aichat.ui.chat.ChatActivity
import com.supremebeing.phoneanti.aichat.ui.main.fragment.CardFragment
import com.supremebeing.phoneanti.aichat.ui.main.fragment.MessageFragment
import com.supremebeing.phoneanti.aichat.ui.main.fragment.PersonFragment
import com.supremebeing.phoneanti.aichat.ui.main.vm.MainViewModel
import com.supremebeing.phoneanti.aichat.utils.ActivityUtil
import com.supremebeing.phoneanti.aichat.utils.FacebookEventUtil
import com.supremebeing.phoneanti.aichat.utils.NotifyUtil
import com.supremebeing.phoneanti.aichat.utils.OrderController
import com.supremebeing.phoneanti.aichat.utils.PayController
import com.supremebeing.phoneanti.aichat.utils.SolarUtil
import com.supremebeing.phoneanti.aichat.utils.netToast
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.Timer
import java.util.TimerTask

class MainActivity : ArchActivity<MainViewModel,ActivityMainBinding>() {
    private val fragmentList = ArrayList<Fragment>()
    private var currentFragment : Fragment? = null
    private var fragmentPosition = 0
    private lateinit var firstRechargeDialog : FirstRechargeDialog
    private var isShow = false
    private lateinit var payController : PayController
    private var orderTimer: Timer? = null
    private var anchorId = 0
    private var anchorName : String? = null
    private var price = 0.0
    private var handler: Handler = object : Handler(Looper.getMainLooper()) {
        @SuppressLint("HandlerLeak")
        override fun handleMessage(@SuppressLint("HandlerLeak") msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                1 -> {
                    reCommit()//检测订单重新提交
                }
            }
        }
    }
    override fun initView() {
        firstRechargeDialog = FirstRechargeDialog(this)
        BindSocket.initSocket(this)
        viewModel.init()
        userRights()
        initFragment()

        payController = PayController()

        subscribeEvent(RxBus.receive(RxEvents.BindSocket::class.java).subscribe {
            val device = OneSignal.getDeviceState() as OSDeviceState
            viewModel.bindSocket(
                it.clientId,
                if (device.userId.isNullOrBlank()) "" else device.userId,
                if (device.pushToken.isNullOrBlank()) "" else device.pushToken
            )
        })

        viewModel.bindData.observe(this){
            //绑定成功
        }

        viewModel.initData.observe(this){
            if (it.error_code == HttpCode.SUCCESS.code){
                if (it.data.contact_url != null){
                    AIP.contactUrl = it.data.contact_url//联系链接
                }
                it.data.ad?.let {ad->
                    AIP.showAd = ad.enabled
                    AIP.maxAdCount = ad.max_count
                    AIP.adInternal = ad.interval_minute
                    if (!DateUtils.isToday(AIP.lastAdTime)){
                        AIP.adCount = 0
                    }
                }
            }
        }

        //用户权益刷新
        subscribeEvent(RxBus.receive(RxEvents.RefreshAllRights::class.java).subscribe {
            userRights()
        })

        //消息通知
        subscribeEvent(RxBus.receive(RxEvents.ShowNotify::class.java).subscribe{
            if (fragmentPosition != 0 && ActivityUtil.currentActivityName != ChatActivity::class.java.name){
                NotifyUtil.showNotify(this,it.notifyBean)
            }
            checkMessage()
        })

        //显示消息红点
        subscribeEvent(RxBus.receive(RxEvents.ShowDot::class.java).subscribe {
            checkMessage()
        })

        binding.mainFirst.setOnSingleClick {
            firstRechargeDialog.showDialog(true)
        }

        firstRechargeDialog.getListener = {_,code->
            payController.payDollar(this,code,1,price)
        }

        payController.setOnListener {  type,purchase, code ,purchaseOrderId,price->
            val json = JSONObject()
            val receiptJson = JSONObject()
            json.put("code", code)
            receiptJson.put("purchaseToken", purchase.purchaseToken)
            receiptJson.put("order_id",purchaseOrderId)
            json.put("receipt", receiptJson)
            OrderController.saveRechargeSmallTicketData(2, code, json.toString(),price,type)
            viewModel.recharge(json, 2, code, saveEvent = true, isRepeatOrder = false)
        }

        payController.setOnHistoryListener {  type,purchase, code, purchaseOrderId,price ->
            val json = JSONObject()
            val receiptJson = JSONObject()
            json.put("code", code)
            receiptJson.put("purchaseToken", purchase.purchaseToken)
            receiptJson.put("order_id",purchaseOrderId)
            json.put("receipt", receiptJson)
            OrderController.saveRechargeSmallTicketData(2, code, json.toString(),price,type)
            viewModel.recharge(json, 2, code, saveEvent = true, isRepeatOrder = false)
        }

        //首充奖励
        viewModel.rechargeData.observe(this){
            if (it.error_code == HttpCode.SUCCESS.code){
                if (it.gid == 2){
                    if (!it.isRepeat){
                        isShow = false
                        binding.mainFirst.gone()
                    }
                    OrderController.dropCommitOrder(it.receipt, it.code, it.gid)
                }
            }else{
                netToast()
            }
        }
    }

    private fun initFragment(){
        val tabList = ArrayList<CustomTabEntity>().apply {
            add(TabEntity("", R.drawable.m1c,R.drawable.m1))
            add(TabEntity("",R.drawable.m2c,R.drawable.m2))
            add(TabEntity("",R.drawable.m3c,R.drawable.m3))
        }

        binding.mainTab.setTabData(tabList)
        binding.mainTab.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                switchFragment(position)
            }
            override fun onTabReselect(position: Int) {}
        })

        fragmentList.add(MessageFragment.newInstance())
        fragmentList.add(CardFragment.newInstance())
        fragmentList.add(PersonFragment.newInstance())
        switchFragment(0)
        switchFragment(1)
    }


    inner class TabEntity(
        private val title : String, private val selectIcon : Int, private val unSelectIcon : Int
    ) : CustomTabEntity {
        override fun getTabTitle(): String = title

        override fun getTabSelectedIcon(): Int = selectIcon

        override fun getTabUnselectedIcon(): Int = unSelectIcon

    }

    /**
     * 切换碎片（内部调用）
     */
    private fun switchFragment(targetFragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()

        if (!targetFragment.isAdded) {
            if (currentFragment != null) {
                transaction.hide(currentFragment!!)
            }
            transaction.add(R.id.main_content, targetFragment, targetFragment.javaClass.name)
            transaction.commitAllowingStateLoss()
        } else {
            transaction.hide(currentFragment!!).show(targetFragment).commitAllowingStateLoss()
        }
        currentFragment = targetFragment
    }

    /**
     * 切换碎片（外部调用）
     */
    fun switchFragment(index: Int) {
        fragmentPosition = index
        val iconList = arrayListOf<View>().apply {
            add(binding.icon1)
            add(binding.icon2)
            add(binding.icon3)
        }
        switchFragment(fragmentList[index])
        binding.mainTab.currentTab = index
        for (i in iconList.indices){
            if (index == i){
                iconList[i].visible()
            }else{
                iconList[i].gone()
            }
        }

        if (index == 2){
            binding.mainFirst.gone()
        }else{
            if (isShow){
                binding.mainFirst.visible()
            }else{
                binding.mainFirst.gone()
            }
        }
    }

    /**
     * 用户权益
     */
    @SuppressLint("SetTextI18n")
    private fun userRights(){
        val rightsList = MessageBar.getRights()
        if (rightsList.isNotEmpty()){
            val currentTime = System.currentTimeMillis() / 1000
            var tokens : BabyEquityBean.Data.Rights? = null
            val vipList = mutableListOf<Int>()
            rightsList.forEach { rightsBean ->
                if (rightsBean.right!=null){
                    if (rightsBean.right.code == RightsCode.FIRSTTOKEN.code){
                        if (currentTime >= rightsBean.available_at && currentTime < rightsBean.expired_at) {
                            if (!rightsBean.isIs_used){
                                tokens = rightsBean
                            }
                        }
                    }

                    if (rightsBean.right.code == RightsCode.VIPDRAWTOKEN.code){
                        if (currentTime > rightsBean.available_at && currentTime < rightsBean.expired_at) {
                            if (!rightsBean.isIs_used){
                                vipList.add(1)
                            }
                        }
                    }
                }
            }

            if (tokens!=null){
                firstRechargeDialog.setData(tokens!!)
                if (!tokens?.isIs_used!!){
                    isShow = true
                    binding.mainFistCoin.text="$${tokens?.right?.desc?.price}GET"
                    tokens?.right?.desc?.price?.let {
                        price = it.toDouble()
                    }
//                    binding.mainFirst.visible()
                }
            }else{
                isShow = false
//                binding.mainFirst.gone()
            }
        }else{
            isShow = false
        }
        currentFragment?.let {
            switchFragment(it)
        }

    }

    /**
     * 检查是否有消息
     */
    private fun checkMessage(){
        lifecycleScope.launch {
            val list = AppDBHelp.getUserInfo()
            if (list.size == 0) return@launch
            var readNum = 0
            list.forEach {
                val chatList = AppDBHelp.getChatMessages(AIP.userId,it.userId)
                if (chatList != null && chatList.size != 0){
                    readNum += it.read
                }
            }
            if (readNum>0){
                binding.mainTab.showMsg(0,readNum)
            }else{
                binding.mainTab.hideMsg(0)
            }
        }
    }

    fun getBarHeight() = getNavigationBarHeight()

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode== KeyEvent.KEYCODE_BACK){
            moveTaskToBack(true)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onResume() {
        super.onResume()
        orderTimer = Timer()
        orderTimer!!.schedule(object : TimerTask() {
            override fun run() {
                handler.sendEmptyMessage(1)
            }
        }, 1000, 5000)
    }

    override fun onPause() {
        super.onPause()
        if (orderTimer != null) {
            orderTimer!!.cancel()
            orderTimer = null
        }
    }

    override fun onNewIntent(intent : Intent?) {
        super.onNewIntent(intent)
        intent?.extras?.getInt("id")?.let {
            anchorId = it
        }

        intent?.extras?.getString("name")?.let {
            anchorName = it
        }

        if (anchorId>0){
            launch(ChatActivity::class.java, Bundle().apply {
                putInt("id",anchorId)
            })
            AIP.aiName = anchorName ?: getString(R.string.unknown)
        }
    }

    /**
     * 掉单
     * 重新提交支付订单
     */
    private fun reCommit() {
        val rechargeSmallTicket = AIP.rechargeTicket
        val rechargeSmallTicketBean: BabyOrderTicketModel
        if (rechargeSmallTicket.isNotBlank()) {
            rechargeSmallTicketBean = Gson().fromJson(
                rechargeSmallTicket,
                BabyOrderTicketModel::class.java
            )

            if (!rechargeSmallTicketBean.smallTicket.isNullOrEmpty()) {
                for (smallTicket in rechargeSmallTicketBean.smallTicket) {
                    val userId = smallTicket.user_id
                    if (userId ==AIP.userId) {
                        val json = JSONObject(smallTicket.receipt)
//                        FacebookEventUtil.orderEvent(smallTicket.price,smallTicket.priceType)
//                        SolarUtil.payOrder(smallTicket.price,1,smallTicket.priceType)
                        viewModel.recharge(json, 2, smallTicket.code,
                            saveEvent = false,
                            isRepeatOrder = true
                        )
                        break
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        BindSocket.unBindService(this)
    }
}