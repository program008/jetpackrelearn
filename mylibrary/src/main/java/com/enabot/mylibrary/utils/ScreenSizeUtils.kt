package com.enabot.mylibrary.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager

/**
 * Created by zmp
 * Date: 2021/1/21 10:52
 * des:屏幕工具类
 */
object ScreenSizeUtils {

    /**
     * 获取应用的屏幕宽度
     * @param context 上下文
     * @return 返回应用的屏幕宽度 px
     */
    fun getAppScreenWidth(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager?
        val point = Point()
        wm?.defaultDisplay?.getSize(point)
        return point.x
    }

    /**
     * 获取应用的屏幕高度
     * @param context 上下文
     * @return 返回应用的屏幕高度 px
     */
    fun getAppScreenHeight(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager?
        val point = Point()
        wm?.defaultDisplay?.getSize(point)
        return point.y
    }

    /**
     * 获取设备的屏幕宽度
     * @param context 上下文
     * @return 返回设备的屏幕宽度 px
     */
    fun getScreenWidth(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager?
        val point = Point()
        wm?.defaultDisplay?.getRealSize(point)
        return point.x
    }

    /**
     * 获取设备的屏幕高度
     * @param context 上下文
     * @return 返回屏设备的幕高度 px
     */
    fun getScreenHeight(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager?
        val point = Point()
        wm?.defaultDisplay?.getRealSize(point)
        return point.y
    }

    /**
     * 获取设备的宽高比
     * @param context 上下文
     * @return 返回屏设备的宽高比
     */
    fun getScreenAspectRatio(context: Context): Float {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val point = Point()
        wm.defaultDisplay.getSize(point)
        if (point.x > point.y) {
            return point.x.toFloat() / point.y.toFloat()
        }
        return point.y.toFloat() / point.x.toFloat()
    }

    /**
     * 获取设备的短边
     * @param context 上下文
     * @return 返回屏设备的宽
     */
    fun getScreenMin(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager?
        wm ?: return 0
        val point = Point()
        wm.defaultDisplay.getSize(point)
        return minOf(point.x, point.y)
    }

    /**
     * 获取设备的长
     * @param context 上下文
     * @return 返回屏设备的高
     */
    fun getScreenMax(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager?
        wm ?: return 0
        val point = Point()
        wm.defaultDisplay.getSize(point)
        return maxOf(point.x, point.y)
    }


    /**
     * Value of dp to value of px.
     *
     * @param dpValue The value of dp.
     * @return value of px
     */
    @JvmStatic
    fun dp2px(dpValue: Float, resources: Resources): Float {
        val scale = resources.displayMetrics.density
        return (dpValue * scale + 0.5f)
    }

    /**
     * Value of px to value of dp.
     *
     * @param pxValue The value of px.
     * @return value of dp
     */
    fun px2dp(pxValue: Float, resources: Resources): Float {
        val scale = resources.displayMetrics.density
        return (pxValue / scale + 0.5f)
    }

    /**
     * Value of sp to value of px.
     *
     * @param spValue The value of sp.
     * @return value of px
     */
    fun sp2px(spValue: Float, resources: Resources): Int {
        val fontScale = resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }

    /**
     * Value of px to value of sp.
     *
     * @param pxValue The value of px.
     * @return value of sp
     */
    fun px2sp(pxValue: Float, resources: Resources): Int {
        val fontScale = resources.displayMetrics.scaledDensity
        return (pxValue / fontScale + 0.5f).toInt()
    }

    /**
     * Converts an unpacked complex data value holding a dimension to its final floating
     * point value. The two parameters <var>unit</var> and <var>value</var>
     * are as in [TypedValue.TYPE_DIMENSION].
     *
     * @param value The value to apply the unit to.
     * @param unit  The unit to convert from.
     * @return The complex floating point value multiplied by the appropriate
     * metrics depending on its unit.
     */
    fun applyDimension(value: Float, unit: Int): Float {
        val metrics = Resources.getSystem().displayMetrics
        when (unit) {
            TypedValue.COMPLEX_UNIT_PX -> return value
            TypedValue.COMPLEX_UNIT_DIP -> return value * metrics.density
            TypedValue.COMPLEX_UNIT_SP -> return value * metrics.scaledDensity
            TypedValue.COMPLEX_UNIT_PT -> return value * metrics.xdpi * (1.0f / 72)
            TypedValue.COMPLEX_UNIT_IN -> return value * metrics.xdpi
            TypedValue.COMPLEX_UNIT_MM -> return value * metrics.xdpi * (1.0f / 25.4f)
        }
        return 0F
    }

    /**
     * Force get the size of view.
     *
     * e.g.
     * <pre>
     * SizeUtils.forceGetViewSize(view, new SizeUtils.OnGetSizeListener() {
     * Override
     * public void onGetSize(final View view) {
     * view.getWidth();
     * }
     * });
    </pre> *
     *
     * @param view     The view.
     * @param listener The get size listener.
     */
    fun forceGetViewSize(view: View, listener: OnGetSizeListener?) {
        view.post { listener?.onGetSize(view) }
    }

    /**
     * Return the width of view.
     *
     * @param view The view.
     * @return the width of view
     */
    fun getMeasuredWidth(view: View): Int {
        return measureView(view)[0]
    }

    /**
     * Return the height of view.
     *
     * @param view The view.
     * @return the height of view
     */
    fun getMeasuredHeight(view: View): Int {
        return measureView(view)[1]
    }

    /**
     * Measure the view.
     *
     * @param view The view.
     * @return arr[0]: view's width, arr[1]: view's height
     */
    fun measureView(view: View): IntArray {
        var lp = view.layoutParams
        if (lp == null) {
            lp = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        val widthSpec = ViewGroup.getChildMeasureSpec(0, 0, lp.width)
        val lpHeight = lp.height
        val heightSpec: Int
        heightSpec = if (lpHeight > 0) {
            View.MeasureSpec.makeMeasureSpec(lpHeight, View.MeasureSpec.EXACTLY)
        } else {
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        }
        view.measure(widthSpec, heightSpec)
        return intArrayOf(view.measuredWidth, view.measuredHeight)
    }

    ///////////////////////////////////////////////////////////////////////////
    // interface
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // interface
    ///////////////////////////////////////////////////////////////////////////
    interface OnGetSizeListener {
        fun onGetSize(view: View?)
    }


    /**
     * Return the status bar's height.
     *
     * @return the status bar's height
     */
    fun getStatusBarHeight(): Int {
        val resources: Resources = Resources.getSystem()
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }

    ///////////////////////////////////////////////////////////////////////////
    // navigation bar
    ///////////////////////////////////////////////////////////////////////////
    fun getNavBarHeight(): Int {
        val res: Resources = Resources.getSystem()
        val resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId != 0) {
            res.getDimensionPixelSize(resourceId)
        } else {
            0
        }
    }

    fun dpToPx(dp: Float, context: Context): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }
}