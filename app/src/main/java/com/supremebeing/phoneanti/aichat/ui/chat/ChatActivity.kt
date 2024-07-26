package com.supremebeing.phoneanti.aichat.ui.chat

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.format.DateUtils
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.LogUtils
import com.bumptech.glide.Glide
import com.supremebeing.phoneanti.aichat.App
import com.supremebeing.phoneanti.aichat.MessageBar
import com.supremebeing.phoneanti.aichat.R
import com.supremebeing.phoneanti.aichat.adapter.ChatAdapter
import com.supremebeing.phoneanti.aichat.adapter.RecommendAdapter
import com.supremebeing.phoneanti.aichat.base.ArchActivity
import com.supremebeing.phoneanti.aichat.bean.AnchorDetails
import com.supremebeing.phoneanti.aichat.bean.BabyEquityBean
import com.supremebeing.phoneanti.aichat.bean.CardBean
import com.supremebeing.phoneanti.aichat.bean.MessageBean
import com.supremebeing.phoneanti.aichat.databinding.ActivityChatBinding
import com.supremebeing.phoneanti.aichat.dialog.AppDialog
import com.supremebeing.phoneanti.aichat.dialog.DeleteDialog
import com.supremebeing.phoneanti.aichat.dialog.FeedDialog
import com.supremebeing.phoneanti.aichat.dialog.FirstRechargeDialog
import com.supremebeing.phoneanti.aichat.dialog.RoleDialog
import com.supremebeing.phoneanti.aichat.event.RxBus
import com.supremebeing.phoneanti.aichat.event.RxEvents
import com.supremebeing.phoneanti.aichat.network.server.HttpCode
import com.supremebeing.phoneanti.aichat.network.server.MessageCode
import com.supremebeing.phoneanti.aichat.network.server.MessageStatus
import com.supremebeing.phoneanti.aichat.network.server.RightsCode
import com.supremebeing.phoneanti.aichat.sql.AppDBHelp
import com.supremebeing.phoneanti.aichat.sql.AppDBHelp.updateMessageStatus
import com.supremebeing.phoneanti.aichat.sql.deleteMessage
import com.supremebeing.phoneanti.aichat.sql.deleteMessageOfOtherId
import com.supremebeing.phoneanti.aichat.sql.getAllMessage
import com.supremebeing.phoneanti.aichat.sql.imEntity.ChatMessage
import com.supremebeing.phoneanti.aichat.sql.saveMessage
import com.supremebeing.phoneanti.aichat.sql.saveUser
import com.supremebeing.phoneanti.aichat.sql.setReadMessage
import com.supremebeing.phoneanti.aichat.sql.setUserReadMessage
import com.supremebeing.phoneanti.aichat.tool.AIP
import com.supremebeing.phoneanti.aichat.tool.checkPushSwitchStatus
import com.supremebeing.phoneanti.aichat.tool.gone
import com.supremebeing.phoneanti.aichat.tool.isFastClick
import com.supremebeing.phoneanti.aichat.tool.setOnSingleClick
import com.supremebeing.phoneanti.aichat.tool.toastShort
import com.supremebeing.phoneanti.aichat.tool.visible
import com.supremebeing.phoneanti.aichat.ui.chat.vm.ChatViewModel
import com.supremebeing.phoneanti.aichat.ui.help.StoreActivity
import com.supremebeing.phoneanti.aichat.utils.FacebookEventUtil
import com.supremebeing.phoneanti.aichat.utils.OrderController
import com.supremebeing.phoneanti.aichat.utils.PayController
import com.supremebeing.phoneanti.aichat.utils.SoftKeyBoardListener
import com.supremebeing.phoneanti.aichat.utils.SolarUtil
import com.supremebeing.phoneanti.aichat.utils.netToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.Locale

