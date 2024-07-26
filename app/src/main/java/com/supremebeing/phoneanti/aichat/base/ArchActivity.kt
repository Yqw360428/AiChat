package com.supremebeing.phoneanti.aichat.base

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.supremebeing.phoneanti.aichat.R
import com.supremebeing.phoneanti.aichat.dialog.CheckedDialog
import com.supremebeing.phoneanti.aichat.dialog.LoadingDialog
import com.supremebeing.phoneanti.aichat.dialog.SignDialog
import com.supremebeing.phoneanti.aichat.event.RxBus
import com.supremebeing.phoneanti.aichat.event.RxEvents
import com.supremebeing.phoneanti.aichat.tool.AIP
import com.supremebeing.phoneanti.aichat.tool.getColor
import com.supremebeing.phoneanti.aichat.ui.chat.BigActivity
import com.supremebeing.phoneanti.aichat.ui.chat.ChatActivity
import com.supremebeing.phoneanti.aichat.ui.chat.InfoActivity
import com.supremebeing.phoneanti.aichat.ui.main.MainActivity
import com.supremebeing.phoneanti.aichat.ui.start.LoginActivity
import com.supremebeing.phoneanti.aichat.ui.start.StartActivity
import com.supremebeing.phoneanti.aichat.utils.ActivityUtil
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import java.lang.reflect.ParameterizedType

@Suppress("UNCHECKED_CAST", "DEPRECATION")
abstract class ArchActivity<VM : ArchViewModel, B : ViewBinding> : AppCompatActivity() {
    abstract fun initView()
    //带参回传的跳转
    private var resultActivity : ((ActivityResult)->Unit)? = null
    private val activityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        resultActivity?.invoke(it)
    }
    //请求权限回传
    private var resultPermissions : ((Map<String,Boolean>)->Unit)? = null
    private val permissionResult = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
        resultPermissions?.invoke(it)
    }

    lateinit var binding: B
    lateinit var viewModel: VM
    private val rxEvents = CompositeDisposable()

    private var loadingDialog : LoadingDialog? = null
    private var isUnLogin=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)//屏幕保持常亮
