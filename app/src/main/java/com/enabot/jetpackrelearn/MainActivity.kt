package com.enabot.jetpackrelearn


import android.R.attr.gravity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.TriggerEvent
import android.hardware.TriggerEventListener
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.enabot.jetpackrelearn.databinding.ActivityMainBinding
import com.enabot.mylibrary.BaseActivity
import com.enabot.mylibrary.utils.log
import com.enabot.mylibrary.utils.setOnUnFastClickListener
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import java.util.Locale


/**
 * “摇一摇”动作的设备加速度应不小于 15m / s2，转动角度不小于 35°，操作时间不少于 3s，或同时考虑加速度值与方向、
 * 转动角度的方式，或与前述单一触发条件等效的其他参数设置，确保用户在走路、 乘车、拾起放下移动智能终端等日常生活中，
 * 非用户主动触发跳转的情况下，不会出现误导、强迫跳转。
 */
class MainActivity : BaseActivity<ActivityMainBinding>(), SensorEventListener {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var locationRequest: LocationRequest? = null
    private var requestingLocationUpdates: Boolean = false
    private lateinit var sensorManager: SensorManager
    private var mLight: Sensor? = null
    private var maximumRange: Float = 0f
    private var mStepDetector = 0 // 自应用运行以来STEP_DETECTOR检测到的步数
    private var mStepCounter = 0 // 自系统开机以来STEP_COUNTER检测到的步数

    //初始化数组
    var gravity = floatArrayOf()//用来保存加速度传感器的值
    var r = floatArrayOf()//
    var geomagnetic = floatArrayOf();//用来保存地磁传感器的值
    var values = floatArrayOf();//用来保存最终的结果
    override fun initViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initViews() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    // Update UI with location data
                    log("lat=${location.latitude},lng=${location.longitude},alt=${location.altitude},accuracy=${location.accuracy}")
                    handleLocation(location)
                }
            }
        }
        // 获取最近一次定位信息
        viewBinding.btnLastLocation.setOnUnFastClickListener {
            lastLocationInfo()
        }
        currentLocationInfo()
        setSensorListener()

    }

    private fun setSensorListener() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val deviceSensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)
        for (index in deviceSensors.indices) {
            log("传感器$index =${deviceSensors[index].type}")
            sensorManager.registerListener(
                this,
                deviceSensors[index],
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }

        //使用有效运动传感器
        val triggerEventListener = object : TriggerEventListener() {
            override fun onTrigger(event: TriggerEvent?) {
                // Do work
                log("有效运动传感器 ${event?.sensor.toString()}")
            }
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION).also {
            sensorManager.requestTriggerSensor(triggerEventListener, it)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            setStepListener()
        }

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun setStepListener() {
        checkPermission(android.Manifest.permission.ACTIVITY_RECOGNITION) {
            // 计步器
            sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER).also {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            }
            sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR).also {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            }
        }
    }

    /**
     * 获取位置详情
     */
    private fun handleLocation(location: Location) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            addresses?.first()?.let {
                val address = "${it.adminArea}/${it.locality}/${it.featureName}"
                showMessage(address)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 获取最近一次定位信息
     */
    private fun lastLocationInfo() {
        checkPermission(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) {
            log("获取到位置信息权限 $it")
            fusedLocationClient.lastLocation
                .addOnSuccessListener {
                    log("获取位置成功 lat=${it.latitude} lng=${it.longitude}")
                    handleLocation(it)
                }
                .addOnFailureListener {
                    showMessage("获取位置失败 $it")
                }
        }
    }

    /**
     * 请求当前位置信息
     */
    fun currentLocationInfo() {
        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest!!)
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener {
            // All location settings are satisfied. The client can initialize
            // location requests here.
            requestingLocationUpdates = true
            startLocationUpdates()
        }
        task.addOnFailureListener {
            if (it is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    it.startResolutionForResult(
                        this@MainActivity,
                        REQUEST_CHECK_SETTINGS
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }

    }

    private fun startLocationUpdates() {
        if (locationRequest == null) {
            return
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest!!,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }


    override fun onResume() {
        super.onResume()
        //if (requestingLocationUpdates) currentLocationInfo()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
        sensorManager.unregisterListener(this)
    }

    fun Context.showMessage(msg: String) {
        viewBinding.tvShow.text = msg
        log(msg)
    }

    companion object {
        const val REQUEST_CHECK_SETTINGS = 100
    }

    override fun onSensorChanged(event: SensorEvent) {
        // The light sensor returns a single value.
        // Many sensors return 3 values, one for each axis.
        var lux = 0f
        var luy = 0f
        var luz = 0f
        if (event.values.size == 1) {
            lux = event.values?.get(0) ?: 0f
        }
        if (event.values.size == 2) {
            lux = event.values?.get(0) ?: 0f
            luy = event.values?.get(1) ?: 0f
        }
        if (event.values.size == 3) {
            lux = event.values?.get(0) ?: 0f
            luy = event.values?.get(1) ?: 0f
            luz = event.values?.get(2) ?: 0f
        }
        when (event.sensor.type) {
            Sensor.TYPE_STEP_COUNTER -> {
                mStepCounter = lux.toInt()
                log("今天你走了 ${lux} 步")
            }

            Sensor.TYPE_STEP_DETECTOR -> {
                log("步测器传感器 $lux")
                if (lux == 1.0f) {
                    mStepDetector++
                }
                log("app启动以来步行 $mStepDetector")
            }

            Sensor.TYPE_LIGHT -> {
                log("光传感器 $lux")
            }

            Sensor.TYPE_GRAVITY -> {
                log("重力加速器 $lux,$luy,$luz")
            }

            Sensor.TYPE_MAGNETIC_FIELD -> {
                geomagnetic = event.values
            }

            Sensor.TYPE_ACCELEROMETER -> {
                log("加速度 $lux,$luy,$luz")
                gravity = event.values
                //getOritation()
            }

            Sensor.TYPE_LINEAR_ACCELERATION -> {
                log("线性加速度 $lux,$luy,$luz")
            }

            Sensor.TYPE_GYROSCOPE -> {
                log("陀螺仪 $lux,$luy,$luz")
            }

            else -> {}
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Do something here if sensor accuracy changes.
        log("sensor=$sensor,accuracy=$accuracy")
    }

    /**
     * 获取手机旋转角度
     */
    fun getOritation() {
        // r从这里返回
        SensorManager.getRotationMatrix(r, null, gravity, geomagnetic)
        //values从这里返回
        SensorManager.getOrientation(r, values)
        //提取数据
        val degreeZ = Math.toDegrees(values[0].toDouble())
        val degreeX = Math.toDegrees(values[1].toDouble())
        val degreeY = Math.toDegrees(values[2].toDouble())
        log("degreeZ=$degreeZ,degreeX=$degreeX,degreeY=$degreeY")
    }
}