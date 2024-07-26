package com.supremebeing.phoneanti.aichat.bean

data class RechargeBean(
    val data: Data,
    var gid: Int = 0,
    var code: String,
    var receipt: String,
    var saveEvent: Boolean = false,
    var isRepeat: Boolean = false,
    var isSecondRecharge: Boolean = false
): BaseBean(){
    data class Data(
        var link: String? = null,
        val success: String? = null,
        val failed: String? = null
    ){
        override fun toString() : String {
            return "Data(link=$link, success=$success, failed=$failed)"
        }
    }

    override fun toString() : String {
        return "RechargeBean(data=$data, gid=$gid, code='$code', receipt='$receipt', saveEvent=$saveEvent, isRepeat=$isRepeat, isSecondRecharge=$isSecondRecharge)"
    }

}
