package com.supremebeing.phoneanti.aichat.network.server

/**
 *@ClassName: HttpUrl
 *@Description: 网络请求地址
 *@Author: Yqw
 *@Date: 2022/12/4 17:13
**/
object HttpUrl {
    //登录
    const val login = "login"
    //ai列表
    const val anchor_list = "anchor/list"
    //关注
    const val user_follow = "user/follow"
    //取消关注
    const val cancelFollow = "user/unfollow"
    //发送消息
    const val send_message = "chat/message"
    //翻译
    const val translateLanguage = "trans"
    //获取配置
    const val ini = "ini"
    //绑定socekt
    const val user_bind = "pusher/bind"
    //主播详情
    const val details = "user/detail"
    //接收消息
    const val receive_message = "chat/message/list"
    //处理消息用户
    const val receive_message_user = "user/summary/list"
    //权益详情
    const val rights_interests = "right"
    //批量处理消息
    const val do_all_message = "chat/messages/remove"
    //ai主动打招呼
    const val send_user_touch="user/touch"
    //签到数据
    const val signData = "checkin"
    //setting
    const val setting = "init"
    //退出登录
    const val sign_out = "logout"
    //删除账号
    const val delete_account = "user/destroy"
    //用户信息
    const val user_message = "user"
    //意见反馈
    const val feed_book = "feedback"
    //我的收藏
    const val meFollowing = "user/followings"
    //领取权益
    const val get_rights = "right/apply"
    //充值套餐
    const val token_list = "token/list"
    //用户账户余额
    const val token_amount = "user/token"
    //卡片列表
    const val card_list = "media/list"
    //解锁卡片
    const val card_unlock = "media/unlock"
    //消息建议
    const val suggest_list = "chat/suggest"
    //更新封面
    const val cover_touch = "user/touch"
    //我的卡片
    const val my_card = "media/unlock/list"
    //邮箱检测
    const val check_email = "email"
    //已读消息
    const val read_message = "chat/messages/read"
    //评价ai
    const val comment_ai = "anchor/comment"
    //ai的评价内容
    const val ai_comment = "anchor/comments"
}