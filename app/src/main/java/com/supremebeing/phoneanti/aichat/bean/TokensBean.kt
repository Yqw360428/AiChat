package com.supremebeing.phoneanti.aichat.bean


data class TokensBean(val data : Data) : BaseBean(){
    data class Data(val tokens: ArrayList<Stoken>?){
        data class Stoken(
            var gid: Int = 0,
            val name: String? = null,
            val icon: Icon? = null,
            val items: List<Item>
        ){
            data class Icon(
                var normal: String? = null,
                val selected: String? = null
            ){
                override fun toString() : String {
                    return "Icon(normal=$normal, selected=$selected)"
                }
            }
            data class Item(
                var id: Int = 0,
                var icon: Int = 0,
                val code: String,
                val price: String? = null,
                val token_amount: Int = 0,
                val original_token_amount: Int = 0,
                val inapp_product_id: String? = null,
                val name: String? = null,
                val price_desc: String? = null,
                val type: Int = 0,
                var res: Int = 0,
                var showPopular:Boolean
            ){
                override fun toString() : String {
                    return "Item(id=$id, icon=$icon, code='$code', price=$price, token_amount=$token_amount, original_token_amount=$original_token_amount, inapp_product_id=$inapp_product_id, name=$name, price_desc=$price_desc, type=$type, res=$res, showPopular=$showPopular)"
                }
            }
        }
        override fun toString() : String {
            return "Data(tokens=$tokens)"
        }

    }
    override fun toString() : String {
        return "TokensBean(data=$data)"
    }

}