@SuppressLint("NotifyDataSetChanged")
class ChatActivity : ArchActivity<ChatViewModel, ActivityChatBinding>() {
    private lateinit var deleteDialog : DeleteDialog
    private lateinit var feedDialog : FeedDialog
    private var anchorId = 0
    private var anchorDetail : AnchorDetails? = null
    private var lookDetail = false
    private var autoTransition : Boolean = false //当前翻译状态是否是自动翻译
    private var canLoadMore = true
    private var userRole = 0
    private var openSuggest = true //是否开启回复建议 默认开启
    private var replyMessage : ChatMessage? = null //正在回复消息状态
    private val waitTime : Long = 20 * 1000
    private var isAiMessage = false
    private var cardList = mutableListOf<CardBean.Data.Medias>()
    private lateinit var firstRechargeDialog : FirstRechargeDialog
    private var noCharge = false
    private lateinit var payController : PayController
    private var cardPage = 1
    private var cardPosition = 0
    private var newMessageId = ""
    private var cover : String? = null
    private var isAll = false
    private lateinit var appDialog : AppDialog
    private var firstAi = false
    private var info = false
    private var linearLayoutManager : LinearLayoutManager? = null
    private val ids = mutableListOf<String>()
    private lateinit var roleDialog : RoleDialog
    private var isCommented = false
    private var price = 0.0

    //推荐消息
    private var suggestList = mutableListOf<String>()
    private lateinit var recommendAdapter : RecommendAdapter

    //消息列表
    private var messageList = mutableListOf<ChatMessage>()
    private lateinit var chatAdapter : ChatAdapter

