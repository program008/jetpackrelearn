package com.enabot.media3


import android.annotation.SuppressLint
import android.os.Bundle
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.enabot.media3.databinding.ActivityMainBinding
import com.enabot.mylibrary.BaseActivity
import com.enabot.mylibrary.utils.log
import com.enabot.mylibrary.utils.setOnUnFastClickListener
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

/**
 * 音频与视频 media3
 * 可配置参数：
 * 插图参数：
 * 参数名        类型      备注
 * use_artwork	boolean	是否使用插图
 * artwork_display_mode	enum	插图显示模式（off、fit、fill）
 * default_artwork	reference	默认插图
 *
 * 控制器参数：
 * use_controller	boolean	是否使用控制器
 * hide_on_touch	boolean	是否在触摸时隐藏控制器
 * hide_during_ads  boolean 是否在广告播放期间隐藏控制器
 * auto_show        boolean 是否自动显示控制器
 * show_timeout     integer 显示控制器的超时时间
 * show_shuffle_button boolean 是否显示随机播放按钮
 * show_subtitle_button boolean是否显示字幕按钮
 * show_vr_button boolean是否显示VR按钮
 * time_bar_min_update_interval integer 进度条的最小更新时间间隔
 * animation_enabled boolean 是否启用控制器动画效果
 *
 * player view 参数
 * show_buffering enum 是否显示缓冲状态（never、when_playing、always）
 * keep_content_on_player_reset boolean 当播放器重置时，是否在其上保留媒体内容（例如字幕）
 * surface_type enum视频渲染控件类型 (none、surface_view、texture_view、spherical_gl_surface_view、video_decoder_gl_surface_view)
 * resize_mode  enum调整视频大小的模式 （fit、fixed_width、fixed_heigt、fill、zoom）
 *
 *
 */
class MainActivity : BaseActivity<ActivityMainBinding>() {
    val url = "https://media.w3.org/2010/05/sintel/trailer.mp4"
    override fun initViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initViews() {
        initPlayMedia()
        viewBinding.btnPlaySingleVideo.setOnUnFastClickListener {
            viewBinding.playerView.player?.run {
                //停止之前播放的视频
                stop()
                //设置单个资源
                setMediaItem(MediaItem.fromUri("https://storage.googleapis.com/exoplayer-test-media-0/play.mp3"))
                //开始缓冲
                prepare()
            }
        }

        viewBinding.btnPlayMultiVideo.setOnUnFastClickListener {
            viewBinding.playerView.player?.run {
                //停止之前播放的视频
                stop()
                //设置多个资源
                setMediaItems(
                    arrayListOf(
                        MediaItem.fromUri("https://minigame.vip/Uploads/images/2021/09/18/1631951892_page_img.mp4"),
                        MediaItem.fromUri("https://storage.googleapis.com/exoplayer-test-media-1/mp4/dizzy-with-tx3g.mp4")
                    )
                )
                //开始缓冲
                prepare()
            }
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun initPlayMedia() {

        val player = ExoPlayer.Builder(this).build()
        //val mediaSession = MediaSession.Builder(this, player).build()
        viewBinding.playerView.player = player
        viewBinding.playerView.player?.run {
            addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    //播放状态变化回调
                    log("onIsPlayingChanged $isPlaying")
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    log("onPlaybackStateChanged $playbackState")
                    when (playbackState) {
                        Player.STATE_IDLE -> {
                            //播放器停止时的状态
                        }

                        Player.STATE_BUFFERING -> {
                            //正在缓冲暑假
                        }

                        Player.STATE_READY -> {
                            //可以开始播放
                        }

                        Player.STATE_ENDED -> {
                            //播放结束
                        }

                        else -> {}
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    super.onPlayerError(error)
                    //说起播放错误信息
                    log("onPlayerError ${error.message}")
                }
            })
            //设置重复模式
            // Player.REPEAT_MODE_ALL 无限重复
            // Player.REPEAT_MODE_ONE 重复一次
            // Player.REPEAT_MODE_OFF 不重复
            repeatMode = Player.REPEAT_MODE_OFF
            // 设置当缓冲完毕后直接播放视频
            playWhenReady = true
        }
    }

    override fun onResume() {
        super.onResume()
        //恢复播放
        viewBinding.playerView.onResume()
    }

    override fun onPause() {
        super.onPause()
        //停止播放
        viewBinding.playerView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        //释放播放器资源
        viewBinding.playerView.player?.release()
        viewBinding.playerView.player = null
    }
}