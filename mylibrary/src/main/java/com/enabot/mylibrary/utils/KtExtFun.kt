package com.enabot.mylibrary.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageFormat.JPEG
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.media.MediaCodec.MetricsConstants.MIME_TYPE
import android.media.MediaScannerConnection
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.collection.ArrayMap
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.size
import androidx.fragment.app.Fragment
import com.enabot.mylibrary.utils.ViewKtxHelper.isFastClick
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

/**
 * @author liu tao
 * @date 2022/3/28 14:44
 * @description 功能扩展函数
 */
object ViewKtxHelper {
    val mHandler = Handler(Looper.getMainLooper())

    private const val MIN_CLICK_DELAY_TIME = 500

    const val CLICK_MESSAGE = 1001
    const val CLICK_TIME = 500L

    private var lastOnclickTime = 0L

    /**
     * 是否是全局快速点击
     */
    fun isFastClick(): Boolean {
        val time = System.currentTimeMillis()
        val isFast = abs(time - lastOnclickTime) < MIN_CLICK_DELAY_TIME
        if (!isFast) {
            lastOnclickTime = time
        }
        return isFast
    }
}

/**
 * 简化日志打印
 */
fun Any.log(contents: String, type: Int = Log.ERROR) {
    val tag = "[ryan] " + this.javaClass.simpleName
    when (type) {
        Log.ERROR -> Log.e(tag, contents)
        Log.DEBUG -> Log.d(tag, contents)
        Log.VERBOSE -> Log.v(tag, contents)
        Log.INFO -> Log.i(tag, contents)
        Log.WARN -> Log.w(tag, contents)
        else -> Log.i(tag, contents)
    }
}

fun Any.toast(msg: String) {
    ToastTools.showMessage(msg)
}

/**
 * 给一个控件组的子控件添加点击缩放效果
 */
@SuppressLint("ClickableViewAccessibility")
fun ConstraintLayout.enableGroupViewClickEffect(scale: Float = 0.9f, duration: Long = 150) {
    val size = this.size
    this.setOnTouchListener { _, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                for (i in 0 until size) {
                    val view = this.getChildAt(i)
                    if (view is TextView) {
                        continue
                    }
                    view.animate().scaleX(scale).scaleY(scale).setDuration(duration)
                        .start()


                }
            }

            MotionEvent.ACTION_UP -> {
                for (i in 0 until size) {
                    val view = this.getChildAt(i)
                    if (view is TextView) {
                        continue
                    }
                    view.animate().scaleX(1f).scaleY(1f).setDuration(duration).start()
                    view.performClick()
                }
            }

            MotionEvent.ACTION_CANCEL -> {
                for (i in 0 until size) {
                    val view = this.getChildAt(i)
                    if (view is TextView) {
                        continue
                    }
                    view.animate().scaleX(1f).scaleY(1f).setDuration(duration).start()
                }
            }
        }
        false
    }
}

/**
 * 给单个控件添加点击缩放功能
 */
fun View.enableSingleViewClickEffect(scale: Float = 0.9f, duration: Long = 150) {
    this.setOnTouchListener { _, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                this.animate().scaleX(scale).scaleY(scale).setDuration(duration)
                    .start()
            }

            MotionEvent.ACTION_UP -> {
                this.animate().scaleX(1f).scaleY(1f).setDuration(duration).start()
                this.performClick()
            }

            MotionEvent.ACTION_CANCEL -> {
                this.animate().scaleX(1f).scaleY(1f).setDuration(duration).start()
            }
        }
        true
    }
}

/**
 * 设置文本右图片大小和文本字体大小相同
 */
fun TextView.setDrawableEnd(drawableId: Int, drawableWidth: Int = 0, drawableHeight: Int = 0) {
    var width = drawableWidth
    var height = drawableHeight
    var drawable: Drawable? = null
    if (drawableWidth == 0 || drawableHeight == 0) {
        width = this.textSize.toInt()
        height = width
    }
    try {
        drawable = ResourcesCompat.getDrawable(resources, drawableId, null)?.apply {
            setBounds(0, 0, width, height)
        }
    } catch (e: Exception) {
        log("--------图标不存在-----")
    }
    setCompoundDrawables(null, null, drawable, null)
}