    override fun initView() {
        keepBoard()
        checkLanguage()

        deleteDialog = DeleteDialog(this)
        feedDialog = FeedDialog(this)
        firstRechargeDialog = FirstRechargeDialog(this)
        appDialog = AppDialog(this)
        roleDialog = RoleDialog(this)
        userRights()
        payController = PayController()

        intent.extras?.getInt("id")?.let {
            anchorId = it
            showLoading()
            viewModel.getAnchorData(anchorId)
        }
        intent.extras?.getBoolean("info")?.let {
            info = it
        }
        setSuggestData()
        setMessageList()

        //主播详情
        viewModel.anchorData.observe(this) {
            dismissLoading()
            if (it.error_code == HttpCode.SUCCESS.code) {
                anchorDetail = it
                Glide.with(this)
                    .load(it.data.profile.cover)
                    .error(R.drawable.place)
                    .placeholder(R.drawable.place)
                    .into(binding.chatBg)

                Glide.with(this)
                    .load(it.data.profile.head)
                    .into(binding.chatHead)

                roleDialog.headIcon = it.data.profile.head

                isCommented = it.data.is_commented
                showRate()

                if (it.data.role != null) {
                    userRole = it.data.role
                }
            } else {
                netToast()
            }
        }

        binding.chatHead.setOnSingleClick {
            if (info) {
                this.finish()
            } else {
                launch(InfoActivity::class.java, Bundle().apply {
                    putInt("id", anchorId)
                })
            }
        }

        //消息体变更
        viewModel.messageLiveBean.observe(this) {
            saveMessage(it !!)
            //更新会话记录
            anchorDetail?.data?.profile?.let {
                viewModel.messageBean?.apply {
                    saveUser(
                        this,
                        it.head.ifBlank { "" },
                        anchorId,
                        it.nickname.ifBlank { "" },
                        userRole
                    )
                }
            }
            //添加一条自己的消息
            if (messageList.any { chat -> chat.message_id == it.message_id }) {
                chatAdapter.notifyItemChanged(messageList.size - 1)
            } else {
                messageList.add(it)
                chatAdapter.notifyItemInserted(messageList.size - 1)
            }
            if (messageList.size >= 1) {
                lifecycleScope.launch {
                    try {
                        binding.recyclerViewChat.scrollToPosition(chatAdapter.itemCount - 1)
                    } catch (e : Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        //发送消息
        viewModel.messageData.observe(this) {
            it?.let {
                if (it.error_code == HttpCode.SUCCESS.code) {
                    if (viewModel.messageBean != null) {
                        messageSuccess(it)
                        //显示等待消息
                        waitMessage()
                    } else {
                        //
                    }
                } else {
                    messageError()
                    if (it.error_code == HttpCode.INSUFFICIENT.code) {
                        inputEnabled()
                        //余额不足，弹出充值
                        if (noCharge) {
                            firstRechargeDialog.showDialog(true)
                        } else {
                            launch(StoreActivity::class.java)
                            getString(R.string.no_money_hint).toastShort
                        }

                    } else {
                        it.message?.toastShort
                    }
                }
            } ?: let {
                messageError()
                inputEnabled()
            }
        }

        //翻译结果
        viewModel.translateData.observe(this) {
            dismissLoading()
            it?.let {
                if (it.error_code == HttpCode.SUCCESS.code) {
                    it.messageBean.translate = it.data.r
                    itemData(it.position, it.messageBean)
                }
            }
        }

        binding.chatContent.setHorizontallyScrolling(false)
        binding.chatContent.maxLines = Int.MAX_VALUE

        binding.chatContent.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                if (isFastClick()) {
                    val textMsg = binding.chatContent.text.toString()
                    if (textMsg.isNotEmpty()) {
                        binding.chatContent.setText("")
                        isAiMessage = false
                        viewModel.sendTextMessage(
                            textMsg,
                            anchorId,
                            anchorDetail?.data?.profile?.head
                        )
                    }
                } else {
                    R.string.send_too_fast.toastShort
                }
            }
            false
        }

        binding.chatSend.setOnSingleClick {
            val textMsg = binding.chatContent.text.toString().trim()
            if (textMsg.isNotEmpty()) {
                binding.chatContent.setText("")
                isAiMessage = false
                viewModel.sendTextMessage(textMsg, anchorId, anchorDetail?.data?.profile?.head)
            } else {
                getString(R.string.input_cannot_be_empty).toastShort
            }
        }

        binding.chatBack.setOnSingleClick {
            this.finish()
        }

        binding.chatStore.setOnSingleClick {
            launch(StoreActivity::class.java)
        }

        binding.chatDelete.setOnSingleClick {
            //删除按钮
            binding.chatDeleteView.visible()
            chatAdapter.deleteAction = true
            chatAdapter.notifyDataSetChanged()
        }

        binding.chatSelectAll.setOnCheckedChangeListener { _, isChecked ->
            isAll = isChecked
            messageList.forEach {
                it.is_delete = isChecked
            }
            chatAdapter.notifyDataSetChanged()
        }

        binding.chatDeleteSubmit.setOnSingleClick {
            //确认删除
            if (messageList.any { it.is_delete }) {
                deleteDialog.setType(0)
                deleteDialog.showDialog(true)
            } else {
                getString(R.string.delete_less_hint).toastShort
            }

        }

        binding.chatDeleteCancel.setOnSingleClick {
            //取消删除
            chatAdapter.deleteAction = false
            binding.chatSelectAll.isChecked = false
            chatAdapter.notifyDataSetChanged()
            binding.chatDeleteView.gone()
        }

        //弹窗回调
        deleteDialog.onListener = {
            when (it) {
                0 -> {
                    //删除消息
                    showLoading()
                    if (isAll) {
                        viewModel.doAllMessage(anchorId)
                        isAll = false
                    } else {
                        val ids = mutableListOf<String>()
                        messageList.forEach { data ->
                            if (data.is_delete) {
                                ids.add(data.message_id)
                            }
                        }
                        viewModel.deleteMessage(ids.toTypedArray(), anchorId)
                    }
                }

                1 -> {
                    //购买卡片
                }

                2 -> {
                    //权限开启
                }
            }
        }

        //删除部分消息
        viewModel.removeData.observe(this) {
            dismissLoading()
            if (it.error_code == HttpCode.SUCCESS.code) {
                //删除消息成功
                messageList.forEach { message ->
                    if (message.is_delete) {
                        deleteMessage(message)
                    }
                }
                binding.chatDeleteView.gone()
                chatAdapter.deleteAction = false
                message()
            } else {
                netToast()
            }
        }

        //删除所有消息
        viewModel.allMessageData.observe(this) {
            dismissLoading()
            if (it.error_code == HttpCode.SUCCESS.code) {
                deleteMessageOfOtherId(anchorId)
                binding.chatDeleteView.gone()
                chatAdapter.deleteAction = false
                binding.chatSelectAll.isChecked = false
                sendMessage()
            } else {
                netToast()
            }
        }

        //设置消息为已读
        viewModel.readData.observe(this) {
            if (it.error_code == HttpCode.SUCCESS.code) {
                setUserReadMessage(anchorId, 0)
                ids.forEach { id ->
                    setReadMessage(id, System.currentTimeMillis())
                }
                RxBus.post(RxEvents.RefreshMessageList("1"))
            } else {
                netToast()
            }
        }

        binding.chatFeed.setOnSingleClick {
            feedDialog.showDialog(true)
        }

        binding.chatClap.setOnSingleClick {
            lifecycleScope.launch {
                binding.chatClapView.visible()
                delay(1000)
                binding.chatClapView.gone()
            }
            viewModel.sendTextMessage(
                getString(R.string.hi),
                anchorId,
                anchorDetail?.data?.profile?.head
            )
        }

        subscribeEvent(RxBus.receive(RxEvents.RefreshMessage::class.java).subscribe {
            lifecycleScope.launch {
                when (it.refresh) {
                    "1" -> {
                        //删除等待的消息
                        replyMessage?.let {
                            deleteMessage(it)
                            deleteMessage(it)
                            replyMessage = null
                        }
                        message(true, autoTransition)
                        readMessage()
                    }

                    "2" -> {
                        if (isAiMessage) {
                            //删除等待的消息
                            isAiMessage = false
                            replyMessage?.let {
                                deleteMessage(it)
                                replyMessage = null
                            }
                            message(true, autoTransition)
                            readMessage()
                        }
                    }

                    "3" -> {
                        message()
                    }
                }

            }
        })

        subscribeEvent(RxBus.receive(RxEvents.RefreshAllRights::class.java).subscribe {
            userRights()
        })

        firstRechargeDialog.getListener = { price, code ->
            payController.payDollar(this, code, 1, this.price)
        }

        payController.setOnListener { type, purchase, code, purchaseOrderId, price ->
            val json = JSONObject()
            val receiptJson = JSONObject()
            json.put("code", code)
            receiptJson.put("purchaseToken", purchase.purchaseToken)
            receiptJson.put("order_id", purchaseOrderId)
            json.put("receipt", receiptJson)
            OrderController.saveRechargeSmallTicketData(2, code, json.toString(), price, type)
            viewModel.recharge(json, 2, code, saveEvent = true, isRepeatOrder = false, type, price)
        }

        payController.setOnHistoryListener { type, purchase, code, purchaseOrderId, price ->
            val json = JSONObject()
            val receiptJson = JSONObject()
            json.put("code", code)
            receiptJson.put("purchaseToken", purchase.purchaseToken)
            receiptJson.put("order_id", purchaseOrderId)
            json.put("receipt", receiptJson)
            OrderController.saveRechargeSmallTicketData(2, code, json.toString(), price, type)
            viewModel.recharge(json, 2, code, saveEvent = true, isRepeatOrder = false, type, price)
        }

        viewModel.rechargeData.observe(this) {
            if (it !!.error_code == HttpCode.SUCCESS.code) {
                if (it.gid == 2) {
                    //删除提交成功后的订单
                    OrderController.dropCommitOrder(it.receipt, it.code, it.gid)
                }
            } else {
                netToast()
            }
        }

        binding.chatRecommend.setOnSingleClick {
            if (openSuggest) {
                binding.chatRecommend.setImageResource(R.drawable.c_roff)
            } else {
                binding.chatRecommend.setImageResource(R.drawable.c_ron)
            }
            openSuggest = ! openSuggest
        }

        viewModel.suggestData.observe(this) {
            if (it.error_code == HttpCode.SUCCESS.code) {
                if (it.data.suggests.size <= 0) {
                    binding.chatRecommendView.gone()
                } else {
                    binding.chatRecommendView.visible()
                }
                suggestList.clear()
                suggestList.addAll(it.data.suggests)
                recommendAdapter.notifyDataSetChanged()
            } else {
                netToast()
            }
        }

        binding.chatRefresh.setOnSingleClick {
            viewModel.suggestData()
        }

        feedDialog.feedbackListener = {
            newMessageId = if (messageList.last().isComing == 1) {
                messageList.last().message_id
            } else {
                messageList[messageList.size - 2].message_id
            }
            showLoading()
            viewModel.feedback(anchorId, newMessageId, it)
        }

        viewModel.feedbackData.observe(this) {
            dismissLoading()
            if (it.error_code == HttpCode.SUCCESS.code) {
                feedDialog.dismiss()
                getString(R.string.feedback_successful).toastShort
            } else {
                netToast()
            }
        }

        binding.chatTrans.setOnSingleClick {
            if (autoTransition) {
                //关闭翻译
                AIP.closeTrans = true
                autoTransition = false
                binding.chatTrans.setImageResource(R.drawable.c_off)
            } else {
                //开启翻译
                AIP.closeTrans = false
                autoTransition = true
                binding.chatTrans.setImageResource(R.drawable.c_on)
            }
        }

        binding.recyclerViewChat.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView : RecyclerView, newState : Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (! recyclerView.canScrollVertically(1) && newState === RecyclerView.SCROLL_STATE_IDLE) {
//                        LogUtils.i("BHomeFragment", "上拉拉不动时触发加载新数据")
                }
                if (! recyclerView.canScrollVertically(- 1) && newState === RecyclerView.SCROLL_STATE_IDLE && canLoadMore) {
                    canLoadMore = false
//                    linearLayoutManager?.stackFromEnd = false
//                        LogUtils.i("BChatKitFragment", "下拉拉不动时触发加载新数据")
                    RxBus.post(RxEvents.HistoryMessage(anchorId))
                }
            }

            override fun onScrolled(recyclerView : RecyclerView, dx : Int, dy : Int) {
                super.onScrolled(recyclerView, dx, dy)
//                    manager.invalidateSpanAssignments()
            }
        })

