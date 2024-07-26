@file:Suppress("DEPRECATION")

package com.supremebeing.phoneanti.aichat.tool

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.graphics.Bitmap
import android.graphics.LinearGradient
import android.graphics.Outline
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Environment
import android.telephony.TelephonyManager
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.supremebeing.phoneanti.aichat.App
import com.supremebeing.phoneanti.aichat.R
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.NetworkInterface
import java.security.MessageDigest
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.Collections
import java.util.regex.Pattern


/**
 * 获得App签名文件的sha1值
 *
 * @param context
 * @return
 */
fun getAppSignSha1(): String {
    try {
        val packageManager = App.instance.packageManager
        val packageInfo = packageManager.getPackageInfo(
            App.instance.packageName,
            PackageManager.GET_SIGNATURES
        )
        // X509证书，X.509是一种非常通用的证书格式
        val signs: Array<Signature> = packageInfo.signatures
        val sign: Signature = signs[0]
        val certFactory = CertificateFactory.getInstance("X.509")
        val cert =
            certFactory.generateCertificate(ByteArrayInputStream(sign.toByteArray())) as X509Certificate
        // sha1
        val md = MessageDigest.getInstance("SHA1")
        // 获得公钥
        val b = md.digest(cert.encoded)
        return bytes2HexFormatted(b)
    } catch (_ : Exception) {
    }
    return ""
}

/**
 * 将获取到得编码进行16进制转换
 *
 * @param arr
 * @return
 */
private fun bytes2HexFormatted(arr: ByteArray): String {
    val str = StringBuilder(arr.size * 2)
    for (i in arr.indices) {
        var h = Integer.toHexString(arr[i].toInt())
        val l = h.length
        if (l == 1) h = "0$h"
        if (l > 2) h = h.substring(l - 2, l)
        str.append(h.uppercase())
        if (i < arr.size - 1) str.append(':')
    }
    return str.toString()
}

fun actionIntent(context: Context){
    val googleApp = "https://play.google.com/store/apps/details?id=${context.packageName}"
    val marketApp = "market://details?id=${context.packageName}"
    val toast = "You don not have an available app store nor a browser!"

    val intent = Intent(Intent.ACTION_VIEW)
    intent.data =
        Uri.parse(marketApp) //跳转到应用市场，非Google Play市场一般情况也实现了这个接口
    //存在手机里没安装应用市场的情况，跳转会包异常，做一个接收判断
    if (intent.resolveActivity(context.packageManager) != null) { //可以接收
        context.startActivity(intent)
    } else { //没有应用市场，我们通过浏览器跳转到Google Play
        intent.data = Uri.parse(googleApp)
        //这里存在一个极端情况就是有些用户浏览器也没有，再判断一次
        if (intent.resolveActivity(context.packageManager) != null) { //有浏览器
            context.startActivity(intent)
        } else {
            toast.toastShort
        }
    }
}

//文字颜色渐变
fun setGradientFont(view: AppCompatTextView, colors : IntArray) {
    view.measure(View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED),0)
    val floats = floatArrayOf(0.5f,1.0f)
    val linearGradient =
        LinearGradient(0f, 0f,
            view.measuredWidth.toFloat(),
            0f,
            colors,
            floats,
            Shader.TileMode.CLAMP)
    view.paint.shader = linearGradient
    view.invalidate()
}


/**
 * 是否有vpn
 */
