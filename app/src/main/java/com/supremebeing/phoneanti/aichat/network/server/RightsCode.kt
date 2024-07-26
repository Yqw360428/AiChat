package com.supremebeing.phoneanti.aichat.network.server

enum class RightsCode(val code: String) {
    FIRSTTOKEN("first_token"),  //首冲权益
    VIPDRAWTOKEN("vip_draw_token"),  //vip领金币权益
    NEWUSERDRAWTOKEN("new_user_draw_token"),  //新用户领取金币
    TOKENFREETOKEN("token_free_token"),  //领取免费金币
    TOKENFORTOKEN("token_for_token"),  //第二次充值权益
    TOKENFORTOKENSAMEDAY("token_for_token_same_day"),  //今日领取金币权益
    TOKENFORTOKENNEXTDAY("token_for_token_next_day"),  //次日领取金币权益
    FIRSTBADCOMMENTREWARD("first_bad_comment_reward")  //差评奖励权益
}