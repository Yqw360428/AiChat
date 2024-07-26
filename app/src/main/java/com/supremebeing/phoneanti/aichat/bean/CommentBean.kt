package com.supremebeing.phoneanti.aichat.bean

data class CommentBean(
    val  data : Data
) : BaseBean(){
    data class Data(
        val comments : List<Comment>
    ){
        data class Comment(
            val rating : Float,
            val content : String
        ){
            override fun toString() : String {
                return "Comment(rating=$rating, content='$content')"
            }
        }

        override fun toString() : String {
            return "Data(comments=$comments)"
        }

    }

    override fun toString() : String {
        return "CommentBean(data=$data)"
    }

}
