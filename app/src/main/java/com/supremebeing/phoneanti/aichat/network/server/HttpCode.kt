package com.supremebeing.phoneanti.aichat.network.server

/**
 * Name: HttpCode
 * CreateBy: dww On 2021/8/5
 * Copyright:
 * Description: 网络请求返回码
 */
enum class HttpCode(val code: Int) {
    SUCCESS(0), //成功
    APP_UPDATE(426), //App需要升级
    ILLEGAL_REQUEST(422), //非法请求(-50)
    DEFAULT_ERROR(2333333), //全局通用错误
    UNAUTHORIZED(401), //登录权限不足，登录失败
    NOT_FOUND(404), //404 Not Found
    ILLEGAL_ARGUMENTS(500), //非法参数
    REFUSED(501), //拒绝请求
    CHECK_ERROR(403), //校验错误
    UNREGISTER(6001), //用户未注册
    EMAIL_USE(6002), //邮箱已被使用
    ACCOUNT_ERROR(6003), //账号或者密码错误
    ACCOUNT_FROZEN(6004), //账号或者设备已被冻结
    MORE_REGISTER(6005), //注册过多用户(-33)
    ACCOUNT_DELETE(6009), //用户账号已删除(The user has been deleted)
    EXAMINE_ANCHOR(6021), //主播申请审核中(Anchor application is under review)
    AMOUNT_LETTLE(2001), //余额不足(Insufficient funds.)
    NO_SURPORT_PAY(2011), //支付渠道不支持(Pay channel not support)
    COMMIT_ERROR(2101), //提现失败(Withdraw failed)
    ONLINE(1001), //对方不在线(Callee offline)
    BUSY(1002), //对方忙(Callee busy)
    CALL_END(1009), //通话已结束(he call has ended)
    MATCH_ERROR(1021), //用户匹配失败(User match failed)
    HAVE_TOKEN(7001), //任务代币已领取(Claimed)
    UNTASK(7002), //任务未完成(UnDone)
    INSUFFICIENT(2001), //余额不足
}