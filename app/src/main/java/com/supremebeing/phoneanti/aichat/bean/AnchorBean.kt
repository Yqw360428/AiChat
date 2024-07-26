package com.supremebeing.phoneanti.aichat.bean

data class AnchorBean(val data : Anchors) : BaseBean(){
    data class Anchors(val anchors : ArrayList<AnchorDetail>? = null){
        override fun toString() : String {
            return "Anchors(anchors=$anchors)"
        }
    }

    override fun toString() : String {
        return "AnchorBean(data=$data)"
    }

}