fun isDeviceInVPN() : Boolean{
    try {
        val all: List<NetworkInterface> =
            Collections.list(NetworkInterface.getNetworkInterfaces())
        for (nif in all) {
            if (nif.name.equals("tun0") || nif.name.equals("ppp0")) {
                Log.i("TAG", "isDeviceInVPN  current device is in VPN.")
                return true
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return false
}

/**
 * 判断是否包含SIM卡
 * @return 状态
 */
fun hasSimCard(context: Context): Boolean {
    val telMgr = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    val simState = telMgr.simState
    var result = true
    when (simState) {
        TelephonyManager.SIM_STATE_ABSENT -> result = false // 没有SIM卡
        TelephonyManager.SIM_STATE_UNKNOWN -> result = false
    }
    Log.d("try", if (result) "有SIM卡" else "无SIM卡")
    return result
//        return true
}

//控件圆角
fun setOutline(view : View,radius : Float){
    view.outlineProvider = object : ViewOutlineProvider(){
        override fun getOutline(view: View, outline: Outline) {
            outline.setRoundRect(0,0,view.width,view.height,radius)
        }
    }
    view.clipToOutline = true
}

fun networkEnable(context : Context) : Boolean{
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val info = connectivityManager.activeNetworkInfo
    return info?.isAvailable ?: false
}

fun dpToPx(dp: Float): Int {
    val scale: Float = App.instance.resources.displayMetrics.density
    return (dp * scale + 0.5f).toInt()
}

//隐藏控件
fun View.gone(){
    this.isGone = true
}

//显示控件
fun View.visible(){
    this.isVisible = true
}

fun View.invisible(){
    this.isInvisible = true
}

fun Int.getColor() : Int{
    return ContextCompat.getColor(App.instance,this)
}

fun Int.getDrawable() : Drawable{
    return ContextCompat.getDrawable(App.instance,this)!!
}

/**
 *@MethodName: setOnSingleClick
 *@Description: 调用控件的点击事件
 *@Author: Yqw
 *@Date: 2023/4/21 18:10
 **/
var lastClickTime = 0L
const val internalTime = 500L
fun View.setOnSingleClick(onClick : (View)->Unit){
    this.setOnClickListener {
        if (lastClickTime != 0L && (System.currentTimeMillis()- lastClickTime)< internalTime){
//            lastClickTime = System.currentTimeMillis()
//            "Do not click repeatedly!".toastShort
            return@setOnClickListener
        }else{
            lastClickTime = System.currentTimeMillis()
            onClick.invoke(it)
        }
    }
}


/**
 *@MethodName: setOnSingleClick
 *@Description: 绑定所需要的控件响应点击事件
 *@Author: Yqw
 *@Date: 2023/4/21 18:09
 **/
fun setOnSingleClick(vararg views : View,onClick : (View)->Unit){
    views.forEach {
        it.setOnClickListener { view->
            if (lastClickTime != 0L && (System.currentTimeMillis()- lastClickTime)< internalTime){
//                lastClickTime = System.currentTimeMillis()
                return@setOnClickListener
            }else{
                lastClickTime = System.currentTimeMillis()
                onClick.invoke(view)
            }
        }
    }
}

/**
 *@MethodName: toastShort
 *@Description: 短时间传String的short
 *@Author: Yqw
 *@Date: 2022/12/7 11:31
 **/
val String.toastShort
    get() = Toast.makeText(App.instance, this, Toast.LENGTH_SHORT).show()

/**
 *@MethodName: toastShort
 *@Description: 短时间传@string的short
 *@Author: Yqw
 *@Date: 2022/12/7 11:33
 **/
val Int.toastShort
    get() = Toast.makeText(App.instance, App.instance.getString(this), Toast.LENGTH_SHORT).show()

/**
 *@MethodName: toastLong
 *@Description: 长时间传String的short
 *@Author: Yqw
 *@Date: 2022/12/7 11:36
 **/
val String.toastLong
    get() = Toast.makeText(App.instance, this, Toast.LENGTH_LONG).show()

/**
 *@MethodName: toastLong
 *@Description: 长时间传@string的short
 *@Author: Yqw
 *@Date: 2022/12/7 11:37
 **/
val Int.toastLong
    get() = Toast.makeText(App.instance, this, Toast.LENGTH_LONG).show()

fun Float.dpToPx() = this * App.instance.resources.displayMetrics.density

// 两次点击按钮之间的点击间隔不能少于1000毫秒
private const val MIN_CLICK_DELAY_TIME = 1000
private var lastClickTime1: Long = 0
fun isFastClick(): Boolean{
    var flag = false
    val curClickTime = System.currentTimeMillis()
    if (curClickTime - lastClickTime1 >= MIN_CLICK_DELAY_TIME) {
        flag = true
    }
    lastClickTime1 = curClickTime
    return flag
}

//设置关键词样式
fun setSpannable(content: String, keys: Array<String>,color : Int): SpannableString {
    val msp = SpannableString(content)
    for (index in keys.indices){
        val p = Pattern.compile(keys[index])
        val m = p.matcher(msp)
        while (m.find()){
            msp.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(App.instance,color)),
                m.start(),
                m.end(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//            msp.setSpan(StyleSpan(Typeface.BOLD),m.start(),m.end(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//            msp.setSpan(AbsoluteSizeSpan(25,true),m.start(),m.end(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//            msp.setSpan(UnderlineSpan(),m.start(),m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
    return msp
}

/**
 * 保存图片到本地
 *
 * @param name   图片的名字，比如传入“123”，最终保存的图片为“123.jpg”
 * @param bitmap    本地图片或者网络图片转成的Bitmap格式的文件
 * @return
 */
fun saveImage(context : Context,cover : String) {
    val pathFile =
        File("${Environment.getExternalStorageDirectory()}${File.separator}${Environment.DIRECTORY_PICTURES}${File.separator}")
    if (! pathFile.exists()) {
        pathFile.mkdir()
    }
    val file = File(pathFile, "${System.currentTimeMillis()}.jpg")
    try {
        val fos = FileOutputStream(file)
        Glide.with(context)
            .asBitmap()
            .load(cover)
            .into(object : CustomTarget<Bitmap>(){
                override fun onResourceReady(
                    resource : Bitmap,
                    transition : Transition<in Bitmap>?
                ) {
                    resource.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                    fos.flush()
                    fos.close()
                    // 最后通知图库更新
                    context.getString(R.string.download_successful).toastShort
                    val localUri = Uri.fromFile(file)
                    val localIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri)
                    context.sendBroadcast(localIntent)
                }
                override fun onLoadCleared(placeholder : Drawable?) {}

            })
    } catch (e : IOException) {
        e.printStackTrace()
    }
}
