package com.supremebeing.phoneanti.aichat.ui.main.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.format.DateUtils
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.LogUtils
import com.scwang.smart.refresh.footer.BallPulseFooter
import com.scwang.smart.refresh.header.MaterialHeader
import com.supremebeing.phoneanti.aichat.R
import com.supremebeing.phoneanti.aichat.adapter.CardAdapter
import com.supremebeing.phoneanti.aichat.adapter.HotAdapter
import com.supremebeing.phoneanti.aichat.adapter.TabAdapter
import com.supremebeing.phoneanti.aichat.base.ArchFragment
import com.supremebeing.phoneanti.aichat.bean.AnchorDetail
import com.supremebeing.phoneanti.aichat.bean.DailyBean
import com.supremebeing.phoneanti.aichat.bean.SignBean
import com.supremebeing.phoneanti.aichat.bean.TabBean
import com.supremebeing.phoneanti.aichat.databinding.FragmentCardBinding
import com.supremebeing.phoneanti.aichat.event.RxBus
import com.supremebeing.phoneanti.aichat.event.RxEvents
import com.supremebeing.phoneanti.aichat.network.server.HttpCode
import com.supremebeing.phoneanti.aichat.tool.AIP
import com.supremebeing.phoneanti.aichat.tool.getColor
import com.supremebeing.phoneanti.aichat.tool.gone
import com.supremebeing.phoneanti.aichat.tool.setOnSingleClick
import com.supremebeing.phoneanti.aichat.tool.toastShort
import com.supremebeing.phoneanti.aichat.tool.visible
import com.supremebeing.phoneanti.aichat.ui.chat.InfoActivity
import com.supremebeing.phoneanti.aichat.ui.main.vm.MainViewModel
import com.supremebeing.phoneanti.aichat.utils.AdUtil
import com.supremebeing.phoneanti.aichat.utils.netToast
import com.supremebeing.phoneanti.aichat.weight.CardConfig
import com.supremebeing.phoneanti.aichat.weight.CardItemTouchHelperCallback
import com.supremebeing.phoneanti.aichat.weight.CardLayoutManager
import com.supremebeing.phoneanti.aichat.weight.OnSwipeListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

@SuppressLint("NotifyDataSetChanged")
class CardFragment : ArchFragment<MainViewModel, FragmentCardBinding>() {
    companion object {
        fun newInstance() = CardFragment().apply {
            arguments = Bundle()
        }
    }

    private var hasNsfw = 0 //0：默认  2：大尺度
    private var recommendList = mutableListOf<AnchorDetail>()
    private var oldRecommendList = mutableListOf<AnchorDetail>()
    private lateinit var cardAdapter : CardAdapter
    private var refreshNsfw = arrayListOf(false,false,false)
    private var quick : String = "female"
    private lateinit var hotAdapter : HotAdapter
    private lateinit var hot1Adapter : HotAdapter
    private var hotList = mutableListOf<AnchorDetail>() //女性数据
    private var hot1List = mutableListOf<AnchorDetail>() //男性数据
    private var isRefresh = false //true 刷新  false 加载
    private var page = 1
    private var page1 = 1
    private var rePage = 0
    private var isRestore = false

    //tab栏修改
    private var tabList = mutableListOf<TabBean>()
    private lateinit var tabAdapter : TabAdapter

