package com.supremebeing.phoneanti.aichat.utils

import android.app.Activity
import android.graphics.Rect
import android.view.View

/**
 *@ClassName: SoftKeyBoardListener
 *@Description: 键盘变化的监听类
 *@Author: Yqw
 *@Date: 2023/1/10 14:14
**/
class SoftKeyBoardListener(private val activity: Activity){
    private var rootView : View? = null
    private var rootViewVisibleHeight = 0
    private var onSoftKeyBoardListener : OnSoftKeyBoardChangeListener? = null

    //键盘变化的监听接口
    interface OnSoftKeyBoardChangeListener{
        fun keyBoardShow(keyBordHeight : Int)

        fun keyBoardHide()
    }

    fun onSoftKeyBoardListener(listener : (Int)->Unit){
        rootView = activity.window.decorView //获取activity根部局

        //监听布局树的变化
        rootView?.viewTreeObserver?.addOnGlobalLayoutListener {
            val rect = Rect()
            rootView?.getWindowVisibleDisplayFrame(rect)//获取根部局的rect
            val visibleHeight = rect.height()//获取根部局的高度

            //如果变化前为0或者相等说明无变化，不进行操作
            if (rootViewVisibleHeight == 0 || rootViewVisibleHeight == visibleHeight){
                rootViewVisibleHeight = visibleHeight
                return@addOnGlobalLayoutListener
            }

            //如果可视高度变小了，说明键盘显示了
            if (rootViewVisibleHeight-visibleHeight>200){
                val height = rootViewVisibleHeight - visibleHeight
                rootViewVisibleHeight = visibleHeight
                listener.invoke(height)
            }

            //如果可视高度变大了，说明键盘消失了
            if (visibleHeight - rootViewVisibleHeight>200){
                rootViewVisibleHeight = visibleHeight
                listener.invoke(0)
            }
        }
    }

}