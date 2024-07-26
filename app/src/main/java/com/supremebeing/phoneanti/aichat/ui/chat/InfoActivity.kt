package com.supremebeing.phoneanti.aichat.ui.chat

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.format.DateUtils
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.utilcode.util.LogUtils
import com.bumptech.glide.Glide
import com.scwang.smart.refresh.footer.BallPulseFooter
import com.scwang.smart.refresh.header.MaterialHeader
import com.supremebeing.phoneanti.aichat.R
import com.supremebeing.phoneanti.aichat.adapter.BarrageViewHolder
import com.supremebeing.phoneanti.aichat.adapter.InfoAdapter
import com.supremebeing.phoneanti.aichat.base.ArchActivity
import com.supremebeing.phoneanti.aichat.bean.AnchorDetails
import com.supremebeing.phoneanti.aichat.bean.CardBean
import com.supremebeing.phoneanti.aichat.databinding.ActivityInfoBinding
import com.supremebeing.phoneanti.aichat.dialog.DeleteDialog
import com.supremebeing.phoneanti.aichat.network.server.HttpCode
import com.supremebeing.phoneanti.aichat.tool.getColor
import com.supremebeing.phoneanti.aichat.tool.gone
import com.supremebeing.phoneanti.aichat.tool.invisible
import com.supremebeing.phoneanti.aichat.tool.setOnSingleClick
import com.supremebeing.phoneanti.aichat.tool.setSpannable
import com.supremebeing.phoneanti.aichat.tool.toastShort
import com.supremebeing.phoneanti.aichat.tool.visible
import com.supremebeing.phoneanti.aichat.ui.chat.vm.ChatViewModel
import com.supremebeing.phoneanti.aichat.ui.help.StoreActivity
import com.supremebeing.phoneanti.aichat.utils.netToast


@SuppressLint("ClickableViewAccessibility", "SetTextI18n", "NotifyDataSetChanged")
class InfoActivity : ArchActivity<ChatViewModel, ActivityInfoBinding>() {
    private lateinit var infoAdapter : InfoAdapter
    private var cardList = mutableListOf<CardBean.Data.Medias>()
    private var anchorId = 0
    private var anchorDetail : AnchorDetails? = null
    private var cardPage = 1
    private var isFollowing = false
    private lateinit var deleteDialog : DeleteDialog
    private var carPosition = 0
    private var isAllCard = false
    private var isRefresh = false //true 刷新  false 加载
    private var allCoins = 0
    private var buyCard = false
    private var barrageList = mutableListOf<String>()
    private var tagShow = false

    override fun initView() {
        deleteDialog = DeleteDialog(this)
        cardData()

        intent.extras?.getInt("id")?.let {
            anchorId = it
        }

        binding.infoBack.setOnSingleClick {
            this.finish()
        }

        viewModel.aiComments(anchorId)
        viewModel.getAnchorData(anchorId)//主播详情
        refreshData()

        //主播详情
        viewModel.anchorData.observe(this) {
            dismissLoading()
            if (it.error_code == HttpCode.SUCCESS.code) {
                anchorDetail = it
                if (it.data.profile.mood.isNullOrBlank()) {
                    viewModel.translateContent(getString(R.string.acout_hint))
                } else {
                    viewModel.translateContent(it.data.profile.mood)
                }
                initData()
            } else {
                netToast()
            }
        }

        //ai的评价内容
        viewModel.aiCommentsData.observe(this){
            if (it.error_code == HttpCode.SUCCESS.code){
                barrageList.clear()
                if (it.data.comments.isNotEmpty()){
                    it.data.comments.forEach {comment->
                        barrageList.add(comment.content)
                    }
                }
                if (barrageList.isNotEmpty()){
                    binding.infoBarrage.visible()
                    setBarrageData()
                }else{
                    binding.infoBarrage.gone()
                }
            }else{
                netToast()
            }
        }

        //翻译角色介绍
        viewModel.moodData.observe(this) {
            if (it.error_code == HttpCode.SUCCESS.code) {
                binding.infoMood.text = setSpannable(
                    "${getString(R.string.about_me)}${it.data.r}",
                    arrayOf(getString(R.string.about_me)), R.color.white
                )
            } else {
                netToast()
            }
        }

        //卡片内容
        viewModel.aiCardData.observe(this) {
            dismissLoading()
            when (it.error_code) {
                HttpCode.SUCCESS.code -> {
                    if (isRefresh) {
                        binding.infoRefresh.finishRefresh()
                    } else {
                        binding.infoRefresh.finishLoadMore()
                    }
                    if (!buyCard){
                        it.data.medias.forEach { medias ->
                            if (! cardList.contains(medias)) {
                                cardList.add(medias)
                            }
                        }
                    }else{
                        buyCard = false
                    }

                    if (it.data.medias.isNotEmpty()){
                        binding.media = it.data.unlock_all
                        allCoins = it.data.unlock_all?.token !!
                    }

                    if (allCoins == 0) {
                        binding.infoLockAll.gone()
                    }
                    if (cardList.isEmpty()){
                        binding.infoImage.gone()
                    }else{
                        binding.infoImage.visible()
                    }
                    infoAdapter.cover = anchorDetail?.data?.profile?.cover
                    infoAdapter.notifyDataSetChanged()
                }

                else -> {
                    netToast()
                }
            }
        }

        //点击喜欢
        binding.infoLike.setOnSingleClick {
            showLoading()
            if (isFollowing) {
                viewModel.cancelLike(anchorId)
                binding.infoLike.setImageResource(R.drawable.c_like_n)
            } else {
                viewModel.addLike(anchorId)
                binding.infoLike.setImageResource(R.drawable.c_like_y)
            }
            isFollowing = ! isFollowing
        }

        //收藏接口回调
        viewModel.likeData.observe(this) {
            dismissLoading()
            if (it.error_code == HttpCode.SUCCESS.code) {
                //收藏接口请求成功
                if (isFollowing) {
                    getString(R.string.follow_successfully).toastShort
                } else {
                    getString(R.string.canceled_successfully).toastShort
                }
            } else {
                netToast()
            }
        }

        //解锁所有卡片
        binding.infoLockAll.setOnSingleClick {
            isAllCard = true
            var needToken = 0
            cardList.forEach {
                if (! it.is_unlocked) {
                    needToken += it.unlock?.token ?: 0
                }
            }
            deleteDialog.setType(1)
            deleteDialog.setPrice(allCoins)
            deleteDialog.showDialog(true)
        }

        binding.infoChat.setOnSingleClick {
            launch(ChatActivity::class.java, Bundle().apply {
                putInt("id", anchorId)
                putBoolean("info", true)
            })
        }

        binding.infoRefresh.setRefreshHeader(MaterialHeader(this))
        binding.infoRefresh.setRefreshFooter(BallPulseFooter(this))

        binding.infoRefresh.setOnRefreshListener {
            refreshData()
        }

        binding.infoRefresh.setOnLoadMoreListener {
            loadData()
        }



    }

