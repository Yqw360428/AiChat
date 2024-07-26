package com.supremebeing.phoneanti.aichat.network.server

import com.supremebeing.phoneanti.aichat.bean.AllMessageBean
import com.supremebeing.phoneanti.aichat.bean.AnchorBean
import com.supremebeing.phoneanti.aichat.bean.AnchorDetails
import com.supremebeing.phoneanti.aichat.bean.BabyEquityBean
import com.supremebeing.phoneanti.aichat.bean.BaseBean
import com.supremebeing.phoneanti.aichat.bean.CardBean
import com.supremebeing.phoneanti.aichat.bean.CommentBean
import com.supremebeing.phoneanti.aichat.bean.DailyBean
import com.supremebeing.phoneanti.aichat.bean.DeleteAccountBean
import com.supremebeing.phoneanti.aichat.bean.FollowingBean
import com.supremebeing.phoneanti.aichat.bean.IniBean
import com.supremebeing.phoneanti.aichat.bean.InitBean
import com.supremebeing.phoneanti.aichat.bean.LikeBean
import com.supremebeing.phoneanti.aichat.bean.LoginBean
import com.supremebeing.phoneanti.aichat.bean.MessageBean
import com.supremebeing.phoneanti.aichat.bean.MessageUserBean
import com.supremebeing.phoneanti.aichat.bean.ReceiveMessageBean
import com.supremebeing.phoneanti.aichat.bean.RechargeBean
import com.supremebeing.phoneanti.aichat.bean.RightApplyBean
import com.supremebeing.phoneanti.aichat.bean.SignBean
import com.supremebeing.phoneanti.aichat.bean.SuggestBean
import com.supremebeing.phoneanti.aichat.bean.TokensBean
import com.supremebeing.phoneanti.aichat.bean.TouchBean
import com.supremebeing.phoneanti.aichat.bean.TranslateBean
import com.supremebeing.phoneanti.aichat.bean.UnlockBean
import io.reactivex.rxjava3.core.Observable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 *@ClassName: HttpService
 *@Description: 网络请求接口
 *@Author: Yqw
 *@Date: 2022/12/4 17:13
**/
interface HttpService {
    /**
     * 登录
     */
    @POST(HttpUrl.login)
    fun login(@Body body : RequestBody) : Observable<LoginBean>

    /**
     * ai列表
     */
    @GET(HttpUrl.anchor_list)
    fun aiList(
        @Query("filter[quick]") quick: String?,
        @Query("filter[result_type]") resulType:String?,
        @Query("filter[only_nsfw]") has_nsfw:Int?,
        @Query("per_page") per_page: Int?,
        @Query("page") page: Int?
    ) : Observable<AnchorBean>

    /**
     * 添加喜欢
     */
    @POST(HttpUrl.user_follow)
    fun addLike(@Body body : RequestBody) : Observable<LikeBean>

    /**
     * 取消喜欢
     */
    @POST(HttpUrl.cancelFollow)
    fun cancelLike(@Body body : RequestBody) : Observable<LikeBean>

    /**
     * 发送消息
     */
    @POST(HttpUrl.send_message)
    fun sendMessage(@Body body : RequestBody) : Observable<MessageBean>

    /**
     * 翻译消息
     */
    @POST(HttpUrl.translateLanguage)
    fun translate(@Body body : RequestBody) : Observable<TranslateBean>

    /**
     * 配置信息
     */
    @GET(HttpUrl.ini)
    fun initData() : Observable<IniBean>

    /**
     * 绑定socket
     */
    @POST(HttpUrl.user_bind)
    fun bindData(@Body body : RequestBody) : Observable<BaseBean>

    /**
     * 获取主播详情
     */
    @GET(HttpUrl.details)
    fun getData(@Query("user_id") user_id: Int?) : Observable<AnchorDetails>

    /**
     * 接受到消息
     */
    @GET(HttpUrl.receive_message)
    fun receiveMessage(
        @Query("filter[other_id]") other_id: Int?,
        @Query("filter[start_at]") start_at: Long?,
        @Query("filter[end_at]") end_at: Long?,
        @Query("limit_person") limit_person: Int?,
        @Query("per_page") per_page: Int?
    ) : Observable<ReceiveMessageBean>