//        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)//禁止截屏和录屏
        val viewBindingClass = ((javaClass.genericSuperclass as ParameterizedType)).actualTypeArguments[1] as Class<*>
        binding = viewBindingClass.getMethod("inflate", LayoutInflater::class.java).invoke(viewBindingClass,layoutInflater) as B
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[((javaClass.genericSuperclass) as ParameterizedType).actualTypeArguments[0] as Class<VM>]
        immersion()
        initView()
        globalSign() //全局签到弹窗

        subscribeEvent(RxBus.receive(RxEvents.NoLogin::class.java).subscribe {
            if (!isUnLogin){
                Toast.makeText(this, "\"UNAUTHORIZED\"", Toast.LENGTH_SHORT).show()
                AIP.clearData()
                ActivityUtil.remoAllActivity()
                val intent=Intent(this, StartActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                isUnLogin=true
            }
        })
    }

    /**
     * 全局的签到弹窗功能
     */
    private var signDialog : SignDialog? = null
    private var checkedDialog : CheckedDialog? = null
    private var signList = mutableListOf<MultiItemEntity>()
    private fun globalSign(){
        subscribeEvent(RxBus.receive(RxEvents.GlobalSign::class.java).subscribe {
            if (this.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)){
                signDialog = SignDialog(this)
                it.signBean.data.checkins?.last()?.itemType = 2
                if (it.signBean.data.checkins != null) {
                    signList.clear()
                    signList.addAll(it.signBean.data.checkins)
                    signDialog?.setData(signList)
                    if (!it.signBean.data.today_checked){
                        signDialog?.showDialog(true)
                    }
                }

                signDialog?.signListener = {double->
                    RxBus.post(RxEvents.DailySign(double))
                }

                subscribeEvent(RxBus.receive(RxEvents.SignReward::class.java).subscribe {coin->
                    checkedDialog = CheckedDialog(this)
                    checkedDialog?.setCoin(coin.coin)
                    checkedDialog?.showDialog(false)

                    checkedDialog?.closeListener = {
                        signDialog?.dismiss()
                    }
                })

            }
        })
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev!!.action == MotionEvent.ACTION_DOWN) {
            val view = currentFocus
            if (isShouldHideKeyboard(view, ev)) {
                hideKeyboard()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    /**
     * 判断是否隐藏软键盘
     */
    private fun isShouldHideKeyboard(view: View?, event: MotionEvent): Boolean {
        if (view != null && (view is AppCompatEditText)) {
            val l = intArrayOf(0, 0)
            view.getLocationInWindow(l)

            val left = l[0]
            val top = l[1]
            val bottom = top + view.height
            val right = left + view.width

            return !(event.x > left && event.x < right &&
                    event.y > top && event.y < bottom)
        }
        return false
    }

    /**
     *@MethodName: launch
     *@Description: 普通跳转页面
     *@Author: Yqw
     *@Date: 2022/12/3 19:37
    **/
    protected fun launch(activity : Class<*>){
        startActivity(Intent(this,activity))
    }

    /**
     *@MethodName: launch
     *@Description: 带参数跳转页面
     *@Author: Yqw
     *@Date: 2022/12/3 19:38
    **/
    protected fun launch(activity: Class<*>,bundle: Bundle){
        startActivity(Intent(this,activity).putExtras(bundle))
    }

    /**
     *@MethodName: launch
     *@Description: 带有返回数据的跳转
     *@Author: Yqw
     *@Date: 2022/12/4 14:48
    **/

    protected fun launch(activity: Class<*>,resultActivity : (ActivityResult)->Unit){
        this.resultActivity = resultActivity
        activityResult.launch(Intent(this,activity))
    }

    /**
     *@MethodName: launch
     *@Description: 带参数且带有返回数据的跳转
     *@Author: Yqw
     *@Date: 2022/12/4 15:30
    **/
    protected fun launch(activity: Class<*>,bundle: Bundle,resultActivity: (ActivityResult) -> Unit){
        this.resultActivity = resultActivity
        activityResult.launch(Intent(this,activity).putExtras(bundle))
    }

    /**
     *@MethodName: launch
     *@Description: 请求权限的跳转
     *@Author: Yqw
     *@Date: 2022/12/4 15:34
    **/
    protected fun launch(permissions : Array<String>,resultPermissions : (Map<String,Boolean>)->Unit){
        this.resultPermissions = resultPermissions
        permissionResult.launch(permissions)
    }

    /**
     *@MethodName: immersion
     *@Description: 是否需要沉浸式状态栏
     *@Author: Yqw
     *@Date: 2022/12/3 21:03
    **/
    protected fun immersion(fullScreen : Boolean = true,statusBarLight : Boolean = false){
        WindowCompat.setDecorFitsSystemWindows(window,!fullScreen)
        val controller = WindowCompat.getInsetsController(window,binding.root)
        controller.isAppearanceLightStatusBars = statusBarLight//true是黑色，false是白色
        controller.isAppearanceLightNavigationBars = statusBarLight //true是深色 false是浅色
        if (fullScreen){
            window.navigationBarColor = Color.TRANSPARENT

            if (VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                window.isNavigationBarContrastEnforced = false
            }
            if (this is MainActivity) window.navigationBarColor = ContextCompat.getColor(this, R.color.black)
            if (this is ChatActivity || this is InfoActivity) {
                window.navigationBarColor = ContextCompat.getColor(this, R.color.black)
                window.statusBarColor = R.color.black.getColor()
            }
            if (navigationBarExit()){
                binding.root.setPadding(0,getStatusBarHeight(),0,getNavigationBarHeight())
            }else{
                binding.root.setPadding(0,getStatusBarHeight(),0,0)
            }
            if (this is BigActivity || this is StartActivity || this is LoginActivity){
                binding.root.setPadding(0,0,0,0)
            }
        }else{
            window.statusBarColor = ContextCompat.getColor(this, R.color.black)
            window.navigationBarColor = ContextCompat.getColor(this, R.color.black)
        }
    }

    /**
     *@MethodName: hideBars
     *@Description: 隐藏导航栏和状态栏
     *@Author: Yqw
     *@Date: 2023/1/4 9:46
    **/
    protected fun hideBars(){
        if (VERSION.SDK_INT>=Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
            window.insetsController?.hide(WindowInsets.Type.navigationBars())
        }else{
            runCatching {
                window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            }
        }
        if (VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
    }



    /**
     *@MethodName: navigationBarExit
     *@Description: 判断导航栏是否存在
     *@Author: Yqw
     *@Date: 2023/2/20 14:49
     **/
    private fun navigationBarExit() : Boolean{
        val windowManager = getSystemService(Service.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(displayMetrics)
        val realHeight = displayMetrics.heightPixels
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val displayHeight = displayMetrics.heightPixels

        return (realHeight - (displayHeight+getStatusBarHeight()))>40
    }

    /**
     *@MethodName: getStatusBarHeight
     *@Description: 获取状态栏高度
     *@Author: Yqw
     *@Date: 2022/12/3 21:02
    **/
    @SuppressLint("InternalInsetResource", "DiscouragedApi")
    protected fun getStatusBarHeight() : Int{
        return resources.getDimensionPixelSize(resources.getIdentifier("status_bar_height", "dimen", "android"))
    }

    /**
     *@MethodName: getNavigationBarHeight
     *@Description: 获取底部导航栏高度
     *@Author: Yqw
     *@Date: 2022/12/6 21:18
     **/
    @SuppressLint("InternalInsetResource", "DiscouragedApi")
    protected fun getNavigationBarHeight() : Int{
        return resources.getDimensionPixelSize(resources.getIdentifier("navigation_bar_height", "dimen", "android"))
    }

    /**
     *@MethodName: showLoading
     *@Description: 显示加载弹窗
     *@Author: Yqw
     *@Date: 2022/12/29 16:45
     **/
    protected fun showLoading(){
        if (loadingDialog == null){
            loadingDialog = LoadingDialog(this)
        }
        if (!loadingDialog!!.isShowing){
            loadingDialog?.show()
        }
    }

    /**
     *@MethodName: dismissLoading
     *@Description: 隐藏加载弹窗
     *@Author: Yqw
     *@Date: 2022/12/29 16:45
     **/
    protected fun dismissLoading(){
        if (loadingDialog!=null){
            if (loadingDialog!!.isShowing){
                loadingDialog?.dismiss()
            }
        }
    }

    /**
     *@ClassName: subscribeEvent
     *@Description: event接收信息
     *@Author: Yqw
     *@Date: 2022/12/4 14:42
     **/
    protected fun subscribeEvent(event : Disposable){
        rxEvents.add(event)
    }

    // 隐藏键盘的函数
    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (loadingDialog != null){
            loadingDialog?.dismiss()
            loadingDialog = null
        }
        rxEvents.dispose()
    }

}