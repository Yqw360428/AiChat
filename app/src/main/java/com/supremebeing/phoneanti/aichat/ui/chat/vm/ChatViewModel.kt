package com.supremebeing.phoneanti.aichat.ui.chat.vm

import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.LogUtils
import com.supremebeing.phoneanti.aichat.base.ArchViewModel
import com.supremebeing.phoneanti.aichat.bean.AllMessageBean
import com.supremebeing.phoneanti.aichat.bean.AnchorDetails
import com.supremebeing.phoneanti.aichat.bean.BaseBean
import com.supremebeing.phoneanti.aichat.bean.CardBean
import com.supremebeing.phoneanti.aichat.bean.CommentBean
import com.supremebeing.phoneanti.aichat.bean.LikeBean
import com.supremebeing.phoneanti.aichat.bean.MessageBean
import com.supremebeing.phoneanti.aichat.bean.RechargeBean
import com.supremebeing.phoneanti.aichat.bean.SuggestBean
import com.supremebeing.phoneanti.aichat.bean.TouchBean
import com.supremebeing.phoneanti.aichat.bean.TranslateBean
import com.supremebeing.phoneanti.aichat.bean.UnlockBean
import com.supremebeing.phoneanti.aichat.event.RxBus
import com.supremebeing.phoneanti.aichat.event.RxEvents
import com.supremebeing.phoneanti.aichat.network.client.RetrofitClient
import com.supremebeing.phoneanti.aichat.network.server.HttpCode
import com.supremebeing.phoneanti.aichat.network.server.MessageCode
import com.supremebeing.phoneanti.aichat.network.server.MessageStatus
import com.supremebeing.phoneanti.aichat.sql.imEntity.ChatMessage
import com.supremebeing.phoneanti.aichat.tool.AIP
import com.supremebeing.phoneanti.aichat.utils.SolarUtil
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

class ChatViewModel : ArchViewModel() {
    val messageData = MutableLiveData<MessageBean>()
    val translateData = MutableLiveData<TranslateBean>()
    val anchorData = MutableLiveData<AnchorDetails>()
    val likeData = MutableLiveData<LikeBean>()
    val allMessageData = MutableLiveData<BaseBean>()
    val touchData = MutableLiveData<TouchBean>()
    val messageLiveBean = MutableLiveData<ChatMessage>()
    val aiCardData = MutableLiveData<CardBean>()
    val unlockData = MutableLiveData<UnlockBean>()
    val rechargeData = MutableLiveData<RechargeBean>()
    val moodData = MutableLiveData<TranslateBean>()
    val suggestData = MutableLiveData<SuggestBean>()
    val feedbackData = MutableLiveData<BaseBean>()
    val coverData = MutableLiveData<BaseBean>()
    val removeData = MutableLiveData<BaseBean>()
    var messageBean : ChatMessage? = null
    val readData = MutableLiveData<BaseBean>()
    val commentAiData = MutableLiveData<BaseBean>()
    val aiCommentsData = MutableLiveData<CommentBean>()


    /**
     * 重发消息
     */
    fun reSendMessage(bean : ChatMessage, id : Int) {
        bean.message_state = MessageStatus.SENDING.code
        bean.sendTime = System.currentTimeMillis() / 1000
        messageLiveBean.postValue(bean)

        sendMessageToService(
            id,
            MessageCode.PERSONAL.code,
            bean.message_type,
            bean.content,
            bean.file_path,
            bean.message_id,
            bean.giftId
        )
    }

    /**
     * 发送文本消息
     */
    fun sendTextMessage(textMsg : String, anchorId : Int, head : String?) {
        messageBean = ChatMessage(
            anchorId.toString() + System.currentTimeMillis().toString(),
            AIP.userId,
            anchorId,
            textMsg, "",
            System.currentTimeMillis() / 1000,
            MessageCode.TEXTMESSAGE.code,
            0L,
            2, //1接收消息  2发送消息
            "", System.currentTimeMillis() / 1000,
            MessageStatus.SENDING.code,
            "",
            0L,
            0,
            0,
            head,
            AIP.head
        )
        messageLiveBean.postValue(messageBean)
        sendMessageToService(
            anchorId,
            MessageCode.PERSONAL.code,
            MessageCode.TEXTMESSAGE.code,
            textMsg,
            "",
            "",
            0
        )
    }

