package com.supremebeing.phoneanti.aichat.ui.help

import android.annotation.SuppressLint
import android.text.format.DateUtils
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.utilcode.util.LogUtils
import com.supremebeing.phoneanti.aichat.MessageBar
import com.supremebeing.phoneanti.aichat.R
import com.supremebeing.phoneanti.aichat.adapter.RechargeAdapter
import com.supremebeing.phoneanti.aichat.base.ArchActivity
import com.supremebeing.phoneanti.aichat.bean.BabyEquityBean
import com.supremebeing.phoneanti.aichat.bean.TokensBean
import com.supremebeing.phoneanti.aichat.databinding.ActivityStoreBinding
import com.supremebeing.phoneanti.aichat.event.RxBus
import com.supremebeing.phoneanti.aichat.event.RxEvents
import com.supremebeing.phoneanti.aichat.network.server.HttpCode
import com.supremebeing.phoneanti.aichat.network.server.RightsCode
import com.supremebeing.phoneanti.aichat.tool.AIP
import com.supremebeing.phoneanti.aichat.tool.gone
import com.supremebeing.phoneanti.aichat.tool.setOnSingleClick
import com.supremebeing.phoneanti.aichat.tool.toastShort
import com.supremebeing.phoneanti.aichat.tool.visible
import com.supremebeing.phoneanti.aichat.ui.help.vm.HelpViewModel
import com.supremebeing.phoneanti.aichat.utils.AdUtil
import com.supremebeing.phoneanti.aichat.utils.FacebookEventUtil
import com.supremebeing.phoneanti.aichat.utils.OrderController
import com.supremebeing.phoneanti.aichat.utils.PayController
import com.supremebeing.phoneanti.aichat.utils.SolarUtil
import com.supremebeing.phoneanti.aichat.utils.netToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.json.JSONObject

class StoreActivity : ArchActivity<HelpViewModel,ActivityStoreBinding>() {
    private var tokensList = mutableListOf<TokensBean.Data.Stoken.Item>()
    private lateinit var rechargeAdapter : RechargeAdapter
    private var code:String?=null
    private lateinit var payController : PayController
    private var gid: Int = 0
    private var firstPrice = 0.0
    private var price = 0.0

    @SuppressLint("NotifyDataSetChanged")
    override fun initView() {
        payController = PayController()

        binding.storeClose.setOnSingleClick {
            this.finish()
        }

        lifecycleScope.launch {
            while (isActive){
                checkReward()
                delay(DateUtils.MINUTE_IN_MILLIS)
            }
        }

        //更新金币余额
        subscribeEvent(RxBus.receive(RxEvents.BalanceUpdate::class.java).subscribe {
            binding.storeCoin.text = AIP.balance
        })

        rechargeAdapter = RechargeAdapter(tokensList)
        binding.recyclerViewStore.layoutManager = GridLayoutManager(this,3)
        binding.recyclerViewStore.adapter = rechargeAdapter

        showLoading()
        viewModel.tokensList()

        viewModel.tokensData.observe(this){
            dismissLoading()
            if (it.error_code == HttpCode.SUCCESS.code){
                tokensList.clear()
                val tokens=it.data.tokens!!
                tokensList.addAll(tokens[0].items)
                rechargeAdapter.notifyDataSetChanged()
            }
        }
        userRights()

        binding.storeWatch.setOnSingleClick {
            AdUtil.showAd(this){
                showLoading()
                viewModel.getReward()
                binding.storeWatch.gone()
            }
        }

        viewModel.rightApplyData.observe(this){
            dismissLoading()
            if (it.error_code == HttpCode.SUCCESS.code){
                if (it.data != null){
                    if (it.data.token != null){
                        getString(R.string.congratulations,it.data.token).toastShort
                    }else{
                        getString(R.string.the_number_of_advertisements_today_has_been_used_up).toastShort
                    }
                }else{
                    getString(R.string.the_number_of_advertisements_today_has_been_used_up).toastShort
                }
            }else{
                netToast()
            }
        }

        viewModel.rightRechargeData.observe(this){
            dismissLoading()
        }

        viewModel.rechargeData.observe(this){
            if (it.error_code == HttpCode.SUCCESS.code){
                if (it.gid == 2){
                    //删除提交的成功后的订单
                    OrderController.dropCommitOrder(it.receipt,it.code,it.gid)
                }
            }
        }

        binding.rechargeTop.setOnSingleClick {
            //首充奖励
            code?.let {
                price  = firstPrice
                payController.payDollar(this,it,1,price)
            }
        }

        rechargeAdapter.setOnItemClickListener{adapter,_,position->
            val data = adapter.data[position] as TokensBean.Data.Stoken.Item
            data.price?.let {
                price = it.toDouble()
            }
            when(data.type){
                2->{
                    //vip充值
                }
                3->{
                    payController.payDollar(this, data.code,1,price)
                }
                else->{
                    if (data.price=="0.00" || data.price=="0" || data.price=="0.0"){
                        viewModel.rightApply(data.id,code)
                        showLoading()
                    }else{
                        payController.payDollar(this, data.code,1,price)
                    }
                }
            }
        }

        payController.setOnListener { type,purchase, code ,purchaseOrderId,price->
            val json = JSONObject()
            val receiptJson = JSONObject()
            json.put("code", code)
            receiptJson.put("purchaseToken", purchase.purchaseToken)
            receiptJson.put("order_id", purchaseOrderId)
            json.put("receipt", receiptJson)
            OrderController.saveRechargeSmallTicketData(2, code, json.toString(),price,type)
            viewModel.recharge(json, gid, code, true, isRepeatOrder = false,type,price)
        }

        payController.setOnHistoryListener { type,purchase, code ,purchaseOrderId,price->
            val json = JSONObject()
            val receiptJson = JSONObject()
            json.put("code",code)
            receiptJson.put("purchaseToken",purchase.purchaseToken)
            receiptJson.put("order_id", purchaseOrderId)
            json.put("receipt",receiptJson)
            OrderController.saveRechargeSmallTicketData(2, code, json.toString(),price,type)
            viewModel.recharge(json, gid, code, true, isRepeatOrder = false,type,price)
        }

        subscribeEvent(RxBus.receive(RxEvents.RefreshAllRights::class.java).subscribe {userRights()})
    }

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
                if (!tokens?.isIs_used!!){
                    code=tokens?.right?.desc?.token_code
                    binding.storeTopIcon.text=tokens?.right?.desc?.token_amount.toString()
                    binding.storePrice.text="$${tokens?.right?.desc?.price}"
                    tokens?.right?.desc?.price?.let {
                        firstPrice = it.toDouble()
                    }
                    binding.rechargeTop.visible()
                }else{
                    binding.rechargeTop.gone()
                }
            }else{
                binding.rechargeTop.gone()
            }
        }else{
            binding.rechargeTop.gone()
        }
    }

    private fun checkReward(){
        if (!AIP.showAd) return
        binding.storeCount.text = (AIP.maxAdCount - AIP.adCount).toString()
        if (AIP.adCount >= AIP.maxAdCount) return
        if (System.currentTimeMillis() - AIP.lastAdTime<AIP.adInternal * DateUtils.MINUTE_IN_MILLIS) return

        AdUtil.loadAd(this){
            binding.storeWatch.visible()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.storeCoin.text = AIP.balance
    }
}