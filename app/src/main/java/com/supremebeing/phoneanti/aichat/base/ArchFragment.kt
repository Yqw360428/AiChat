package com.supremebeing.phoneanti.aichat.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.LogUtils
import com.supremebeing.phoneanti.aichat.dialog.LoadingDialog
import com.supremebeing.phoneanti.aichat.tool.setOnSingleClick
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import java.lang.reflect.ParameterizedType


@Suppress("UNCHECKED_CAST")
abstract class ArchFragment<VM : ViewModel, B : ViewBinding> : Fragment() {
    lateinit var binding: B
    lateinit var viewModel: VM
    private var isNavigationViewInit = false//记录是否已经初始化过一次视图
    private var lastView: View? = null//记录上次创建的view
    private var loadingDialog : LoadingDialog? = null
    private val rxEvents = CompositeDisposable()

    abstract fun initView()


    //带参回传的跳转
    private var resultActivity : ((activityResult : ActivityResult)->Unit)? = null
    private val activityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        resultActivity?.invoke(it)
    }
    //请求权限回传
    private var resultPermissions : ((resultPermissions : Map<String,Boolean>)->Unit)? = null
    private val permissionResult = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
        resultPermissions?.invoke(it)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //如果fragment的view已经创建则不再重新创建
        if (lastView == null) {
            val viewBindingClass = ((javaClass.genericSuperclass as ParameterizedType)).actualTypeArguments[1] as Class<*>
            binding = viewBindingClass.getMethod("inflate", LayoutInflater::class.java).invoke(viewBindingClass,layoutInflater) as B
            lastView = binding.root
        }
        lastView?.isClickable = true
        return lastView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (!isNavigationViewInit) {//初始化过视图则不再进行view和data初始化
            super.onViewCreated(view, savedInstanceState)
            viewModel = ViewModelProvider(this)[(javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<VM>]
            initView()

            isNavigationViewInit = true
        }
    }

    /**
     *@MethodName: launch
     *@Description: 普通跳转页面
     *@Author: Yqw
     *@Date: 2022/12/3 19:37
     **/
    protected fun launch(activity : Class<*>){
        startActivity(Intent(requireContext(),activity))
    }

    /**
     *@MethodName: launch
     *@Description: 带参数跳转页面
     *@Author: Yqw
     *@Date: 2022/12/3 19:38
     **/
    protected fun launch(activity: Class<*>,bundle: Bundle){
        startActivity(Intent(requireContext(),activity).putExtras(bundle))
    }

    /**
     *@MethodName: launch
     *@Description: 带有返回数据的跳转
     *@Author: Yqw
     *@Date: 2022/12/4 14:48
     **/

    protected fun launch(activity: Class<*>,resultActivity : ((activityResult : ActivityResult)->Unit)){
        this.resultActivity = resultActivity
        activityResult.launch(Intent(requireContext(),activity))
    }

    /**
     *@MethodName: launch
     *@Description: 带参数且带有返回数据的跳转
     *@Author: Yqw
     *@Date: 2022/12/4 15:30
     **/
    protected fun launch(activity: Class<*>,bundle: Bundle,resultActivity: (activityResult: ActivityResult) -> Unit){
        this.resultActivity = resultActivity
        activityResult.launch(Intent(requireContext(),activity).putExtras(bundle))
    }

    /**
     *@MethodName: launch
     *@Description: 请求权限的跳转
     *@Author: Yqw
     *@Date: 2022/12/4 15:34
     **/
    protected fun launch(permissions : Array<String>,resultPermissions : (resultPermissions : Map<String,Boolean>)->Unit){
        this.resultPermissions = resultPermissions
        permissionResult.launch(permissions)
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

    /**
     *@MethodName: showLoading
     *@Description: 显示加载弹窗
     *@Author: Yqw
     *@Date: 2022/12/29 16:45
     **/
    protected fun showLoading(){
        if (loadingDialog == null){
            loadingDialog = LoadingDialog(requireContext())
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
     *@MethodName: getStatusBarHeight
     *@Description: 获取状态栏高度
     *@Author: Yqw
     *@Date: 2022/12/3 21:02
     **/
    @SuppressLint("InternalInsetResource", "DiscouragedApi")
    protected fun getStatusBarHeight() : Int{
        return resources.getDimensionPixelSize(resources.getIdentifier("status_bar_height", "dimen", "android"))
    }

    // 隐藏键盘的函数
    protected fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
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