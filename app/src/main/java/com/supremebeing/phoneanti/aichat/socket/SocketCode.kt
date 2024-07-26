package com.supremebeing.phoneanti.aichat.socket

/**
 * Name: HttpCode
 * CreateBy: dww On 2021/8/5
 * Copyright:
 * Description: 网络请求返回码
 */
enum class SocketCode(val code: Int) {
    SUCCESS(0), //socket链接成功
    NEW_MESSAGE_AI(111), //有新消息
    NEW_MESSAGE(112), //有新消息
    NEW_CALL(101), //新通话
    ANSWER_CALL(102), //接听
    HANGUP_CALL(103), //挂断
    SEND_GIFT_CALL(104), //送礼物
    ASK_FOR_GIFT(105), //主播索要礼物
    UPDATE_ACCOUNT(200), //更新账户余额
    MATCH_PERSON(311), //匹配到用户
    OTHER_SURE(312), //对方已确认
    OTHER_REFUSE(313), //对方已拒绝
    REFRESH_USERINFO(301), //更新用户状态
    RIGHTS(600) //用户权益
}