package com.supremebeing.phoneanti.aichat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.blankj.utilcode.util.LogUtils
import com.supremebeing.phoneanti.aichat.R
import com.supremebeing.phoneanti.aichat.databinding.AdapterLeftBinding
import com.supremebeing.phoneanti.aichat.databinding.AdapterRightBinding
import com.supremebeing.phoneanti.aichat.sql.imEntity.ChatMessage
import com.supremebeing.phoneanti.aichat.tool.AIP
import com.supremebeing.phoneanti.aichat.tool.dpToPx
import com.supremebeing.phoneanti.aichat.tool.gone
import com.supremebeing.phoneanti.aichat.tool.setOnSingleClick
import com.supremebeing.phoneanti.aichat.tool.visible

class ChatAdapter(var list : MutableList<ChatMessage>) : RecyclerView.Adapter<ViewHolder>() {
    private val left = 0
    private val right = 1

    //删除按钮出现
    var deleteAction = false

    var translateCall:((Int,ChatMessage)->Unit)?=null //翻译
    var reSendCall:((ChatMessage)->Unit)?=null //重新发送
    var copyListener : ((String)->Unit)? = null//复制消息
    var deleteListener : ((Boolean,Int)->Unit)? = null//删除消息

    override fun getItemViewType(position : Int) : Int {
        return if (list[position].isComing == 1) left else right
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : ViewHolder {
        return if (viewType == left) {
            ChatLeftHolder(
                AdapterLeftBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            ChatRightHolder(
                AdapterRightBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemCount() : Int = list.size

    override fun onBindViewHolder(holder : ViewHolder, position : Int) {
        val data = list[position]
        when(holder.itemViewType){
            left->{
                val binding = (holder as ChatLeftHolder).binding
                if (data.is_ended){
                    binding.leftContent.setPadding(dpToPx(15f),dpToPx(15f),dpToPx(15f),dpToPx(15f))
                    binding.leftLottie.gone()
                }else{
                    binding.leftContent.setPadding(dpToPx(15f),dpToPx(5f),dpToPx(15f),dpToPx(5f))
                    binding.leftLottie.visible()
                }

                if (data.isTransLateShow()){
                    binding.leftTransView.visible()
                    binding.leftTrans.text = data.translate
                }else{
                    binding.leftTransView.gone()
                }

                if (deleteAction){
                    binding.leftDelete.visible()
                    binding.leftDelete.setImageResource(if (data.is_delete) R.drawable.ca_c else R.drawable.ca_n)
                }else{
                    binding.leftDelete.gone()
                }

                binding.leftName.text = AIP.aiName
                binding.leftContent.text = data.content

                binding.leftTranslate.setOnSingleClick {
                    translateCall?.invoke(position,data)
                }

                binding.leftDelete.setOnSingleClick {
                    deleteListener?.invoke(!data.is_delete, position)
                }

            }
            right->{
                val binding = (holder as ChatRightHolder).binding
                binding.rightContent.text = data.content

                if (data.isSending()){
                    binding.rightLoad.visible()
                }else{
                    binding.rightLoad.gone()
                }

                if (data.isSendError()){
                    binding.rightResend.visible()
                }else{
                    binding.rightResend.gone()
                }

                if (deleteAction){
                    binding.rightDelete.visible()
                    binding.rightDelete.setImageResource(if (data.is_delete) R.drawable.ca_c else R.drawable.ca_n)
                }else{
                    binding.rightDelete.gone()
                }

                binding.rightResend.setOnSingleClick {
                    reSendCall?.invoke(data)
                }

                binding.rightDelete.setOnSingleClick {
                    deleteListener?.invoke(!data.is_delete, position)
                }

                binding.rightContent.setOnLongClickListener {
                    copyListener?.invoke(binding.rightContent.text.toString())
                    true
                }
            }
        }
    }

    inner class ChatLeftHolder(var binding : AdapterLeftBinding) : ViewHolder(binding.root)

    inner class ChatRightHolder(var binding : AdapterRightBinding) : ViewHolder(binding.root)

}