    /**
     * 发送消息到服务端
     */
    private fun sendMessageToService(
        anchorId : Int,
        to_type : Int,
        message_type : Int,
        content : String?,
        url : String?,
        mediaId : String?,
        gift_id : Int
    ) {
        val json = JSONObject()
        json.put("to", anchorId)
        json.put("to_type", to_type)
        val childJson = JSONObject()
        childJson.put("type", message_type)
        childJson.put("text", content)
        childJson.put("url", url)
        childJson.put("media_id", mediaId)
        childJson.put("gift_id", gift_id)
        json.put("content", childJson)

        val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        rxHttpSubscribe(RetrofitClient.serviceApi.sendMessage(requestBody),
            {
                messageData.postValue(it)
            }, {
                messageData.postValue(getErrorBean(it, MessageBean::class.java))
            }
        )
    }

    /**
     * 翻译文本消息
     */
    fun sendTranslateData(messageBean : ChatMessage, position : Int) {
        val json = JSONObject()
        json.put("q", messageBean.content)
        json.put("target", AIP.localCode)
        val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        rxHttpSubscribe(RetrofitClient.serviceApi.translate(requestBody),
            {
                it.position = position
                it.messageBean = messageBean
                translateData.postValue(it)
            },
            {
                translateData.postValue(getErrorBean(it, TranslateBean::class.java))
            }
        )
    }

    /**
     * 翻译角色介绍
     */
    fun translateContent(mood : String) {
        val json = JSONObject()
        json.put("q", mood)
        json.put("target", AIP.localCode)
        val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        rxHttpSubscribe(RetrofitClient.serviceApi.translate(requestBody),
            {
                moodData.postValue(it)
            },
            {
                moodData.postValue(getErrorBean(it, TranslateBean::class.java))
            })
    }

    /**
     * 主播详情
     */
    fun getAnchorData(id : Int) {
        rxHttpSubscribe(RetrofitClient.serviceApi.getData(id),
            {
                anchorData.postValue(it)
            }, {
                anchorData.postValue(getErrorBean(it, AnchorDetails::class.java))
            }
        )
    }

    /**
     * 添加关注
     */
    fun addLike(id : Int) {
        val json = JSONObject()
        json.put("user_id", id)
        val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        rxHttpSubscribe(RetrofitClient.serviceApi.addLike(requestBody),
            {
                likeData.postValue(it)
            },
            {
                likeData.postValue(getErrorBean(it, LikeBean::class.java))
            }
        )
    }

    /**
     * 取消关注
     */
    fun cancelLike(id : Int) {
        val json = JSONObject()
        json.put("user_id", id)
        val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        rxHttpSubscribe(RetrofitClient.serviceApi.cancelLike(requestBody),
            {
                likeData.postValue(it)
            },
            {
                likeData.postValue(getErrorBean(it, LikeBean::class.java))
            }
        )
    }

    /**
     * 批量处理消息
     */
    fun doAllMessage(id : Int) {
        val json = JSONObject()
        json.put("op", "remove")
        json.put("other_id", id)
        val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        rxHttpSubscribe(RetrofitClient.serviceApi.doAllMessage(requestBody),
            {
                allMessageData.postValue(it)
            },
            {
                allMessageData.postValue(getErrorBean(it, BaseBean::class.java))
            })
    }

    /**
     * ai主动打招呼
     */
    fun aiSendMessage(id : Int) {
        val json = JSONObject()
        json.put("user_id", id)
        val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        rxHttpSubscribe(RetrofitClient.serviceApi.aiSendMessage(requestBody),
            {
                touchData.postValue(it)
            },
            {
                touchData.postValue(getErrorBean(it, TouchBean::class.java))
            })
    }

    /**
     * ai卡片列表
     */
    fun aiCardList(id : Int, page : Int) {
        rxHttpSubscribe(RetrofitClient.serviceApi.aiCardList(id, page),
            {
                aiCardData.postValue(it)
            },
            {
                aiCardData.postValue(getErrorBean(it, CardBean::class.java))
            }
        )
    }

    /**
     * 解锁ai卡片
     */
    fun unlockCard(pid : Int) {
        val json = JSONObject()
        json.put("pid", pid)
        val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        rxHttpSubscribe(RetrofitClient.serviceApi.unlockCard(requestBody),
            {
                unlockData.postValue(it)
            },
            {
                unlockData.postValue(getErrorBean(it, UnlockBean::class.java))
            }
        )
    }

