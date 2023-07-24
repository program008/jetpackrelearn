package com.enabot.jetpackrelearn

import android.content.ContentValues
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.camera.core.Camera
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.TorchState
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.enabot.jetpackrelearn.databinding.ActivityCameraXBinding
import com.enabot.mylibrary.BaseActivity
import com.enabot.mylibrary.utils.log
import com.enabot.mylibrary.utils.setOnUnFastClickListener
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * @author liu tao
 * @date 2023/7/21 14:38
 * @description 图片信息 名称，时间，地点，尺寸，大小，路径,设备，焦距，光圈，iso,闪光灯，白平衡，曝光时间
 *
 * 组合
 * Preview + VideoCapture + ImageCapture：LIMITED 及更高级设备。
 * Preview + VideoCapture + ImageAnalysis：添加到 Android 7(N) 的 LEVEL_3（最高级）设备。
 * Preview + VideoCapture + ImageAnalysis + ImageCapture：不支持。
 *
 * 配置
 * https://developer.android.com/training/camerax/configuration?hl=zh-cn
 * 聚焦
 * https://blog.csdn.net/Android_LeeJiaLun/article/details/126116616
 */
class CameraXActivity : BaseActivity<ActivityCameraXBinding>() {
    private var imageCapture: ImageCapture? = null
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null
    private lateinit var cameraExecutor: ExecutorService
    private var allPermissionsGranted: Boolean = false
    private var backCamera = true
    private var camera: Camera? = null
    override fun initViewBinding(): ActivityCameraXBinding {
        return ActivityCameraXBinding.inflate(layoutInflater)
    }

    override fun initViews() {
        // 拍照
        viewBinding.imageCaptureButton.setOnUnFastClickListener {
            takePhoto()
        }
        // 录像
        viewBinding.videoCaptureButton.setOnUnFastClickListener {
            captureVideo()
        }
        // 切换摄像头
        viewBinding.switchCamera.setOnUnFastClickListener {
            backCamera = !backCamera
            startCamera()
        }
        // 启用或停用手电筒
        viewBinding.switchTorch.setOnUnFastClickListener {
            val torchState = camera?.cameraInfo?.torchState?.value
            if (torchState == TorchState.ON) {
                viewBinding.ivTorch.isSelected = false
                camera?.cameraControl?.enableTorch(false)
            } else {
                viewBinding.ivTorch.isSelected = true
                camera?.cameraControl?.enableTorch(true)
            }
        }
        //对焦
        viewBinding.viewFinder.setOnUnFastClickListener {

        }
        cameraExecutor = Executors.newSingleThreadExecutor()
        // 请求必要的权限
        checkPermission(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) {
            allPermissionsGranted = it
            startCamera()
        }
    }

    private fun takePhoto() {
        if (!allPermissionsGranted) return
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return
        // Create time stamped name and MediaStore entry.
        val name =
            SimpleDateFormat(FILENAME_FORMAT, Locale.CHINA).format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
        ).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    log("Photo capture failed: ${exc.message}")
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    log(msg)
                }
            })
    }

    private fun captureVideo() {
        if (!allPermissionsGranted) return
        val videoCapture = this.videoCapture ?: return

        viewBinding.videoCaptureButton.isEnabled = false

        val curRecording = recording
        if (curRecording != null) {
            // Stop the current recording session.
            curRecording.stop()
            recording = null
            return
        }

        // create and start a new recording session
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraX-Video")
            }
        }

        val mediaStoreOutputOptions = MediaStoreOutputOptions.Builder(
            contentResolver,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        ).setContentValues(contentValues).build()
        recording = videoCapture.output.prepareRecording(this, mediaStoreOutputOptions).apply {
            if (PermissionChecker.checkSelfPermission(
                    this@CameraXActivity, android.Manifest.permission.RECORD_AUDIO
                ) == PermissionChecker.PERMISSION_GRANTED
            ) {
                withAudioEnabled()
            }
        }.start(ContextCompat.getMainExecutor(this)) { recordEvent ->
            when (recordEvent) {
                is VideoRecordEvent.Start -> {
                    viewBinding.videoCaptureButton.apply {
                        text = getString(R.string.stop_capture)
                        isEnabled = true
                    }
                }

                is VideoRecordEvent.Finalize -> {
                    if (!recordEvent.hasError()) {
                        val msg =
                            "Video capture succeeded: " + "${recordEvent.outputResults.outputUri}"
                        Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                        log(msg)
                    } else {
                        recording?.close()
                        recording = null
                        log(
                            "Video capture ends with error: " + "${recordEvent.error}"
                        )
                    }
                    viewBinding.videoCaptureButton.apply {
                        text = getString(R.string.start_capture)
                        isEnabled = true
                    }
                }
            }
        }

    }

    private fun startCamera() {
        if (!allPermissionsGranted) return
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
            }
//            val imageAnalyzer = ImageAnalysis.Builder().build().also {
//                it.setAnalyzer(cameraExecutor, LuminosityAnalyzer { luma ->
//                    log("Average luminosity: $luma")
//                })
//            }
            imageCapture = ImageCapture.Builder().build()

            val recorder = Recorder.Builder().setQualitySelector(
                QualitySelector.from(
                    Quality.HIGHEST, FallbackStrategy.higherQualityOrLowerThan(Quality.SD)
                )
            ).build()
            videoCapture = VideoCapture.withOutput(recorder)

            // Select back camera as a default
            val cameraSelector =
                if (backCamera) CameraSelector.DEFAULT_BACK_CAMERA else CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, videoCapture
                )
                val torchState = camera?.cameraInfo?.torchState?.value
                viewBinding.ivTorch.isSelected = torchState == TorchState.ON
                val cameraInfo = camera?.cameraInfo
                val hasFlashUnit = cameraInfo?.hasFlashUnit()
                log("确定手电筒功能是否可用 $hasFlashUnit")
            } catch (exc: Exception) {
                log("Use case binding failed $exc")
            }

        }, ContextCompat.getMainExecutor(this))
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
}