    private fun setBarrageData(){
        val barrageHolder = BarrageViewHolder()
        binding.infoBarrage.apply {
            setData(barrageList,barrageHolder)
            displayLines = 2
            minIntervalTime = 2000
            maxIntervalTime = 4000
            animationTime = 10000
            isRepeat = true
            start()
        }

    }

    private fun initData() {
        anchorDetail?.let {
            binding.data = it

            Glide.with(this)
                .load(it.data.profile.cover)
                .placeholder(R.drawable.place)
                .error(R.drawable.place)
                .into(binding.infoBg)

            binding.infoLike.setImageResource(if (it.data.is_following) R.drawable.c_like_y else R.drawable.c_like_n)
            isFollowing = it.data.is_following

            binding.infoGender.setImageResource(if (it.data.profile.gender == 1) R.drawable.in_male else R.drawable.in_female)

            if (it.data.is_nsfw) {
                binding.infoNsfw.visible()
                tagShow = true
            }

            if (it.data.need_vip) {
                binding.infoVip.visible()
                tagShow = true
            }

            if (it.data.is_new) {
                binding.infoNewHot.visible()
                binding.infoNewHot.text = getString(R.string.new_)
                tagShow = true
            }
            it.data.anchor?.let {anchor->
                if (anchor.is_hot) {
                    binding.infoNewHot.visible()
                    binding.infoNewHot.text = getString(R.string.hot)
                    tagShow = true
                }
            }

            it.data.tags?.let {list->
                if (list.isNotEmpty()) {
                    binding.infoSexual.visible()
                    binding.infoSexual.text = list.first().name
                    tagShow = true
                }
            }

            if (tagShow){
                binding.infoTagView.visible()
            }else{
                binding.infoTagView.gone()
            }
        }

    }

    private fun cardData() {
        cardList.clear()
        infoAdapter = InfoAdapter(cardList)
        binding.recyclerViewInfo.layoutManager = GridLayoutManager(this, 3)
        binding.recyclerViewInfo.adapter = infoAdapter

        infoAdapter.setOnItemClickListener { _, _, position ->
            val medias = cardList[position]
            carPosition = position
            if (medias.is_unlocked) {
                //已经解锁(查看大图，跳转大图页面)
                launch(BigActivity::class.java, Bundle().apply {
                    putString("cover", medias.cover)
                    putInt("anchorId", anchorId)
                })
            } else {
                //未解锁
                val haveHeart = anchorDetail?.data?.hearts?.value
                val needHeart = medias.unlock?.hearts
                val needToken = medias.unlock?.token
                if (haveHeart != null && needHeart != null && needToken != null){
                    isAllCard = false
                    if (haveHeart >= needHeart) {
                        showLoading()
                        viewModel.unlockCard(medias.id !!)
                    } else {
                        deleteDialog.setType(1)
                        deleteDialog.setPrice(needToken)
                        deleteDialog.showDialog(true)
                    }
                }
            }
        }

        //购买卡片
        deleteDialog.onListener = {
            if (it == 1) {
                if (isAllCard) {
                    showLoading()
                    viewModel.unlockAll(anchorId)
                } else {
                    showLoading()
                    viewModel.unlockCard(cardList[carPosition].id !!)
                }
            }
        }

        //解锁卡片
        viewModel.unlockData.observe(this) {
            dismissLoading()
            when (it.error_code) {
                HttpCode.SUCCESS.code -> {
                    if (isAllCard) {
                        cardList.forEach { bean ->
                            bean.is_unlocked = true
                        }
                        binding.infoLockAll.gone()
                    } else {
                        cardList[carPosition].is_unlocked = true
                    }
                    buyCard = true
                    refreshData(1)
                }

                HttpCode.INSUFFICIENT.code -> {
                    launch(StoreActivity::class.java)
                    getString(R.string.no_money_hint).toastShort
                }

                else -> {
                    netToast()
                }
            }
            isAllCard = false
        }
    }

    private fun refreshData(page : Int = cardPage) {
        isRefresh = true
//        showLoading()
        viewModel.aiCardList(anchorId, page)//获取卡片
    }

    private fun loadData() {
        isRefresh = false
//        showLoading()
        viewModel.aiCardList(anchorId,++cardPage)//获取卡片
    }

    fun getBarHeight() = getNavigationBarHeight()

}