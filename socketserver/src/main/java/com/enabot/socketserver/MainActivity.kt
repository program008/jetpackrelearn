package com.enabot.socketserver

import com.enabot.mylibrary.BaseActivity
import com.enabot.mylibrary.utils.setOnUnFastClickListener
import com.enabot.socketserver.databinding.ActivityMainBinding


class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun initViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initViews() {
        val listeningThread: Thread = ListeningThread(8899)
        listeningThread.start()
        viewBinding.btnContentSend.setOnUnFastClickListener {
            //发送消息
        }
    }
}