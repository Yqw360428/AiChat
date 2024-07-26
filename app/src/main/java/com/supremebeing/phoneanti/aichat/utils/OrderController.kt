package com.supremebeing.phoneanti.aichat.utils

import com.blankj.utilcode.util.LogUtils
import com.google.gson.Gson
import com.supremebeing.phoneanti.aichat.bean.BabyOrderTicketModel
import com.supremebeing.phoneanti.aichat.tool.AIP

/**
 * 订单保存
 * 掉单存储
 */
object OrderController {
    /**
     * 保存订单
     */
    fun saveRechargeSmallTicketData(gid: Int, code: String, receipt: String,price : Double,type : String) {
        val rechargeSmallTicket = AIP.rechargeTicket
        val rechargeSmallTicketBean: BabyOrderTicketModel
        if (rechargeSmallTicket.isNotBlank()) {
            rechargeSmallTicketBean = Gson().fromJson(
                rechargeSmallTicket,
                BabyOrderTicketModel::class.java
            )

            val list = ArrayList<BabyOrderTicketModel.SmallTicket>()
            val smallTicket = BabyOrderTicketModel.SmallTicket()
            smallTicket.gid = gid
            smallTicket.user_id = AIP.userId
            smallTicket.code = code
            smallTicket.receipt = receipt
            smallTicket.priceType = type
            smallTicket.price = price
            list.add(smallTicket)
            if (rechargeSmallTicketBean.smallTicket.isNotEmpty()) {
                list.addAll(rechargeSmallTicketBean.smallTicket)
            }

            rechargeSmallTicketBean.smallTicket = list
        } else {
            rechargeSmallTicketBean =
                BabyOrderTicketModel()
            val list = ArrayList<BabyOrderTicketModel.SmallTicket>()
            val smallTicket = BabyOrderTicketModel.SmallTicket()
            smallTicket.gid = gid
            smallTicket.user_id = AIP.userId
            smallTicket.code = code
            smallTicket.receipt = receipt
            smallTicket.priceType = type
            smallTicket.price = price
            list.add(smallTicket)
            rechargeSmallTicketBean.smallTicket = list
        }

        AIP.rechargeTicket = Gson().toJson(rechargeSmallTicketBean)
    }

    /**
     * 去掉已提交的订单
     */
    fun dropCommitOrder(receipt: String, code: String, gid: Int) {
        val rechargeSmallTicket = AIP.rechargeTicket
        if (!rechargeSmallTicket.isNullOrBlank()) {
            val rechargeSmallTicketBean = Gson().fromJson(
                rechargeSmallTicket,
                BabyOrderTicketModel::class.java
            )

            if (!rechargeSmallTicketBean.smallTicket.isNullOrEmpty()) {
                try {
                    for (index in 0 until rechargeSmallTicketBean.smallTicket.size) {
                        val smallTicket = rechargeSmallTicketBean.smallTicket[index]
                        if (smallTicket.receipt == receipt && smallTicket.code == code &&
                            smallTicket.gid == gid && smallTicket.user_id == AIP.userId
                        ) {
                            rechargeSmallTicketBean.smallTicket.removeAt(index)
                        }
                    }
                }catch (e: java.lang.Exception){
                    LogUtils.i("/////下标越界了")
                }
            }

            AIP.rechargeTicket = Gson().toJson(rechargeSmallTicketBean)
        }
    }

}