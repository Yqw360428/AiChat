package com.supremebeing.phoneanti.aichat.weight

import android.content.Context
import android.util.AttributeSet
import com.airbnb.lottie.LottieAnimationView

class LottieView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LottieAnimationView(context, attrs, defStyleAttr) {

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        /**
         * 依赖onDetachedFromWindow 停止动画， 动画 play 可能在没完成就结束页面，导致OOM
         */

        cancelAnimation()
    }
}