fun TextView.setDrawableStart(drawableId: Int, drawableWidth: Int = 0, drawableHeight: Int = 0) {
    var width = drawableWidth
    var height = drawableHeight
    var drawable: Drawable? = null
    if (drawableWidth == 0 || drawableHeight == 0) {
        width = this.textSize.toInt()
        height = width
    }
    try {
        drawable = ResourcesCompat.getDrawable(resources, drawableId, null)?.apply {
            setBounds(0, 0, width, height)
        }
    } catch (e: Exception) {
        log("--------图标不存在-----")
    }
    setCompoundDrawables(drawable, null, null, null)
}


/**
 * 文本内容改变监听扩展函数
 */
fun EditText.onChange(textChanged: ((String) -> Unit)) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            textChanged.invoke(s.toString())
        }
    })
}

/**
 * show toast
 */
fun Context.showToast(message: String?, length: Int = Toast.LENGTH_SHORT) {
    message?.let {
        Toast.makeText(this, it, length).show()
    }
}

/**
 * show snack
 */
fun View.showSnackMessage(
    message: String?,
    anchorView: View? = null,
    backgroundColor: Int,
    textColor: Int,
    length: Int = Snackbar.LENGTH_SHORT
) {
    message?.let {
        try {
            val snack = Snackbar.make(this, it, length)
            snack.setBackgroundTint(ContextCompat.getColor(context, backgroundColor))
            snack.setTextColor(ContextCompat.getColor(context, textColor))
            snack.anchorView = anchorView
            snack.show()
        } catch (ex: Exception) {
            log("${ex.message}")
        }
    }
}

/**
 * check if the internet is available
 */
fun Context.isNetworkAvailable(): Boolean {
//    val manager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//        val capabilities = manager.getNetworkCapabilities(manager.activeNetwork)
//        if (capabilities != null) {
//            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
//                return true
//            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
//                return true
//            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
//                return true
//            }
//        }
//    } else {
//        try {
//            val activeNetworkInfo = manager.activeNetworkInfo
//            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
//                return true
//            }
//        } catch (ex: Exception) {
//            log("${ex.message}")
//        }
//    }
    return false
}

/**
 * control visibility of views--> show
 */
fun View.show() {
    visibility = View.VISIBLE
}

/**
 * control visibility of views--> hide
 */
fun View.hide() {
    visibility = View.GONE
}

/**
 * control visibility of views--> invisible
 */
fun View.invisible() {
    visibility = View.INVISIBLE
}

fun <T : JsonElement?> T.asIntOrNull(): Int? {
    return if (this == null || isJsonNull) null else asInt
}

fun <T : JsonElement?> T.asStringOrNull(): String? {
    return if (this == null || isJsonNull) null else asString
}

fun <T : JsonElement?> T.asLongOrNull(): Long? {
    return if (this == null || isJsonNull) null else asLong
}

fun <T : JsonElement?> T.asJsonObjectOrNull(): JsonObject? {
    return if (this == null || isJsonNull) null else asJsonObject
}

fun <T : JsonElement?> T.asJsonArrayOrNull(): JsonArray? {
    return if (this == null || isJsonNull) null else asJsonArray
}

fun <T : JsonElement?> T.asBooleanOrNull(): Boolean? {
    return if (this == null || isJsonNull) null else asBoolean
}

fun <T : JsonElement?> T.asDoubleOrNull(): Double? {
    return if (this == null || isJsonNull) null else asDouble
}

inline fun <T> MutableList<T>.removeFirstIf(predicate: (T) -> Boolean): T? {
    val indexOfFirst = indexOfFirst(predicate)
    if (indexOfFirst != -1) {
        return removeAt(indexOfFirst)
    }
    return null
}


