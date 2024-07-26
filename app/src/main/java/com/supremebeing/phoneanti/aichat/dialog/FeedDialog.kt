package com.supremebeing.phoneanti.aichat.dialog

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import com.supremebeing.phoneanti.aichat.R
import com.supremebeing.phoneanti.aichat.adapter.FeedAdapter
import com.supremebeing.phoneanti.aichat.base.ArchBottomDialog
import com.supremebeing.phoneanti.aichat.bean.FeedBean
import com.supremebeing.phoneanti.aichat.databinding.DialogFeedBinding
import com.supremebeing.phoneanti.aichat.tool.setOnSingleClick
import com.supremebeing.phoneanti.aichat.tool.toastShort
import com.supremebeing.phoneanti.aichat.ui.chat.ChatActivity

class FeedDialog(context : Context) : ArchBottomDialog<DialogFeedBinding>(context) {
    private lateinit var feedAdapter : FeedAdapter
    var feedbackListener : ((String)->Unit)? = null
    override fun initView() {
        binding.root.setPadding(0,0,0,(activity as ChatActivity).getBarHeight())
        val feedList = mutableListOf<FeedBean>().apply {
            add(FeedBean(context.getString(R.string.out_of_character),false))
            add(FeedBean(context.getString(R.string.inaccurate),false))
            add(FeedBean(context.getString(R.string.waiting_too_long),false))
            add(FeedBean(context.getString(R.string.repeat_reply),false))
            add(FeedBean(context.getString(R.string.incomplete_reply),false))
            add(FeedBean(context.getString(R.string.inaccurate_translation),false))
            add(FeedBean(context.getString(R.string.boring),false))
            add(FeedBean(context.getString(R.string.other),false))
        }

        feedAdapter = FeedAdapter(feedList)
        binding.recyclerViewFeed.layoutManager = GridLayoutManager(context,2)
        binding.recyclerViewFeed.adapter = feedAdapter

        binding.feedBtn.setOnSingleClick {
            if (!feedList.any { it.select }) {
                context.getString(R.string.feed_top_hint).toastShort
                return@setOnSingleClick
            }
            if (binding.feedEdit.text.isNullOrBlank()){
                context.getString(R.string.feed_hint).toastShort
                return@setOnSingleClick
            }
            var content = feedList.filter { it.select }.joinToString(","){it.text}
            content += ",${binding.feedEdit.text.toString()}"
            feedbackListener?.invoke(content)
            feedList.forEach {
                it.select = false
            }
            binding.feedEdit.setText("")
        }

        feedAdapter.setOnItemClickListener{_,_,position->
            feedList[position].select = !feedList[position].select
            feedAdapter.notifyItemChanged(position)
        }
    }
}