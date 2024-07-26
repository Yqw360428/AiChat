package com.supremebeing.phoneanti.aichat.ui.help

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.scwang.smart.refresh.footer.BallPulseFooter
import com.scwang.smart.refresh.header.MaterialHeader
import com.supremebeing.phoneanti.aichat.adapter.MyCardAdapter
import com.supremebeing.phoneanti.aichat.base.ArchActivity
import com.supremebeing.phoneanti.aichat.bean.CardBean
import com.supremebeing.phoneanti.aichat.databinding.ActivityCardBinding
import com.supremebeing.phoneanti.aichat.network.server.HttpCode
import com.supremebeing.phoneanti.aichat.tool.gone
import com.supremebeing.phoneanti.aichat.tool.setOnSingleClick
import com.supremebeing.phoneanti.aichat.tool.visible
import com.supremebeing.phoneanti.aichat.ui.chat.BigActivity
import com.supremebeing.phoneanti.aichat.ui.help.vm.HelpViewModel
import com.supremebeing.phoneanti.aichat.utils.netToast

class CardActivity : ArchActivity<HelpViewModel,ActivityCardBinding>() {
    private lateinit var myCardAdapter : MyCardAdapter
    private var mediaList = mutableListOf<CardBean.Data.Medias>()
    private var cardPage = 1
    private var isRefresh = false //true 刷新  false 加载

    @SuppressLint("NotifyDataSetChanged")
    override fun initView() {
        binding.cardClose.setOnSingleClick {
            this.finish()
        }

        myCardAdapter = MyCardAdapter(mediaList)
        binding.recyclerViewCard.layoutManager = GridLayoutManager(this,3)
        binding.recyclerViewCard.adapter = myCardAdapter

        showLoading()
        refreshData()

        binding.cardRefresh.setRefreshHeader(MaterialHeader(this))
        binding.cardRefresh.setRefreshFooter(BallPulseFooter(this))

        viewModel.myCardData.observe(this){
            dismissLoading()
            if (it.error_code == HttpCode.SUCCESS.code){
                if (isRefresh) {
                    binding.cardRefresh.finishRefresh()
                } else {
                    binding.cardRefresh.finishLoadMore()
                }
                it.data.medias.forEach {card->
                    if (!mediaList.contains(card)){
                        mediaList.add(card)
                    }
                }
                if (mediaList.size<=0){
                    binding.cardEmpty.visible()
                    binding.cardRefresh.gone()
                }else{
                    binding.cardEmpty.gone()
                    binding.cardRefresh.visible()
                }
                myCardAdapter.notifyDataSetChanged()
            }else{
                netToast()
            }
        }

        myCardAdapter.setOnItemClickListener{_,_,position->
            val data = mediaList[position]
            launch(BigActivity::class.java, Bundle().apply {
                putInt("anchorId",data.user_id)
                putString("cover",data.cover)
            })
        }

        binding.cardRefresh.setOnRefreshListener {
            refreshData()
        }

        binding.cardRefresh.setOnLoadMoreListener {
            loadData()
        }
    }

    private fun refreshData() {
        isRefresh = true
        showLoading()
        viewModel.myCard(cardPage)
    }

    private fun loadData() {
        isRefresh = false
        showLoading()
        viewModel.myCard(++cardPage)
    }

}