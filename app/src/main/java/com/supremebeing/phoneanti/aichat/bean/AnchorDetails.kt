package com.supremebeing.phoneanti.aichat.bean

data class AnchorDetails(val data : Details) : BaseBean() {
    data class Details(
        val anchor: Anchor?=null,
        val call: Call,
        val follower_count: Int,
        val media_count:Int,
        var id: Int,
        val is_blocked: Boolean,
        val is_busy: Boolean,
        val is_online: Boolean,
        val profile: ProfileAnchor,
        val hearts:Heart,
        val role: Int? = null,
        var is_hot:Boolean,
        var is_new:Boolean,
        var chat:ChatBean,
        var photos:ArrayList<String>,
        var need_vip:Boolean=false,
        var is_nsfw:Boolean=false,
        var is_following: Boolean=false,
        val tags: List<Tag>? = null,
        val is_commented : Boolean = false
    ){
        override fun toString() : String {
            return "Details(anchor=$anchor, call=$call, follower_count=$follower_count, media_count=$media_count, id=$id, is_blocked=$is_blocked, is_busy=$is_busy, is_online=$is_online, profile=$profile, hearts=$hearts, role=$role, is_hot=$is_hot, is_new=$is_new, chat=$chat, photos=$photos, need_vip=$need_vip, is_nsfw=$is_nsfw, is_following=$is_following, tags=$tags, is_commented=$is_commented)"
        }
    }
}