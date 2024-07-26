package com.supremebeing.phoneanti.aichat

import android.app.Application
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.LogUtils
import com.supremebeing.phoneanti.aichat.bean.BabyEquityBean
import com.supremebeing.phoneanti.aichat.event.RxBus
import com.supremebeing.phoneanti.aichat.event.RxEvents
import com.supremebeing.phoneanti.aichat.network.server.HttpCode
import com.supremebeing.phoneanti.aichat.tool.AIP
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ynw
 * 消息监听
 */
object MessageBar {
    private var viewModel: AppViewModel?=null
    private var application:Application?=null
    private val rxEvents = CompositeDisposable()
    lateinit var owner: LifecycleOwner

    private var hasSendRefresh=false
    private val rights = mutableListOf<BabyEquityBean.Data.Rights>()

    init {
        viewModel= viewModel?: AppViewModel()
    }

    fun register(life:LifecycleOwner,app: Application){
        this.owner=life
        application=app
        setLiveEvent()
        loadEvent()
    }

    /**
     * 监听绑定
     */
    private fun setLiveEvent() {
        if (AIP.isLogin){
            viewModel?.getRightData()//获取所有
            initMessage()
        }


        subscribeEvent(RxBus.receive(RxEvents.LoginSuccess::class.java).subscribe {
            if (AIP.userId!=0){
                viewModel?.getRightData()//获取权益
                initMessage()
            }
        })

        subscribeEvent(RxBus.receive(RxEvents.UserRights::class.java).subscribe {
            if (AIP.userId!=0){
                viewModel?.getRightData()//获取权益
            }
        })

        subscribeEvent(RxBus.receive(RxEvents.HistoryMessage::class.java).subscribe {
            if (AIP.userId!=0){
                initMessage(it.history)
            }
        })

    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun loadEvent() {
        viewModel?.receiveMessageData?.observe(owner) {
            it?.let {
                if (it.error_code == HttpCode.SUCCESS.code) {
                    GlobalScope.launch(Dispatchers.Unconfined) {
                        val userIds = viewModel?.messageReceive(it,App.instance.applicationContext)
                        if (userIds!!.isNotEmpty()) {
                            viewModel?.messageUserData(
                                userIds,
                                it.startTime
                            )
                        }
                    }
                } else {
                    LogUtils.e("${it.message}")
                }
            }
        }

        viewModel?.messageUserData?.observe(owner) {
            it?.let { bean->
                if (bean.error_code == HttpCode.SUCCESS.code) {
                    if (bean.data != null) {
                        viewModel?.conversationUser(bean,application?.applicationContext!!)
                    }
                } else {
                    LogUtils.e("${bean.message}")
                }
                if (!hasSendRefresh){
                    hasSendRefresh=true
                    if (it.startTime != null){
                        if (it.startTime == 0L){
                            RxBus.post(RxEvents.RefreshMessage("3"))
                        }
                    }
                    RxBus.post(RxEvents.RefreshMessage("2"))
                    RxBus.post(RxEvents.RefreshMessageList("1"))
                    owner.lifecycleScope.launch {
                        delay(500)
                        hasSendRefresh=false
                    }
                }
            }
        }

        viewModel?.rightsAndInterestsData?.observe(owner) {
            it?.let {
                if (it.data != null){
                    if (it.data.rights != null){
                        if (it.data.rights.isNotEmpty()){
                            setRights(it.data.rights)
                        }else{
                            rights.clear()
                        }
                    }else{
                        rights.clear()
                    }
                    RxBus.post(RxEvents.RefreshAllRights("1"))
                }
            }
        }
    }

    //权益数据
    fun setRights(rights: MutableList<BabyEquityBean.Data.Rights>){
        this.rights.clear()
        this.rights.addAll(rights)
    }

    fun getRights() : MutableList<BabyEquityBean.Data.Rights>{
        return rights
    }
    //消息请求
    fun initMessage(his:Int=0) {
        try {
            LogUtils.d("===========收到的消息========："+AIP.messageTime)
            if (AIP.messageTime==0L){
                val currentTime = System.currentTimeMillis()/1000
                viewModel?.getServiceMessageForTime(null, null, null, null, null, currentTime)
            }else{
                val currentTime = System.currentTimeMillis()/1000
                if (his>0){
                    viewModel?.getServiceMessageForTime(
                        null,
                        null,
                        AIP.messageEndTime,
                        null,
                        null,
                        currentTime
                    )
                }else{
                    if (currentTime - AIP.messageTime >=3){
                        viewModel?.getServiceMessageForTime(
                            null,
                            AIP.messageTime,
                            null,
                            null,
                            null,
                            currentTime
                        )
                    }
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun subscribeEvent(event : Disposable){
        rxEvents.add(event)
    }

}