inline fun <T> MutableList<T>.addIfNoFind(item: T, predicate: (T) -> Boolean): T? {
    val find = firstOrNull(predicate)
    if (find != null) {
        return find
    }
    add(item)
    return null
}


fun String?.isNullOrEmpty2Other(other: String?): String? {
    if (this.isNullOrEmpty()) {
        return other
    }
    return this
}

/**
 * 全局防止快速点击
 * inline function中的代码直接嵌入到调用处
 * crossinline 的作用是让被标记的lambda表达式不允许非局部返回。
 */
inline fun View.setOnUnFastClickListener(crossinline onClick: (View) -> Unit) {
    this.setOnClickListener {
        if (isFastClick()) {
            return@setOnClickListener
        }
        onClick.invoke(this)
    }
}

fun ArrayMap<String, out Any?>.toJson(): String {
    return GsonUtils.toJson(this)
}

fun Window.hideStatusBar(type: Int = WindowInsetsCompat.Type.systemBars()) {
    //SYSTEM_UI_FLAG_IMMERSIVE_STICKY通过系统上滑或者下滑拉出导航栏后会自动隐藏。
    val insetsController: WindowInsetsControllerCompat =
        WindowCompat.getInsetsController(this, this.decorView)
    insetsController.hide(type)
    this.statusBarColor = Color.TRANSPARENT
    insetsController.isAppearanceLightStatusBars = false
    insetsController.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    val params: WindowManager.LayoutParams = this.attributes
    params.systemUiVisibility =
        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    this.attributes = params
}

fun View.dpToPx(dp: Float): Float {
    return ScreenSizeUtils.dp2px(dp, context.resources)
}

/**
 * 双击
 */
inline fun View.setDoubleClick(crossinline onClick: (View) -> Unit) {
    this.setOnClickListener {
        if (ViewKtxHelper.mHandler.hasMessages(ViewKtxHelper.CLICK_MESSAGE)) {
            ViewKtxHelper.mHandler.removeMessages(ViewKtxHelper.CLICK_MESSAGE)
            onClick.invoke(it)
            return@setOnClickListener
        }
        ViewKtxHelper.mHandler.sendEmptyMessageDelayed(
            ViewKtxHelper.CLICK_MESSAGE,
            ViewKtxHelper.CLICK_TIME
        )
    }
}

/**
 * 获取版本名称
 *
 * @param context 上下文
 * @return 版本名称
 */
fun Context.getVersionName(): String? {

    //获取包管理器
    val pm = packageManager
    //获取包信息
    try {
        val packageInfo = pm.getPackageInfo(packageName, 0)
        //返回版本号
        return packageInfo.versionName
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return null
}

/**
 * 获取版本号
 *
 * @param context 上下文
 * @return AppName
 */
fun Context.getAppName(): String {
    //获取包管理器
    val labelRes = applicationInfo.labelRes
    //获取包信息
    try {
        return getString(labelRes)
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    return ""
}

/**
 * 获取省市区
 */
fun String.addressLines(): Array<String> {
    val addressLines = this.split("&").toTypedArray()
    if (addressLines.size == 3) {
        return addressLines
    }
    if (addressLines.size == 2) {
        return arrayOf(addressLines[0], addressLines[1], "")
    }
    if (addressLines.size == 1) {
        return arrayOf(addressLines[0], "", "")
    }
    return arrayOf("", "", "")
}

/**
 * 获取指定文本的宽度
 * @param text
 * @param textSize
 * @return
 */
fun Context.getTextWidth(text: String?, textSize: Float): Float {
    if (TextUtils.isEmpty(text)) {
        return 0f
    }
    val paint = Paint() //创建一个画笔对象
    paint.textSize = textSize //设置画笔的文字大小
    return paint.measureText(text) //利用画笔丈量指定文本的宽度
}

fun View.gone() = run { visibility = View.GONE }

fun View.visible() = run { visibility = View.VISIBLE }

infix fun View.visibleIf(condition: Boolean) =
    run { visibility = if (condition) View.VISIBLE else View.GONE }

infix fun View.goneIf(condition: Boolean) =
    run { visibility = if (condition) View.GONE else View.VISIBLE }

infix fun View.invisibleIf(condition: Boolean) =
    run { visibility = if (condition) View.INVISIBLE else View.VISIBLE }

fun Fragment.toast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
}

fun Fragment.toast(@StringRes message: Int) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
}