    override fun initView() {
        signData()
        setTabAdapter()
        setRecommendAdapter()
        setGenderAdapter()
        getAiData()
        refreshData()
        refreshData1()

        binding.cardSwitch.setOnCheckedChangeListener { _, isChecked ->
            hasNsfw = if (isChecked) {
                binding.cardOn.visible()
                binding.cardOff.gone()
                2
            } else {
                binding.cardOn.gone()
                binding.cardOff.visible()
                0
            }
            refreshNsfw[0] = true
            refreshNsfw[1] = true
            refreshNsfw[2] = true
            page = 1
            page1 = 1
            rePage = 0
            getAiData()
            refreshData()
            refreshData1()
        }

        /**
         * ai滑动卡片的数据
         */
        viewModel.anchorRecommendData.observe(this) {
            dismissLoading()
            if (it.error_code == HttpCode.SUCCESS.code) {
                lifecycleScope.launch {
                    if (! AIP.firstWipe) {
                        binding.cardSwipeView.visible()
                        delay(2500)
                        binding.cardSwipeView.gone()
                        AIP.firstWipe = true
                    }
                }
                if (refreshNsfw[0]) {
                    oldRecommendList.clear()
                    recommendList.clear()
                    refreshNsfw[0] = false
                }
                if (it.data.anchors != null) {
                    oldRecommendList.addAll(it.data.anchors)
                    oldRecommendList.forEach { data ->
                        if (! recommendList.contains(data)) {
                            recommendList.add(data)
                        }
                    }
                    if (recommendList.size > 0) {
                        binding.cardEmptyView.gone()
                    }
                    cardAdapter.notifyDataSetChanged()
                }
            } else {
                netToast()
                binding.cardEmptyView.visible()
            }
        }

        /**
         * ai女性角色的数据
         */
        viewModel.anchorData.observe(this){
            dismissLoading()
            if (it.error_code == HttpCode.SUCCESS.code){
                if (isRefresh){
                    binding.hotRefresh.finishRefresh(true)
                }else{
                    binding.hotRefresh.finishLoadMore(true)
                }

                if (refreshNsfw[1]){
                    hotList.clear()
                    refreshNsfw[1] = false
                }
                if (it.data != null && it.data.anchors != null){
                    if (it.data.anchors.size>0){
                        it.data.anchors.forEach {anchor->
                            if (!hotList.contains(anchor)){
                                hotList.add(anchor)
                            }
                        }
                    }
                }
                hotAdapter.notifyDataSetChanged()
            }else{
                netToast()
            }
        }

        /**
         * ai男性角色的数据
         */
        viewModel.anchor1Data.observe(this){
            dismissLoading()
            if (it.error_code == HttpCode.SUCCESS.code){
                if (isRefresh){
                    binding.hotRefresh1.finishRefresh(true)
                }else{
                    binding.hotRefresh1.finishLoadMore(true)
                }

                if (refreshNsfw[2]){
                    hot1List.clear()
                    refreshNsfw[2] = false
                }
                if (it.data != null && it.data.anchors != null){
                    if (it.data.anchors.size>0){
                        it.data.anchors.forEach {anchor->
                            if (!hot1List.contains(anchor)){
                                hot1List.add(anchor)
                            }
                        }
                    }
                }

                hot1Adapter.notifyDataSetChanged()
            }else{
                netToast()
            }
        }

        /**
         * 所有角色数据
         */
        viewModel.allAnchorData.observe(this){
            dismissLoading()
            if (it.error_code == HttpCode.SUCCESS.code) {
                if (!isRestore){
                    oldRecommendList.clear()
                    isRestore = true
                }
                if (refreshNsfw[0]) {
                    oldRecommendList.clear()
                    recommendList.clear()
                    refreshNsfw[0] = false
                }
                if (it.data.anchors != null) {
                    oldRecommendList.addAll(it.data.anchors)
                    oldRecommendList.forEach { data ->
                        if (! recommendList.contains(data)) {
                            recommendList.add(data)
                        }
                    }
                    if (recommendList.size > 0) {
                        binding.cardEmptyView.gone()
                    }
                    cardAdapter.notifyDataSetChanged()
                }
            } else {
                netToast()
                binding.cardEmptyView.visible()
            }
        }

        cardAdapter.setOnItemClickListener { _, _, position ->
            val data = recommendList[position]
            launch(InfoActivity::class.java, Bundle().apply {
                putInt("id", data.id)
            })
            AIP.aiName = data.profile.nickname
        }

        binding.cardRestore.setOnSingleClick {
            viewModel.requestAllAiData(hasNsfw,++rePage)
        }

        if (AIP.isRelease) {
            binding.cardNsfwView.visible()
        } else {
            binding.cardNsfwView.gone()
        }
        binding.hotRefresh.setRefreshHeader(MaterialHeader(requireContext()))
        binding.hotRefresh.setRefreshFooter(BallPulseFooter(requireContext()))
        binding.hotRefresh1.setRefreshHeader(MaterialHeader(requireContext()))
        binding.hotRefresh1.setRefreshFooter(BallPulseFooter(requireContext()))

        binding.hotRefresh.setOnRefreshListener {
            refreshData()
        }

        binding.hotRefresh.setOnLoadMoreListener {
            loadData()
        }

        binding.hotRefresh1.setOnRefreshListener {
            refreshData1()
        }

        binding.hotRefresh1.setOnLoadMoreListener {
            loadData1()
        }

    }

    /**
     * 设置tab栏(修改后)
     */
    private fun setTabAdapter() {
        tabList.clear()
        tabList.add(TabBean(getString(R.string.discover), true))
        tabList.add(TabBean(getString(R.string.hot), false))
        tabList.add(TabBean(getString(R.string.male), false))

        tabAdapter = TabAdapter(tabList)
        binding.recyclerViewTab.layoutManager = LinearLayoutManager(requireContext()).apply {
            orientation = RecyclerView.HORIZONTAL
        }
        binding.recyclerViewTab.adapter = tabAdapter

        tabAdapter.setOnItemClickListener { adapter, _, position ->
            val tab = adapter.data[position] as TabBean
            tabList.forEach {
                it.select = tab.tab == it.tab
            }
            tabAdapter.notifyDataSetChanged()
            tabSelect(position)
        }
    }

    /**
     * 设置男性或者女性ai数据
     */
    private fun setGenderAdapter() {
        hotList.clear()
        hotAdapter = HotAdapter(hotList)
        binding.recyclerViewHot.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = hotAdapter
        }

        hotAdapter.setOnItemClickListener { adapter, _, position ->
            val data = adapter.data[position] as AnchorDetail
            launch(InfoActivity::class.java, Bundle().apply {
                putInt("id", data.id)
            })
            AIP.aiName = data.profile.nickname
        }

