package com.supremebeing.phoneanti.aichat.bean

data class Profile(
    var age : Int = 0,
    val attrs : Attrs? = null,
    val birthday: String="",
    val cover: String="",
    val gender: Int=0,
    var head: String = "",
    val location: Location?=null,
    val mood: String = "",
    var nickname: String = "",
    val settings: Settings?=null,
    val vip:Vip?=null
)

data class Attrs(
    val body_type: String = "",
    val height: String = "",
    val weight: String = "",
    var belong: String = "",
    val constellation: String = "",
    val hair_color: String = ""
){
    override fun toString() : String {
        return "Attrs(body_type=$body_type, height=$height, weight=$weight, belong=$belong, constellation=$constellation, hair_color=$hair_color)"
    }
}

data class Location(
    val city: String="",
    val country: String="",
    val country_code: String="",
    val lat: Double,
    val lng: Double
){
    override fun toString() : String {
        return "Location(city=$city, country=$country, country_code=$country_code, lat=$lat, lng=$lng)"
    }
}

data class Settings(
    val incognito_mode: Boolean,
    val show_location: Boolean
){
    override fun toString() : String {
        return "Settings(incognito_mode=$incognito_mode, show_location=$show_location)"
    }
}

data class Vip(
    val expired_at: Long,//vip到期时间
    val token: ArrayList<Token>? = null
){
    override fun toString() : String {
        return "Vip(expired_at=$expired_at, token=$token)"
    }
}

data class Token(
    private var gid: Int = 0,
    val code: String = "",
    val price: String = "",
    val original_price: String = ""
){
    override fun toString() : String {
        return "Token(gid=$gid, code=$code, price=$price, original_price=$original_price)"
    }
}

data class Test(
    var recharge: String = "",
    val recharge_show: String = "",
    val audience_hunting: String = ""
){
    override fun toString() : String {
        return "Test(recharge=$recharge, recharge_show=$recharge_show, audience_hunting=$audience_hunting)"
    }
}

data class AnchorDetail(
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
    val role: Int,
    var is_hot:Boolean,
    var is_new:Boolean,
    var chat:ChatBean,
    var photos:ArrayList<String>,
    var need_vip:Boolean=false,
    var is_nsfw:Boolean=false,
    var is_following: Boolean=false,
    val tags: List<Tag>? = null
) : BaseBean(){
    override fun toString() : String {
        return "AnchorDetail(anchor=$anchor, call=$call, follower_count=$follower_count, media_count=$media_count, id=$id, is_blocked=$is_blocked, is_busy=$is_busy, is_online=$is_online, profile=$profile, hearts=$hearts, role=$role, is_hot=$is_hot, is_new=$is_new, chat=$chat, photos=$photos, need_vip=$need_vip, is_nsfw=$is_nsfw, is_following=$is_following)"
    }
}

data class Anchor(
    val comment: CommentNope?,
    val free_call: Boolean,
    val is_hot: Boolean,
    var is_new:Boolean,
//    val price: MsgPrice?,
    val tags: List<Tag>
){
    override fun toString() : String {
        return "Anchor(comment=$comment, free_call=$free_call, is_hot=$is_hot, tags=$tags)"
    }
}

data class CommentNope(
    val nice_count: Int,
    val nope_count: Int,
    val rating: String
){
    override fun toString() : String {
        return "CommentNope(nice_count=$nice_count, nope_count=$nope_count, rating='$rating')"
    }
}

data class Tag(
    val name: String,
    val style: Style
){
    override fun toString() : String {
        return "Tag(name='$name', style=$style)"
    }
}

data class Style(
    val color: String
){
    override fun toString() : String {
        return "Style(color='$color')"
    }
}

data class Call(
    val price: Int
){
    override fun toString() : String {
        return "Call(price=$price)"
    }
}

data class ProfileAnchor(
    val age: Int,
    val attrs: AttrsBody?=null,
    var cover: String,
    val gender: Int,
    val head: String,
    val location: Location?=null,
    val mood: String?=null,
    val nickname: String,
    val vip: Vip
){
    override fun toString() : String {
        return "ProfileAnchor(age=$age, attrs=$attrs, cover='$cover', gender=$gender, head='$head', location=$location, mood=$mood, nickname='$nickname', vip=$vip)"
    }
}

data class AttrsBody(
    var body_type: String?=null,
    var height: String?=null,
    var weight:String?=null,
    var hair_color:String?=null,
    var belong:String?=null,
    var constellation:String?=null
){
    override fun toString() : String {
        return "AttrsBody(body_type=$body_type, height=$height, weight=$weight, hair_color=$hair_color, belong=$belong, constellation=$constellation)"
    }
}

data class Heart(
    val value: Int?=null,
    val level:Int,
    val level_min_exp:Int,
    val level_max_exp:Int
){
    override fun toString() : String {
        return "Heart(value=$value, level=$level, level_min_exp=$level_min_exp, level_max_exp=$level_max_exp)"
    }
}

data class ChatBean(
    val price: Int
){
    override fun toString() : String {
        return "ChatBean(price=$price)"
    }
}

data class ReceiveMessage(
    var messages: ArrayList<Message>?=null
)

data class Message(
    var msg_id: String,
    val sender_id: Int = 0,
    val to: Int = 0,
    val to_type: Int = 0,
    val content: MessageContent,
    val sent_at: Long = 0,
    val delivered_at: Long = 0,
    val read_at: Long = 0
)

data class MessageContent(
    var text: String? = null,
    val type: Int = 0,
    val url: String? = null,
    val media_id: String? = null,
    val gift_id: Int = 0,
    val is_ended:Boolean=false //消息是否结束
)

data class MessageUsers(
    var users: ArrayList<MessageUser>
)

data class MessageUser(
    var id: Int = 0,
    val profile: Profile? = null,
    val role: Int = 0
)

data class SocketMessage(
    val event_code:Int,val data:MessageData
)

data class MessageData(
    val msg_id:String,
    val sent_at:Long,
    val sender_id:Int,
    val to:Int,
    val to_type:Int,

    val updated_at:Long,
    val content:SocketMessageContent
)

data class SocketMessageContent(
    val is_ended:Boolean=false,
    val text:String,
    val type:Int
)

data class FirstRecharge(
    val code: String,
    val original_price: Double,
    val original_token_amount: Int,
    val price: String,
    val token_amount: Int
)
