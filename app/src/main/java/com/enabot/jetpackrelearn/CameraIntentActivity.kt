package com.enabot.jetpackrelearn

import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.os.Environment
import android.view.View
import android.widget.MediaController
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.enabot.jetpackrelearn.databinding.ActivityCameraIntentBinding
import com.enabot.mylibrary.BaseActivity
import com.enabot.mylibrary.utils.log
import com.enabot.mylibrary.utils.setOnUnFastClickListener
import org.joda.time.DateTime
import java.io.File

/**
 * @author liu tao
 * @date 2023/7/21 14:35
 * @description 图片信息 名称，时间，地点，尺寸，大小，路径
 */
class CameraIntentActivity : BaseActivity<ActivityCameraIntentBinding>() {
    lateinit var pictureFile: File
    lateinit var videoFile: File
    override fun initViewBinding(): ActivityCameraIntentBinding {
        return ActivityCameraIntentBinding.inflate(layoutInflater)
    }

    override fun initViews() {
        val takePickLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) {
            log("拍照完成 $it")
            viewBinding.videoView.visibility = View.GONE
            viewBinding.ivPicture.visibility = View.VISIBLE
            MediaScannerConnection.scanFile(this, arrayOf(pictureFile.toString()), null, null)
            viewBinding.ivPicture.setImageBitmap(BitmapFactory.decodeFile(pictureFile.absolutePath))
        }
        // 拍照
        viewBinding.btnTakePic.setOnUnFastClickListener {
            val filename = DateTime.now().toString("yyyy-MM-dd-HH-mm-ss") + ".png"
            log("filename=$filename")
            //拍照
            pictureFile = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                filename
            )
            val uri =
                FileProvider.getUriForFile(this, "$packageName.fileprovider", pictureFile)
            takePickLauncher.launch(uri)
        }
        val captureVideoLauncher =
            registerForActivityResult(ActivityResultContracts.CaptureVideo()) {
                log("录像完成 $it")
                MediaScannerConnection.scanFile(this, arrayOf(videoFile.toString()), null, null)
                playVideo()
            }
        //录像
        viewBinding.btnRecordVideo.setOnUnFastClickListener {
            val filename = DateTime.now().toString("yyyy-MM-dd-HH-mm-ss") + ".mp4"
            videoFile = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                filename
            )
            val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", videoFile)
            captureVideoLauncher.launch(uri)
        }
    }

    private fun playVideo(){
        viewBinding.videoView.visibility = View.VISIBLE
        viewBinding.ivPicture.visibility = View.GONE
        viewBinding.videoView.setVideoPath(videoFile.absolutePath)
        val mediaController = MediaController(this)
        viewBinding.videoView.setMediaController(mediaController)
        viewBinding.videoView.setFocusable(true)
        viewBinding.videoView.start()
        viewBinding.videoView.setOnCompletionListener {
           log("该视频播放完毕")
        }
    }
}