package com.supremebeing.phoneanti.aichat.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.supremebeing.phoneanti.aichat.App
import com.supremebeing.phoneanti.aichat.R
import com.supremebeing.phoneanti.aichat.bean.BaseBean
import com.supremebeing.phoneanti.aichat.bean.DailyBean
import com.supremebeing.phoneanti.aichat.bean.SignBean
import com.supremebeing.phoneanti.aichat.event.RxBus
import com.supremebeing.phoneanti.aichat.event.RxEvents
import com.supremebeing.phoneanti.aichat.network.client.RetrofitClient
import com.supremebeing.phoneanti.aichat.network.server.HttpCode
import com.supremebeing.phoneanti.aichat.tool.networkEnable
import com.supremebeing.phoneanti.aichat.tool.toastShort
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException

abstract class ArchViewModel : ViewModel() {
    val disposable = CompositeDisposable()

    /**
     *@MethodName: getErrorBean
     *@Description: 处理错误信息
     *@Author: Yqw
     *@Date: 2022/12/4 18:43
     **/
    private val bean="{\"error_code\":0,\"message\":\"msg\"}"
    protected fun <B : BaseBean> getErrorBean(throwable: Throwable?, beanClass: Class<B>): B {
        return if (throwable is HttpException) {
            val errorBody = throwable.response()?.errorBody() as ResponseBody
            try {
                val resultStr = errorBody.string()
                val bean = Gson().fromJson(resultStr, beanClass)
                bean.error_code = throwable.code()
                bean
            } catch (e: Exception) {
                val bean = Gson().fromJson(bean, beanClass)
                bean.error_code = throwable.code()
                bean.message = throwable.message()
                bean
            }
        } else {
            val bean = Gson().fromJson(bean, beanClass)
            bean.message = throwable?.message
            bean.error_code = 233333
            bean
        }
    }

    /**
     * 针对ViewModel中的Retrofit Observable进行订阅并加入Disposable队列
     * @param viewModel 界面中的ViewModel
     * @param observable ServerApi返回的Observable
     * @param consumer 处理事件的consumer，监听的逻辑都在这里
     * @param tConsumer 处理异常的consumer
     */
    fun <T : BaseBean> rxHttpSubscribe(observable: Observable<out T>, consumer: Consumer<in T>, tConsumer: Consumer<Throwable>) {
        if (!networkEnable(App.instance)){
            R.string.network_hint.toastShort
            return
        }
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    if (it.error_code == HttpCode.UNAUTHORIZED.code){
                        RxBus.post(RxEvents.NoLogin())
                    }else{
                        consumer.accept(it)
                    }

                }
                ,tConsumer)
            .also {
                disposable.add(it)
            }
    }

    override fun onCleared() {
        disposable.dispose()
        super.onCleared()
    }
}