    /**
     * 收到消息的用户信息
     */
    @POST(HttpUrl.receive_message_user)
    fun messageUserData(@Body body: RequestBody): Observable<MessageUserBean>

    /**
     * 权益详情
     */
    @GET(HttpUrl.rights_interests)
    fun rightsAndInterestsData() : Observable<BabyEquityBean>

    /**
     * 批量处理消息
     */
    @POST(HttpUrl.do_all_message)
    fun doAllMessage(@Body body : RequestBody) : Observable<BaseBean>

    /**
     * ai主动打招呼
     */
    @POST(HttpUrl.send_user_touch)
    fun aiSendMessage(@Body body : RequestBody) : Observable<TouchBean>

    /**
     * 签到数据
     */
    @GET(HttpUrl.signData)
    fun signList() : Observable<SignBean>

    /**
     * 签到
     */
    @POST(HttpUrl.signData)
    fun dailySign(@Body body : RequestBody) : Observable<DailyBean>

    /**
     * 系统检查入口
     */
    @POST(HttpUrl.setting)
    fun systemData(@Body body : RequestBody) : Observable<InitBean>

    /**
     * 退出登录
     */
    @POST(HttpUrl.sign_out)
    fun signOut() : Observable<BaseBean>

    /**
     * 删除账号
     */
    @POST(HttpUrl.delete_account)
    fun deleteAccount() : Observable<BaseBean>

    /**
     * 更新用户信息
     */
    @POST(HttpUrl.user_message)
    fun updateUserData(@Body body : RequestBody) : Observable<BaseBean>

    /**
     * 反馈信息
     */
    @POST(HttpUrl.feed_book)
    fun feedBack(@Body body : RequestBody) : Observable<BaseBean>

    /**
     * 收藏信息
     */
    @GET(HttpUrl.meFollowing)
    fun followingData(
        @Query("per_page") per_page : Int?,
        @Query("page") page : Int?
    ) : Observable<FollowingBean>

    /**
     * 看广告领金币
     */
    @POST(HttpUrl.get_rights)
    fun getRightApply(@Body body : RequestBody) : Observable<RightApplyBean>

    /**
     * 充值套餐
     */
    @GET(HttpUrl.token_list)
    fun tokenList() : Observable<TokensBean>

    /**
     * 充值
     */
    @POST(HttpUrl.token_amount)
    fun recharge(@Body body : RequestBody) : Observable<RechargeBean>

    /**
     * ai的卡片列表
     */
    @GET(HttpUrl.card_list)
    fun aiCardList(@Query("filter[user_id]") user_id: Int?,
                   @Query("page") page : Int?) : Observable<CardBean>

    /**
     * 解锁ai卡片
     */
    @POST(HttpUrl.card_unlock)
    fun unlockCard(@Body body : RequestBody) : Observable<UnlockBean>

    /**
     * 消息建议
     */
    @GET(HttpUrl.suggest_list)
    fun suggestData() : Observable<SuggestBean>

    /**
     * 更心封面
     */
    @POST(HttpUrl.cover_touch)
    fun refreshCover(@Body body : RequestBody) : Observable<BaseBean>

    /**
     * 删除消息
     */
    @POST(HttpUrl.do_all_message)
    fun deleteMessage(@Body body : RequestBody) : Observable<BaseBean>

    /**
     * 解锁所有卡片
     */
    @POST(HttpUrl.card_unlock)
    fun unlockAll(@Body body : RequestBody) : Observable<UnlockBean>

    /**
     * 获取我已解锁的卡片
     */
    @GET(HttpUrl.my_card)
    fun myCard(@Query("page") page : Int?) : Observable<CardBean>

    /**
     * 检测邮箱是否可用
     */
    @GET(HttpUrl.check_email)
    fun checkEmail(@Query("account") email : String) : Observable<BaseBean>

    /**
     * 已读消息
     */
    @POST(HttpUrl.read_message)
    fun readMessage(@Body body : RequestBody) : Observable<BaseBean>

    /**
     * 评价ai
     */
    @POST(HttpUrl.comment_ai)
    fun commentAi(@Body body : RequestBody) : Observable<BaseBean>

    /**
     * ai的评价内容
     */
    @GET(HttpUrl.ai_comment)
    fun aiComment(@Query("aid") aid : Int) : Observable<CommentBean>
}