fun Activity.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Activity.toast(@StringRes message: Int) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun View.snackbar(message: String, duration: Int = Snackbar.LENGTH_LONG) {
    Snackbar.make(this, message, duration).show()
}

fun View.snackbar(@StringRes message: Int, duration: Int = Snackbar.LENGTH_LONG) {
    Snackbar.make(this, message, duration).show()
}

fun Activity.hideKeyboard() {
    val imm: InputMethodManager =
        getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    val view = currentFocus ?: View(this)
    imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
}

fun Fragment.hideKeyboard() {
    activity?.apply {
        val imm: InputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus ?: View(this)
        imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}

val String.isDigitOnly: Boolean
    get() = matches(Regex("^\\d*\$"))

val String.isAlphabeticOnly: Boolean
    get() = matches(Regex("^[a-zA-Z]*\$"))

val String.isAlphanumericOnly: Boolean
    get() = matches(Regex("^[a-zA-Z\\d]*\$"))

val Any?.isNull get() = this == null
fun Any?.ifNull(block: () -> Unit) = run {
    if (this == null) {
        block()
    }
}

fun String.toDate(format: String = "yyyy-MM-dd HH:mm:ss"): Date? {
    val dateFormatter = SimpleDateFormat(format, Locale.getDefault())
    return dateFormatter.parse(this)
}

fun Date.toStringFormat(format: String = "yyyy-MM-dd HH:mm:ss"): String {
    val dateFormatter = SimpleDateFormat(format, Locale.getDefault())
    return dateFormatter.format(this)
}

/**
 * Saves a bitmap as a PNG file.
 *
 * Note that `.png` extension is added to the filename.
 */
fun Bitmap.saveAsPNG(applicationContext: Context, filename: String) = "$filename.png".let { name ->
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        val file =
            File(applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), name)
        FileOutputStream(file).use { compress(Bitmap.CompressFormat.PNG, 100, it) }
        MediaScannerConnection.scanFile(
            applicationContext,
            arrayOf(file.absolutePath), null, null
        )
        FileProvider.getUriForFile(
            applicationContext,
            "${applicationContext.packageName}.provider", file
        )
    } else {
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
            put(MediaStore.Video.Media.IS_PENDING, 1)
        }

        val resolver = applicationContext.contentResolver
        val uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
        uri?.let { resolver.openOutputStream(it) }
            ?.use { compress(Bitmap.CompressFormat.PNG, 100, it) }

        values.clear()
        values.put(MediaStore.Video.Media.IS_PENDING, 0)
        uri?.also {
            resolver.update(it, values, null, null)
        }
    }

    /**
     * Saves a bitmap as a Jpeg file.
     *
     * Note that `.jpg` extension is added to the filename.
     */
    fun Bitmap.saveAsJPG(applicationContext: Context, filename: String) =
        "$filename.jpg".let { name ->
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
                FileOutputStream(
                    File(
                        applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        name
                    )
                )
                    .use { compress(Bitmap.CompressFormat.JPEG, 100, it) }
            else {
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                    put(MediaStore.Video.Media.IS_PENDING, 1)
                }

                val resolver = applicationContext.contentResolver
                val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                uri?.let { resolver.openOutputStream(it) }
                    ?.use { compress(Bitmap.CompressFormat.JPEG, 70, it) }

                values.clear()
                values.put(MediaStore.Video.Media.IS_PENDING, 0)
                uri?.also {
                    resolver.update(it, values, null, null)
                }
            }
        }
}