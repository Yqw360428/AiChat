package com.supremebeing.phoneanti.aichat.event

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

/**
 *@ClassName: RxBus
 *@Description: RxBus的发送和接收
 *@Author: Yqw
 *@Date: 2022/12/3 21:28
**/
object RxBus {

    private val subject = PublishSubject.create<Any>()

    /**
     * 发送事件
     */
    fun post(event: Any) {
        subject.onNext(event)
    }

    /**
     * 接收事件
     */
    fun <T : Any> receive(event: Class<T>): Observable<T> = subject.hide().ofType(event)
}
