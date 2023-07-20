package com.enabot.mylibrary

import android.app.Activity
import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

/**
 * @author liu tao
 * @date 2023/6/9 12:03
 * @description bottom sheet dialog base class
 */
abstract class BaseBottomSheetDialog<T : ViewBinding>(context: FragmentActivity) {
    private var bottomSheetDialog: BottomSheetDialog? = null
    private var mDialogBehavior: BottomSheetBehavior<*>? = null
    protected val viewBinding: T by lazy{
        initViewBinding(context)
    }

    init {
        bottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetDialog)
        //设置点击dialog外部不消失
        bottomSheetDialog?.setCanceledOnTouchOutside(true)
        //核心代码 解决了无法去除遮罩问题
        bottomSheetDialog?.window?.setDimAmount(0.6f)
        //dialog的高度
        //默认弹出的高度
        mDialogBehavior?.peekHeight = getWindowHeight(context) * 2 / 3
        //最大弹出的高度
        mDialogBehavior?.maxHeight = getWindowHeight(context) * 4 / 5
        //设置布局
        bottomSheetDialog?.setContentView(viewBinding.root)
        //用户行为
        mDialogBehavior = BottomSheetBehavior.from(viewBinding.root.parent as View)
        //展示
        //bottomSheetDialog?.show()
        //重新用户的滑动状态
        mDialogBehavior?.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                //监听BottomSheet状态的改变
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetDialog?.dismiss()
                    mDialogBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                //监听拖拽中的回调，根据slideOffset可以做一些动画
            }
        })
    }

    fun show() {
        bottomSheetDialog?.show()
    }

    fun dismiss(){
        bottomSheetDialog?.dismiss()
    }

    protected abstract fun initViewBinding(context: FragmentActivity): T

    /**
     * 计算高度(初始化可以设置默认高度)
     *
     * @return
     */
    private fun getWindowHeight(context: Activity): Int {
        val res: Resources = context.resources
        val displayMetrics: DisplayMetrics = res.displayMetrics
        //设置弹窗高度为屏幕高度
        return displayMetrics.heightPixels
    }
}