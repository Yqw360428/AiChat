package com.supremebeing.phoneanti.aichat.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.core.DrawerPopupView
import com.lxj.xpopup.enums.PopupPosition
import java.lang.reflect.ParameterizedType

/**
 *@ClassName: BaseBindingFullDialog
 *@Description: 全屏弹窗
 *@Author: Yqw
 *@Date: 2022/12/5 10:18
 **/
@Suppress("UNCHECKED_CAST")
abstract class ArchDrawerDialog<B : ViewBinding>(context: Context) : DrawerPopupView(context) {
    lateinit var binding: B
    init {
        drawerContentContainer.addView(View(context))
    }

    abstract fun initView()

    override fun onCreate() {
        super.onCreate()
        val viewBindingClass =
            ((javaClass.genericSuperclass as ParameterizedType)).actualTypeArguments[0] as Class<*>
        binding = viewBindingClass.getMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        )
            .invoke(viewBindingClass, activity.layoutInflater, parent, false) as B
        drawerContentContainer.removeAllViews()
        drawerContentContainer.addView(binding.root)
        initView()
    }

    /**
     * 直接调用show()会空指针
     */
    fun showDialog(outside: Boolean) {
        val dialog = XPopup.Builder(context)
            .dismissOnTouchOutside(outside)
            .popupPosition(PopupPosition.Left)
            .asCustom(this)
        dialog.popupInfo.autoFocusEditText = false
        dialog.popupInfo.autoOpenSoftInput = false
        dialog.show()
    }

    @Deprecated(
        "This method will throws NullPointException,call showDialog() instead",
        ReplaceWith("super.show()", "com.lxj.xpopup.core.BottomPopupView")
    )
    override fun show(): BasePopupView {
        return super.show()
    }

    /**
     * Dismiss监听
     */
    private var onDismissListener: (() -> Unit)? = null

    fun setOnDismissListener(listener: (() -> Unit)) {
        this.onDismissListener = listener
    }

    override fun onDismiss() {
        super.onDismiss()
        onDismissListener?.invoke()
    }
}