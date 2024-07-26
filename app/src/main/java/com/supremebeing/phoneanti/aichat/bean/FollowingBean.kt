package com.supremebeing.phoneanti.aichat.bean

data class FollowingBean(val data : Data) : BaseBean(){
    data class Data(val followings:ArrayList<Follower>?){
        data class Follower(
            var id: Int = 0,
            val profile: Profile? = null,
            val role: Int = 0,
            val is_online: Boolean = false,
            val is_busy: Boolean = false,
            val followed_at: Long = 0,
            var is_following: Boolean = false
        ){
            override fun toString() : String {
                return "Follower(id=$id, profile=$profile, role=$role, is_online=$is_online, is_busy=$is_busy, followed_at=$followed_at, is_following=$is_following)"
            }
        }

        override fun toString() : String {
            return "Data(followings=$followings)"
        }

    }

    override fun toString() : String {
        return "FollowingBean(data=$data)"
    }
}
