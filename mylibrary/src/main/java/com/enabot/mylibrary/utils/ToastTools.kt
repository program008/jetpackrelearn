package com.enabot.mylibrary.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.Toast
import java.lang.ref.WeakReference

/**
 * @author zmp
 * @date : 2021/3/1 19:16
 * des:ToastTools
 */
object ToastTools {
    var mContext: WeakReference<Context>? = null
    var mToast: Toast? = null

    private val mHandler = Handler(Looper.getMainLooper()) {
        if (it.what == 0) {
            mToast?.cancel()
            val time = it.arg1
            val gravity = it.arg2
            val obj = it.obj.toString()
            mToast = Toast.makeText(mContext?.get(), obj, time)
            mToast?.setGravity(gravity, 0, 100)
            mToast?.show()
        }

        return@Handler true
    }

    fun init(context: Context) {
        mContext = WeakReference(context.applicationContext)
    }

    @JvmOverloads
    fun showMessage(message: String, time: Int = Toast.LENGTH_SHORT, gravity: Int = Gravity.CENTER) {
        mContext ?: return
        mHandler.obtainMessage(0, time, gravity, message).sendToTarget()
    }

    @JvmOverloads
    fun showMessage(messageId: Int, time: Int = Toast.LENGTH_SHORT, gravity: Int = Gravity.CENTER) {
        val message = mContext?.get()?.getString(messageId) ?: return
        showMessage(message, time, gravity)
    }

}