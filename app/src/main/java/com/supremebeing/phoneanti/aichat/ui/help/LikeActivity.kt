package com.supremebeing.phoneanti.aichat.ui.help

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.scwang.smart.refresh.footer.BallPulseFooter
import com.scwang.smart.refresh.header.MaterialHeader
import com.supremebeing.phoneanti.aichat.R
import com.supremebeing.phoneanti.aichat.adapter.LikeAdapter
import com.supremebeing.phoneanti.aichat.base.ArchActivity
import com.supremebeing.phoneanti.aichat.bean.FollowingBean
import com.supremebeing.phoneanti.aichat.databinding.ActivityLikeBinding
import com.supremebeing.phoneanti.aichat.network.server.HttpCode
import com.supremebeing.phoneanti.aichat.tool.AIP
import com.supremebeing.phoneanti.aichat.tool.setOnSingleClick
import com.supremebeing.phoneanti.aichat.ui.chat.ChatActivity
import com.supremebeing.phoneanti.aichat.ui.help.vm.HelpViewModel
import com.supremebeing.phoneanti.aichat.utils.netToast

class LikeActivity : ArchActivity<HelpViewModel,ActivityLikeBinding>() {
    private var likeList = mutableListOf<FollowingBean.Data.Follower>()
    private lateinit var likeAdapter : LikeAdapter
    private var isRefresh = false //true 刷新  false 加载
    private var page = 1

    @SuppressLint("NotifyDataSetChanged")
    override fun initView() {

        binding.likeClose.setOnSingleClick {
            this.finish()
        }

        likeAdapter = LikeAdapter(likeList)
        binding.recyclerViewLike.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewLike.adapter = likeAdapter

        binding.refresh.setOnRefreshListener {
            refreshData()
        }

        binding.refresh.setOnLoadMoreListener {
            loadData()
        }

        refreshData()

        binding.refresh.setRefreshHeader(MaterialHeader(this))
        binding.refresh.setRefreshFooter(BallPulseFooter(this))

        viewModel.followingData.observe(this){
            dismissLoading()
            if (it.error_code == HttpCode.SUCCESS.code){
                if (it.data.followings != null){
                    if (isRefresh){
                        binding.refresh.finishRefresh()
                    }else{
                        binding.refresh.finishLoadMore()
                    }
                    if (it.data.followings.isNotEmpty()){
                        it.data.followings.forEach {fo->
                            if (!likeList.contains(fo)){
                                likeList.add(fo)
                            }
                        }
                    }
                    likeAdapter.notifyDataSetChanged()
                }
            }else{
                netToast()
            }
        }

        likeAdapter.setOnItemChildClickListener{adapter,view,position->
            val data = adapter.data[position] as FollowingBean.Data.Follower
            if (view.id == R.id.like_chat){
                launch(ChatActivity::class.java, Bundle().apply {
                    putString("cover",data.profile?.cover)
                    putInt("id",data.id)
                    putString("mood",data.profile?.mood)
                })
                AIP.aiName = data.profile?.nickname ?: getString(R.string.unknown)
            }
        }
    }

    private fun refreshData(){
        isRefresh = true
        showLoading()
        viewModel.following(page)
    }

    private fun loadData(){
        isRefresh = false
        showLoading()
        viewModel.following(++page)
    }

    override fun onResume() {
        super.onResume()
        likeList.clear()
        page = 1
        refreshData()
    }
}