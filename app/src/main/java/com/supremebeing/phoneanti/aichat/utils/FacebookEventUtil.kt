package com.supremebeing.phoneanti.aichat.utils

import com.facebook.appevents.AppEventsLogger
import com.supremebeing.phoneanti.aichat.App
import java.math.BigDecimal
import java.util.Currency

object FacebookEventUtil {

    fun orderEvent(price : Double,priceType : String = ""){
        AppEventsLogger.newLogger(App.instance).logPurchase(BigDecimal(price), Currency.getInstance("USD"))
    }

}