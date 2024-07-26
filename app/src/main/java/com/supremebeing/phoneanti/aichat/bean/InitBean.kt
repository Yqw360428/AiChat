package com.supremebeing.phoneanti.aichat.bean

data class InitBean(val data : Init? = null) : BaseBean(){
    data class Init(
        var region: ArrayList<Region>? = null,
        var last: Last?=null
    ){
        data class Last(
            var version: String,
            val ini_version: String,
            val is_force: Int = 0,
            val change_log: String,
            val tips: String,
            val link: String,
            val maintain: Boolean = false,
            val ignore: ArrayList<String>? = null
        ){
            override fun toString() : String {
                return "Last(version='$version', ini_version='$ini_version', is_force=$is_force, change_log='$change_log', tips='$tips', link='$link', maintain=$maintain, ignore=$ignore)"
            }
        }

        data class Region(
            var im: String,
            var api: String
        ){
            override fun toString() : String {
                return "Region(im='$im', api='$api')"
            }
        }

        override fun toString() : String {
            return "Init(last=$last, region=$region)"
        }


    }

    override fun toString() : String {
        return "InitBean(data=$data)"
    }

}