        hot1List.clear()
        hot1Adapter = HotAdapter(hot1List)
        binding.recyclerViewHot1.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = hot1Adapter
        }

        hot1Adapter.setOnItemClickListener { adapter, _, position ->
            val data = adapter.data[position] as AnchorDetail
            launch(InfoActivity::class.java, Bundle().apply {
                putInt("id", data.id)
            })
            AIP.aiName = data.profile.nickname
        }
    }

    private fun tabSelect(position : Int) {
        when (position) {
            0 -> {
                binding.cardDiscoverView.visible()
                binding.cardHotView.gone()
            }

            1 -> {
                binding.cardDiscoverView.gone()
                binding.cardHotView.visible()
                binding.hotRefresh.visible()
                binding.hotRefresh1.gone()
            }

            2 -> {
                binding.cardDiscoverView.gone()
                binding.cardHotView.visible()
                binding.hotRefresh.gone()
                binding.hotRefresh1.visible()

            }
        }
    }

    /**
     * 滑动卡片的数据设置
     * 以及滑动的监听
     */
    private fun setRecommendAdapter() {
        cardAdapter = CardAdapter(recommendList)
        val cardCallback =
            CardItemTouchHelperCallback(cardAdapter, recommendList)
        val touchHelper = ItemTouchHelper(cardCallback)
        binding.recyclerViewCard.adapter = cardAdapter
        binding.recyclerViewCard.layoutManager =
            CardLayoutManager(binding.recyclerViewCard, touchHelper)
        touchHelper.attachToRecyclerView(binding.recyclerViewCard)
        cardCallback.setOnSwipedListener(object : OnSwipeListener<AnchorDetail> {
            override fun onSwiping(
                holder : RecyclerView.ViewHolder?,
                ratio : Float,
                direction : Int
            ) {
                //正在滑动
                when (direction) {
                    CardConfig.SWIPING_LEFT -> {
                        //正在左滑
                    }

                    CardConfig.SWIPING_RIGHT -> {
                        //正在右滑
                    }
                }
                val placeView = holder?.itemView?.findViewById<AppCompatImageView>(R.id.place_view)
                if (abs(ratio) > 0.1) {
                    placeView?.visible()
                } else {
                    placeView?.gone()
                }

            }

            override fun onSwipedClear() {
                //卡片滑完了
                binding.cardEmptyView.visible()
            }

            override fun onSwiped(
                holder : RecyclerView.ViewHolder?,
                data : AnchorDetail?,
                direction : Int
            ) {
                //滑动之后，左边或者右边
                when (direction) {
                    CardConfig.SWIPED_LEFT -> {
                        //左滑完成
                    }

                    CardConfig.SWIPED_RIGHT -> {
                        //右滑完成
                    }
                }
                val placeView = holder?.itemView?.findViewById<AppCompatImageView>(R.id.place_view)
                placeView?.gone()
            }

        })
    }

    private val signObserver = Observer<SignBean> {
        if (it.error_code == HttpCode.SUCCESS.code) {
            RxBus.post(RxEvents.GlobalSign(it))
        } else {
            netToast()
        }
    }

    private val dailyObserver = Observer<DailyBean> {
        if (it.error_code == HttpCode.SUCCESS.code) {
            RxBus.post(RxEvents.SignReward(it.data.token))
            getString(R.string.successfully_signed_in).toastShort
        } else {
            netToast()
        }
    }

    private fun signData() {
        lifecycleScope.launch {
            AdUtil.loadAd(requireContext()) {}
            delay(DateUtils.MINUTE_IN_MILLIS)
            viewModel.getSign()
        }
        viewModel.signData.observeForever(signObserver)
        viewModel.dailySignData.observeForever(dailyObserver)

        subscribeEvent(RxBus.receive(RxEvents.DailySign::class.java).subscribe {
            if (it.sign) {
                AdUtil.showAd(requireActivity()) {
                    //签到
                    viewModel.dailyCheck(true)
                }
            } else {
                viewModel.dailyCheck(false)
            }

        })
    }

    private fun getAiData() {
        showLoading()
        viewModel.requestRecommendAiData(null, "recommend", hasNsfw, 1)
    }

    private fun refreshData() {
        isRefresh = true
        showLoading()
        viewModel.requestAiData(getString(R.string.female).lowercase(), null, hasNsfw, page)
    }

    private fun loadData() {
        isRefresh = false
        showLoading()
        viewModel.requestAiData(getString(R.string.female).lowercase(), null, hasNsfw, ++ page)
    }

    private fun refreshData1(){
        isRefresh = true
        showLoading()
        viewModel.requestAiData1(getString(R.string.male).lowercase(), null, hasNsfw, page1)
    }

    private fun loadData1(){
        isRefresh = false
        showLoading()
        viewModel.requestAiData1(getString(R.string.male).lowercase(), null, hasNsfw, ++ page1)
    }

    override fun onDestroy() {
        super.onDestroy()
        runCatching {
            viewModel.signData.removeObserver(signObserver)
            viewModel.dailySignData.removeObserver(dailyObserver)
        }
    }
}