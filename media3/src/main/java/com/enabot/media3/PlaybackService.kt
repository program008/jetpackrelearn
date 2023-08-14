package com.enabot.media3

import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession

/**
 * @author liu tao
 * @date 2023/8/14 14:14
 * @description
 */
class PlaybackService : MediaLibraryService() {
    var mediaLibrarySession: MediaLibrarySession? = null
    var callback: MediaLibrarySession.Callback = object : MediaLibrarySession.Callback {

    }

    // If desired, validate the controller before returning the media library session
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? =
        mediaLibrarySession

    // Create your player and media library session in the onCreate lifecycle event
    override fun onCreate() {
        super.onCreate()
        val player = ExoPlayer.Builder(this).build()
        mediaLibrarySession = MediaLibrarySession.Builder(this, player, callback).build()
    }

    // Remember to release the player and media library session in onDestroy
    override fun onDestroy() {
        mediaLibrarySession?.run {
            player.release()
            release()
            mediaLibrarySession = null
        }
        super.onDestroy()
    }
}