        binding.chatRate.setOnSingleClick {
            roleDialog.showDialog(false)
        }

        roleDialog.contentListener = {
            showLoading()
            viewModel.commentAi(anchorId, null, it)
        }

        viewModel.commentAiData.observe(this) {
            dismissLoading()
            if (it.error_code == HttpCode.SUCCESS.code) {
                R.string.evaluation_successful.toastShort
                isCommented = true
                binding.chatRate.gone()
                roleDialog.dismiss()
            } else {
                netToast()
            }
        }

    }

    private fun setSuggestData() {
        recommendAdapter = RecommendAdapter(suggestList)
        binding.recyclerViewRecommend.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewRecommend.adapter = recommendAdapter

        recommendAdapter.setOnItemClickListener { _, _, position ->
            val data = suggestList[position]
            if (isFastClick() && binding.chatContent.isEnabled) {
                isAiMessage = false
                viewModel.sendTextMessage(data, anchorId, anchorDetail?.data?.profile?.head)
            } else {
                getString(R.string.send_too_fast).toastShort
            }
        }
    }

    /**
     * 刷新item
     * 如果是自动刷新 则 下标+1
     */
    @SuppressLint("NotifyDataSetChanged")
    fun itemData(position : Int, messageBean : ChatMessage) {
        runOnUiThread {
            for (i in 0 until messageList.size) {
                val newMessageBean = messageList[i]
                if (newMessageBean.message_id == messageBean.message_id) {
                    newMessageBean.translate = messageBean.translate
                    AppDBHelp.updateMessageTranslate(
                        messageBean.message_id,
                        messageBean.translate.toString()
                    )
                }
            }
            try {
                if (autoTransition) {
                    chatAdapter.notifyItemChanged(messageList.size - 1)
                    binding.recyclerViewChat.scrollToPosition(messageList.size - 1)
                } else {
                    chatAdapter.notifyItemChanged(position)
                    binding.recyclerViewChat.scrollToPosition(position)
                }
            } catch (e : Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun sendMessage() {
        isAiMessage = true
        AIP.aiId = anchorId
        waitMessage()
        lifecycleScope.launch {
            delay(2000)
            viewModel.aiSendMessage(anchorId)
        }
    }

    private fun showRate() {
        val allMessage = getAllMessage(AIP.userId, anchorId)
        if (allMessage.size >= 10) {
            if (! isCommented) binding.chatRate.visible()
        }
    }

    private fun setMessageList() {
        val allMessage = getAllMessage(AIP.userId, anchorId)
        readMessage()
        messageList.addAll(allMessage)
        if (allMessage.size > 0) {
            if (allMessage.last().isComing == 2 && allMessage.last().message_state == MessageStatus.FAILE.code) {
                viewModel.messageBean = allMessage.last()
            }
        }

        chatAdapter = ChatAdapter(messageList).apply {
            translateCall = { position, chatMessage ->//点击翻译
                if (chatMessage.translate.isNullOrEmpty()) {
                    showLoading()
                    viewModel.sendTranslateData(chatMessage, position)
                } else {
                    messageList[position].translate = ""
                    AppDBHelp.updateMessageTranslate(chatMessage.message_id, "")
                    chatAdapter.notifyItemChanged(position)
                }
            }
            reSendCall = {//点击重新发送
                when (it.message_type) {
                    MessageCode.TEXTMESSAGE.code -> {
                        viewModel.reSendMessage(it, anchorId)
                    }
                }
            }
            copyListener = {
                val clipboardManager =
                    getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboardManager.setPrimaryClip(ClipData.newPlainText("label", it))
                R.string.copy_successful.toastShort
            }
            deleteListener = { isDelete, position ->
                messageList[position].is_delete = isDelete
                chatAdapter.notifyItemChanged(position)
            }
        }
        linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager?.stackFromEnd = true
        binding.recyclerViewChat.layoutManager = linearLayoutManager
        chatAdapter.setHasStableIds(true)
        binding.recyclerViewChat.adapter = chatAdapter

        lifecycleScope.launch {
            delay(500)
            when (allMessage.size) {
                0 -> sendMessage()
                1 -> {
                    if (! autoTransition) {
                        scroll(0)
                    }
                }

                else -> {
                    if (! autoTransition) {
                        scroll(allMessage.size - 1)
                    }
                }
            }
        }

    }

    private fun readMessage() {
        lifecycleScope.launch {
            val allMessage = getAllMessage(AIP.userId, anchorId)
            allMessage.forEach {
                if (it.read == 0L) {
                    ids.add(it.message_id)
                }
            }
            viewModel.readMessage(ids.toTypedArray(), anchorId)
        }
    }

    /**
     * 显示对方正在回复消息的状态
     */
    private fun waitMessage() {
        //****************显示对方正在回复****************
        replyMessage = ChatMessage(
            "1232313131" + System.currentTimeMillis().toString(),
            AIP.userId,
            anchorId,
            "", "",
            System.currentTimeMillis() / 1000,
            MessageCode.TEXTMESSAGE.code,
            0L,
            1, //1接收消息  2发送消息
            "", System.currentTimeMillis() / 1000,
            MessageStatus.SENDING.code,
            "",
            0L,
            0,
            0,
            anchorDetail?.data?.profile?.head,
            AIP.head, false
        )
        lifecycleScope.launch {
            saveMessage(replyMessage !!)
            message()
            delay(waitTime)
            replyMessage?.let {
                deleteMessage(it)
                replyMessage = null
                message()
                inputEnabled()
            }
        }
    }

    /**
     * 设置输入框可用
     */
    private fun inputEnabled() {
        try {
            binding.chatContent.apply {
                isEnabled = true
                hint = getString(R.string.message)
            }
            binding.chatDelete.isEnabled = true
            binding.chatClap.isEnabled = false
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 消息发送成功
     */
    private fun messageSuccess(it : MessageBean) {
        deleteMessage(viewModel.messageBean !!)
        viewModel.messageBean !!.message_id = it.data.msg_id
        viewModel.messageBean !!.message_state = MessageStatus.SUCCESS.code
        saveMessage(viewModel.messageBean !!)
        message()
    }

    /**
     * 消息发送失败
     */
    private fun messageError() {
        if (viewModel.messageBean != null) {
            updateMessageStatus(
                viewModel.messageBean !!.message_id,
                MessageStatus.FAILE.code
            )
            message()
        }
    }

    /**
     * 刷新列表
     */
    @SuppressLint("NotifyDataSetChanged")
    fun message(newMessage : Boolean = false, translate : Boolean = false) {
        runOnUiThread {
            messageList.clear()
            val conversationMessages = getAllMessage(AIP.userId, anchorId)
            if (conversationMessages.isNotEmpty()) {
                try {
                    if (newMessage) {
                        if (translate && conversationMessages[conversationMessages.size - 1].is_ended) {
                            autoTransition = true //自动翻译
                            viewModel.sendTranslateData(
                                conversationMessages[conversationMessages.size - 1],
                                conversationMessages.size - 1
                            )
                        }
                        if (conversationMessages[conversationMessages.size - 1].is_ended && openSuggest) {
                            viewModel.suggestData()
                        }
                        conversationMessages[conversationMessages.size - 1].newMessage = true
                    }
                } catch (e : Exception) {
                    e.printStackTrace()
                }

                try {
                    if (conversationMessages[conversationMessages.size - 1].is_ended) {
                        //ai回复完毕
                        binding.chatContent.apply {
                            isEnabled = true
                            hint = getString(R.string.message)
                        }
                        binding.chatDelete.isEnabled = true
                        binding.chatClap.isEnabled = true
                        newMessageId =
                            conversationMessages[conversationMessages.size - 1].message_id
                        if (canLoadMore) {
                            lifecycleScope.launch {
                                delay(500)
                                if (! autoTransition) {
                                    if (conversationMessages.size == 1) {
                                        scroll(0)
                                    } else {
                                        scroll(conversationMessages.size - 1)
                                    }
                                }
                            }
                        }
                    } else {
                        binding.chatContent.apply {
                            isEnabled = false
                            hint = getString(R.string.type_a_message)
                        }
                        binding.chatDelete.isEnabled = false
                        binding.chatClap.isEnabled = false
                    }
                } catch (e : Exception) {
                    e.printStackTrace()
                }

                if (conversationMessages.size >= 10) {
                    if (! isCommented) binding.chatRate.visible()
                }

                if (conversationMessages.size >= 20) {
                    if (showAppRate()) appDialog.showDialog(false)
                }

            } else {
                viewModel.doAllMessage(anchorId)
            }
            messageList.addAll(conversationMessages)
            chatAdapter.notifyDataSetChanged()
            if (! canLoadMore) {
//                linearLayoutManager?.stackFromEnd = true
                canLoadMore = true
            } else {
                lifecycleScope.launch {
                    try {
                        delay(1000)
                        if (binding.recyclerViewChat.canScrollVertically(1)) {
                            binding.recyclerViewChat.layoutManager?.smoothScrollToPosition(
                                binding.recyclerViewChat,
                                null,
                                chatAdapter.itemCount - 1
                            )
//                                binding.recyclerViewChat.layoutManager?.scrollToPosition(chatAdapter.itemCount - 1)
                        }
                    } catch (e : Exception) {
                        e.printStackTrace()
                    }
                }
            }

        }
    }

    /**
     * 用户权益
     */
    private fun userRights() {
        val rightsList = MessageBar.getRights()
        if (rightsList.isNotEmpty()) {
            val currentTime = System.currentTimeMillis() / 1000
            var tokens : BabyEquityBean.Data.Rights? = null
            val vipList = mutableListOf<Int>()
            rightsList.forEach { rightsBean ->
                if (rightsBean.right != null) {
                    if (rightsBean.right.code == RightsCode.FIRSTTOKEN.code) {
                        if (currentTime >= rightsBean.available_at && currentTime < rightsBean.expired_at) {
                            if (! rightsBean.isIs_used) {
                                tokens = rightsBean
                            }
                        }
                    }

                    if (rightsBean.right.code == RightsCode.VIPDRAWTOKEN.code) {
                        if (currentTime > rightsBean.available_at && currentTime < rightsBean.expired_at) {
                            if (! rightsBean.isIs_used) {
                                vipList.add(1)
                            }
                        }
                    }
                }
            }

            if (tokens != null) {
                firstRechargeDialog.setData(tokens !!)
                if (! tokens?.isIs_used !!) {
                    tokens?.right?.desc?.price?.let {
                        price = it.toDouble()
                    }
                    noCharge = true
                }
            } else {
                noCharge = false
            }
        } else {
            noCharge = false
        }
    }

    private fun scroll(position : Int) {
        runCatching {
            linearLayoutManager?.scrollToPosition(position)
            val view = linearLayoutManager?.findViewByPosition(position) !!
            linearLayoutManager?.scrollToPositionWithOffset(position, view.top)
        }
    }

    override fun onResume() {
        super.onResume()
        checkNotify()
    }


    /**
     * 检查通知权限是否开启
     */
    private fun checkNotify() {
        if (checkPushSwitchStatus()) {
            binding.chatNotification.gone()
        } else {
            binding.chatNotification.visible()
            binding.chatNotification.setOnSingleClick {
                val localIntent = Intent()
                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                localIntent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                localIntent.data = Uri.fromParts("package", packageName, null)
                startActivity(localIntent)
            }
        }
    }

    private fun keepBoard() {
        SoftKeyBoardListener(this).onSoftKeyBoardListener {
            if (it > 0) {
                binding.chatInputView.setPadding(0, 0, 0, it)
            } else {
                binding.chatInputView.setPadding(0, 0, 0, 0)
            }
        }
    }

    fun getBarHeight() = getNavigationBarHeight()

    private fun checkLanguage() {
        val code = Locale.getDefault().language
        if (code.contains("en")) {
            autoTransition = false
            binding.chatTrans.setImageResource(R.drawable.c_off)
        } else {
            if (AIP.closeTrans) {
                autoTransition = false
                binding.chatTrans.setImageResource(R.drawable.c_off)
                return
            }
            autoTransition = true
            binding.chatTrans.setImageResource(R.drawable.c_on)
        }
    }

    private fun showAppRate() : Boolean {
        if (AIP.rated) return false
        if (DateUtils.isToday(AIP.lastAppShow)) return false
        AIP.lastAppShow = System.currentTimeMillis()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        replyMessage?.let {
            deleteMessage(it)
        }
    }
}