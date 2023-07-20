package com.enabot.mylibrary

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.viewbinding.ViewBinding
import com.enabot.mylibrary.utils.ScreenSizeUtils
import com.enabot.mylibrary.utils.log
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

/**
 * @author liu tao
 * @date 2023/4/10 19:03
 * @description
 */
abstract class BaseActivity<T : ViewBinding> : AppCompatActivity(),
    EasyPermissions.PermissionCallbacks {
    /**
     * 沉浸式setPadding
     */
    open val immersiveHideNavigate = true
    val viewBinding: T by lazy {
        initViewBinding()
    }
    //获取权限成功，做需要权限相关的工作
    private var hasPermissionsListener: ((Boolean) -> Unit)? = null
    protected abstract fun initViewBinding(): T
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        initViews()
    }

    protected abstract fun initViews()
    override fun setContentView(view: View) {
        if (immersiveHideNavigate) {
            hideStatusBar(WindowInsetsCompat.Type.navigationBars())
            view.setPadding(0, ScreenSizeUtils.getStatusBarHeight(), 0, 0)
        }
        super.setContentView(view)
    }

    /**
     * 检测权限 并请求权限
     */
    fun checkPermission(vararg params: String, callback: (Boolean) -> Unit) {
        hasPermissionsListener = callback
        val permissionList = mutableListOf<String>()
        params.forEach {
            permissionList.add(it)
        }
        val permissions = permissionList.toTypedArray()
        if (EasyPermissions.hasPermissions(this, *permissions)) {
            log("有权限")
            hasPermissionsListener?.invoke(true)
            return
        }
        EasyPermissions.requestPermissions(this, "", requestCode, *permissions)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        log("onPermissionsGranted")
        hasPermissionsListener?.invoke(true)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        log("onPermissionsDenied:$requestCode")
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    /**
     * 全屏并且隐藏状态栏
     */
    protected open fun hideStatusBar(type: Int = WindowInsetsCompat.Type.systemBars()) {
        hideBottomUIMenu()
        val window = window
        val insetsController: WindowInsetsControllerCompat =
            WindowCompat.getInsetsController(window, window.decorView)
        insetsController.hide(type)
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        setLightStatusBars(true)
    }

    private fun hideBottomUIMenu() {
        //SYSTEM_UI_FLAG_IMMERSIVE_STICKY通过系统上滑或者下滑拉出导航栏后会自动隐藏。
        val params: WindowManager.LayoutParams = window.attributes
        params.systemUiVisibility =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.attributes = params
    }

    fun setLightStatusBars(light: Boolean) {
        val insetsController: WindowInsetsControllerCompat =
            WindowCompat.getInsetsController(window, window.decorView)
        window.statusBarColor = Color.TRANSPARENT
        insetsController.isAppearanceLightStatusBars = light
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        //SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN：让内容显示到状态栏
        //SYSTEM_UI_FLAG_LAYOUT_STABLE：状态栏文字显示白色
        //SYSTEM_UI_FLAG_LIGHT_STATUS_BAR：状态栏文字显示黑色
        if (light) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        } else {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    }

    companion object {
        private const val requestCode = 123
    }
}