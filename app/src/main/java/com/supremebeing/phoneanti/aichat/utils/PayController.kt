package com.supremebeing.phoneanti.aichat.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.SkuType
import com.blankj.utilcode.util.LogUtils
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PayController {
    private var purchasesUpdatedListener : PurchasesUpdatedListener? = null
    private var billingClient: BillingClient? = null
    //监听
    private var listener: ((type : String,purchase: Purchase, code: String, purchase_order_id: String,price : Double) -> Unit)? = null
    private var historyListener: ((type : String,purchase: Purchase, code: String, purchase_order_id: String,price : Double) -> Unit)? = null
    private var errorListener: ((errorCode: Int) -> Unit)? = null

    fun setOnListener(listener: ((type : String,purchase: Purchase, code: String, purchase_order_id: String,price : Double) -> Unit)) {
        this.listener = listener
    }

    fun setOnErrorListener(errorListener: ((errorCode: Int) -> Unit)) {
        this.errorListener = errorListener
    }

    fun setOnHistoryListener(historyListener: ((type : String,purchase: Purchase, code: String, purchase_order_id: String,price : Double) -> Unit)) {
        this.historyListener = historyListener
    }

    private var price = 0.0

    fun payDollar(activity: Activity, orderID: String, type: Int,price : Double){
        this.price = price
        purchasesUpdatedListener =
            PurchasesUpdatedListener { billingResult, purchases ->
                when(billingResult.responseCode){
                    BillingResponseCode.OK -> {
                        if (type==1){
                            if (purchases != null) {
                                for (purchase in purchases) {
                                    handlePurchase(purchase)
                                }
                            }
                        }else{
                            if (purchases != null) {
                                for (purchase in purchases) {
                                    handleSubscribePurchase(purchase)
                                }
                            }
                        }

                    }

                    BillingResponseCode.USER_CANCELED -> {
                        errorListener?.invoke(BillingResponseCode.USER_CANCELED)
                    }

                    BillingResponseCode.ITEM_ALREADY_OWNED -> {//未消耗掉商品
                        queryHistory()
                    }
                    BillingResponseCode.ERROR->{
                        errorListener?.invoke(BillingResponseCode.USER_CANCELED)
                    }
                }

            }

        billingClient = BillingClient.newBuilder(activity)
            .setListener(purchasesUpdatedListener!!)
            .enablePendingPurchases()
            .build()

        billingClient!!.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingResponseCode.OK) {
                    querySkuDetails(orderID, activity,type)
                }
            }

            override fun onBillingServiceDisconnected() {
//                    LogUtil.i("/////onBillingServiceDisconnected:")
            }
        })
    }

    fun querySkuDetails(orderID: String, activity: Activity, type:Int) {
        val skuList = ArrayList<String>()
        skuList.add("com.chat.samanthai.app.1")// $1.99 首充
        skuList.add("com.chat.samanthai.app.2")// $3.99
        skuList.add("com.chat.samanthai.app.3")// $9.99
        skuList.add("com.chat.samanthai.app.4")// $19.99
        skuList.add("com.chat.samanthai.app.5")// $29.99
        skuList.add("com.chat.samanthai.app.6")// $39.99
        skuList.add("com.chat.samanthai.app.7")// $49.99
        val params = SkuDetailsParams.newBuilder()
        if (type==1){
            params.setSkusList(skuList).setType(SkuType.INAPP)
        }else{
            params.setSkusList(skuList).setType(SkuType.SUBS)
        }
        billingClient!!.querySkuDetailsAsync(
            params.build()
        ) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingResponseCode.OK){
                if (skuDetailsList!=null){
                    for (index in 0 until skuDetailsList.size){
                        if (orderID==skuDetailsList[index].sku){
                            val skuDetails = skuDetailsList[index]
                            toBuy(skuDetails, activity)
                        }
                    }
                }
            }else{
                LogUtils.e("=====>${billingResult.responseCode}")
            }

        }

    }


    private fun toBuy(skuDetails: SkuDetails, activity: Activity){
        val flowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(skuDetails)
            .build()
        billingClient!!.launchBillingFlow(activity, flowParams).responseCode
        SolarUtil.orderEvent(skuDetails.priceAmountMicros/1000000.0,skuDetails.priceCurrencyCode)
    }


    fun handlePurchase(purchase: Purchase) {
        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        val listener = ConsumeResponseListener { billingResult, purchaseToken ->
            if (billingResult.responseCode == BillingResponseCode.OK) {
                SolarUtil.payOrder(this.price,1)
                FacebookEventUtil.orderEvent(this.price)
                val skus = purchase.skus
                skus.forEach {
                    listener?.invoke("",purchase, it,purchase.orderId!!,price)
                }
            }
        }

        if (billingClient!=null) {
            billingClient?.consumeAsync(consumeParams, listener)
        }

    }

    fun handleSubscribePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            GlobalScope.launch(Dispatchers.Main) {
                if (!purchase.isAcknowledged) {
                    // 向后端发送purchaseToken
                    val skus = purchase.skus
                    skus.forEach {
                        listener?.invoke("",purchase, it,purchase.orderId!!,price)
                    }
//                    LogMsg.i("//////purchase_orderID11111:" + Gson().toJson(purchase.skus) + "/////token:" + purchase.orderId)
                    withContext(Dispatchers.IO) {
                        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(purchase.purchaseToken)
                        billingClient?.acknowledgePurchase(acknowledgePurchaseParams.build()) {}
                    }
                }
            }
        }
    }

    /**
     * 掉单处理
     */
    fun handlePurchaseHistory(purchase: Purchase) {
        val consumeParams =
            ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

        val listener =
            ConsumeResponseListener { billingResult, purchaseToken ->
                if (billingResult.responseCode == BillingResponseCode.OK) {
                    SolarUtil.payOrder(this.price,1)
                    FacebookEventUtil.orderEvent(this.price)
                    val skus = purchase.skus
                    skus.forEach {
                        historyListener?.invoke("",purchase, it,purchase.orderId!!,price)
                    }

                }
            }
        if (billingClient!=null){
            billingClient!!.consumeAsync(consumeParams, listener)
        }

    }


    fun isGooglePlayInstalled(context: Context): Boolean {
        val pm: PackageManager = context.packageManager
        var app_installed = false
        app_installed = try {
            val info = pm.getPackageInfo(
                context.packageName,
                PackageManager.GET_ACTIVITIES
            )
            val label = info.applicationInfo.loadLabel(pm) as String
            label != "Market"
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
        return app_installed
    }

    /**
     * 补单操作
     */
    private fun queryHistory() {
        if (billingClient!=null){
            if (billingClient!!.isReady){
                billingClient!!.queryPurchasesAsync(SkuType.INAPP) { billingResult, list ->
                    if (billingResult.responseCode == BillingResponseCode.OK
                    ) {
                        for (purchase in list) {
                            handlePurchaseHistory(purchase)
                        }
                    }
                }

            }

        }
    }

    /**
     * 释放
     */
    fun release() {
        if (billingClient!=null){
            if (billingClient!!.isReady) {
                billingClient!!.endConnection()
            }
        }

    }

}