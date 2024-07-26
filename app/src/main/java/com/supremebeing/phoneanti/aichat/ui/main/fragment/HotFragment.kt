package com.supremebeing.phoneanti.aichat.ui.main.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.utilcode.util.LogUtils
import com.scwang.smart.refresh.footer.BallPulseFooter
import com.scwang.smart.refresh.header.MaterialHeader
import com.supremebeing.phoneanti.aichat.adapter.HotAdapter
import com.supremebeing.phoneanti.aichat.base.ArchFragment
import com.supremebeing.phoneanti.aichat.bean.AnchorDetail
import com.supremebeing.phoneanti.aichat.databinding.FragmentHotBinding
import com.supremebeing.phoneanti.aichat.event.RxBus
import com.supremebeing.phoneanti.aichat.event.RxEvents
import com.supremebeing.phoneanti.aichat.tool.AIP
import com.supremebeing.phoneanti.aichat.ui.chat.ChatActivity
import com.supremebeing.phoneanti.aichat.ui.chat.InfoActivity
import com.supremebeing.phoneanti.aichat.ui.main.vm.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("NotifyDataSetChanged")
class HotFragment : ArchFragment<MainViewModel,FragmentHotBinding>() {
    companion object{
        fun newInstance(quick:String):HotFragment{
            return HotFragment().apply {
                arguments= Bundle().apply {
                    putString("quick",quick)
                }
            }
        }
    }
    private var quick : String? = null //男或者女
    private var hasNsfw = 0 //0：默认  2：大尺度
    private var page = 1
    private lateinit var hotAdapter : HotAdapter
    private var hotList = mutableListOf<AnchorDetail>()
    private var isRefresh = false //true 刷新  false 加载
    private var refreshNsfw = false

    override fun initView() {
        quick = arguments?.getString("quick")
        refreshData()

        subscribeEvent(RxBus.receive(RxEvents.ShowNsfw::class.java).subscribe {
            refreshNsfw = hasNsfw != it.nsfw
            if (refreshNsfw) page = 1
            hasNsfw = it.nsfw
            refreshData()
        })

        hotAdapter = HotAdapter(hotList)

        binding.recyclerViewHot.apply {
            layoutManager = GridLayoutManager(requireContext(),2)
            adapter = hotAdapter
        }

        viewModel.anchorData.observe(this){
            dismissLoading()
            if (isRefresh){
                binding.hotRefresh.finishRefresh(true)
            }else{
                binding.hotRefresh.finishLoadMore(true)
            }

            if (refreshNsfw){
                hotList.clear()
                refreshNsfw = false
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
        }

        hotAdapter.setOnItemClickListener{adapter,_,position->
            val data = adapter.data[position] as AnchorDetail
            launch(InfoActivity::class.java,Bundle().apply {
                putInt("id",data.id)
            })
            AIP.aiName = data.profile.nickname
        }

        binding.hotRefresh.setRefreshHeader(MaterialHeader(requireContext()))
        binding.hotRefresh.setRefreshFooter(BallPulseFooter(requireContext()))

        binding.hotRefresh.setOnRefreshListener {
            refreshData()
        }

        binding.hotRefresh.setOnLoadMoreListener {
            loadData()
        }

    }

    private fun refreshData(){
        isRefresh = true
        showLoading()
        viewModel.requestAiData(quick,null,hasNsfw,page)
    }

    private fun loadData(){
        isRefresh = false
        showLoading()
        viewModel.requestAiData(quick,null,hasNsfw,++page)
    }
}