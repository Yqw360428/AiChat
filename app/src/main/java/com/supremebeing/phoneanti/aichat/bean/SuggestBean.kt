package com.supremebeing.phoneanti.aichat.bean

data class SuggestBean(val data : Data) : BaseBean(){
    data class Data(
        val suggests : ArrayList<String>
    ){
        override fun toString() : String {
            return "Data(suggests=$suggests)"
        }
    }

    override fun toString() : String {
        return "SuggestBean(data=$data)"
    }

}
