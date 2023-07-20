package com.enabot.jetpackrelearn

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
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
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.DetectedActivity
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

class MainActivity : BaseActivity<ActivityMainBinding>() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var locationRequest: LocationRequest? = null
    private var requestingLocationUpdates: Boolean = false

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
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
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
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
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
    }

    fun Context.showMessage(msg: String) {
        viewBinding.tvShow.text = msg
        log(msg)
    }

    companion object {
        const val REQUEST_CHECK_SETTINGS = 100
    }
}