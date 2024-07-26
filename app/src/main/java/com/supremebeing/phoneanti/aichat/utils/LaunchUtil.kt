package com.supremebeing.phoneanti.aichat.utils

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.view.animation.LinearInterpolator
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.animation.doOnEnd

object LaunchUtil {
    const val maxTime = 15000L
    private const val speedTime = (maxTime *0.6).toInt()
    private var animator : ValueAnimator? = null

    @SuppressLint("SetTextI18n")
    fun initProgress(progressBar : ProgressBar, text : AppCompatTextView? = null){
        progressBar.max = maxTime.toInt()
        ValueAnimator.ofInt(1, speedTime).apply {
            duration = 2000
            interpolator = LinearInterpolator()
            addUpdateListener {
                val i = it.animatedValue as Int
                progressBar.progress = i
                text?.let {view->
                    view.text = "Loading...${((i.toFloat()/ maxTime.toFloat())*100).toInt()}%"
                }
            }
            doOnEnd {
                animator = ValueAnimator.ofInt(speedTime, maxTime.toInt()).apply {
                    duration = maxTime -2000
                    interpolator = LinearInterpolator()
                    addUpdateListener {
                        val i = it.animatedValue as Int
                        progressBar.progress = i
                        text?.let {view->
                            view.text = "Loading...${((i.toFloat()/ maxTime.toFloat())*100).toInt()}%"
                        }
                    }
                    start()
                }
            }
        }.start()
    }

    fun endAnimator(){
        if (animator != null){
            animator?.end()
        }
    }
}