    /**
     * 解锁所有卡片
     */
    fun unlockAll(aid : Int) {
        val json = JSONObject()
        json.put("aid", aid)
        val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        rxHttpSubscribe(RetrofitClient.serviceApi.unlockAll(requestBody),
            {
                unlockData.postValue(it)
            },
            {
                unlockData.postValue(getErrorBean(it, UnlockBean::class.java))
            }
        )
    }

    /**
     * 充值
     */
    fun recharge(
        json : JSONObject,
        gid : Int,
        code : String,
        saveEvent : Boolean = false,
        isRepeatOrder : Boolean = false,
        priceType : String,
        price : Double
    ) {
        val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        rxHttpSubscribe(RetrofitClient.serviceApi.recharge(requestBody),
            {
                it.gid = gid
                it.saveEvent = saveEvent
                it.code = code
                it.receipt = json.toString()
                it.isRepeat = isRepeatOrder
                rechargeData.postValue(it)
            },
            {
                rechargeData.postValue(getErrorBean(it, RechargeBean::class.java))
            }
        )
    }

    /**
     * 消息建议
     */
    fun suggestData() {
        rxHttpSubscribe(RetrofitClient.serviceApi.suggestData(),
            {
                suggestData.postValue(it)
            },
            {
                suggestData.postValue(getErrorBean(it, SuggestBean::class.java))
            }
        )
    }

    /**
     * 反馈建议
     */
    fun feedback(anchorId : Int, msgId : String, content : String) {
        val json = JSONObject()
        json.put("anchor_id", anchorId)
        json.put("msg_id", msgId)
        json.put("content", content)
        val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        rxHttpSubscribe(RetrofitClient.serviceApi.feedBack(requestBody),
            {
                feedbackData.postValue(it)
            },
            {
                feedbackData.postValue(getErrorBean(it, BaseBean::class.java))
            }
        )
    }

    /**
     * 更新封面
     */
    fun refreshCover(anchorId : Int, cover : String) {
        val json = JSONObject()
        json.put("user_id", anchorId)
        json.put("chat_cover", cover)
        val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        rxHttpSubscribe(RetrofitClient.serviceApi.refreshCover(requestBody),
            {
                coverData.postValue(it)
            },
            {
                coverData.postValue(getErrorBean(it, BaseBean::class.java))
            }
        )
    }

    /**
     * 删除消息
     */
    fun deleteMessage(msg_ids : Array<String>, other_id : Int) {
        val json = JSONObject()
        json.put("op", "remove")
        json.put("msg_ids", JSONArray(msg_ids))
        json.put("other_id", other_id)
        val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        rxHttpSubscribe(RetrofitClient.serviceApi.deleteMessage(requestBody),
            {
                removeData.postValue(it)
            },
            {
                removeData.postValue(getErrorBean(it, BaseBean::class.java))
            }
        )
    }

    /**
     * 已读消息
     */
    fun readMessage(msg_ids : Array<String>, other_id : Int) {
        val json = JSONObject()
        json.put("op", "read")
        json.put("msg_ids", JSONArray(msg_ids))
        json.put("other_id", other_id)
        val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        rxHttpSubscribe(RetrofitClient.serviceApi.readMessage(requestBody),
            {
                readData.postValue(it)
            },
            {
                readData.postValue(getErrorBean(it, BaseBean::class.java))
            }
        )
    }

    /**
     * 评价ai
     */
    fun commentAi(aid : Int, rating : Int?, content : String) {
        val json = JSONObject()
        json.put("aid", aid)
        rating?.let {
            json.put("rating", rating)
        }
        json.put("content", content)
        val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        rxHttpSubscribe(RetrofitClient.serviceApi.commentAi(requestBody),
            {
                commentAiData.postValue(it)
            },
            {
                commentAiData.postValue(getErrorBean(it, BaseBean::class.java))
            }
        )
    }

    /**
     * ai评价内容
     */
    fun aiComments(aid : Int) {
        rxHttpSubscribe(RetrofitClient.serviceApi.aiComment(aid),
            {
                aiCommentsData.postValue(it)
            },
            {
                aiCommentsData.postValue(getErrorBean(it, CommentBean::class.java))
            }
        )
    }
}