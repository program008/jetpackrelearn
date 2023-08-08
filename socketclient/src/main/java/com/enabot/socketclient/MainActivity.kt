package com.enabot.socketclient

import androidx.lifecycle.lifecycleScope
import com.enabot.mylibrary.BaseActivity
import com.enabot.mylibrary.utils.ToastTools
import com.enabot.mylibrary.utils.log
import com.enabot.mylibrary.utils.setOnUnFastClickListener
import com.enabot.mylibrary.utils.toast
import com.enabot.socketclient.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.Socket
import java.net.SocketTimeoutException
import java.nio.charset.Charset
import kotlin.concurrent.thread


class MainActivity : BaseActivity<ActivityMainBinding>() {
    var tcpSocket: Socket? = null
    var tcpReceiveThread: Thread? = null
    override fun initViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initViews() {
        ToastTools.init(this)
        viewBinding.btnConnServer.setOnUnFastClickListener {
            //连接服务器
            val serverIp = viewBinding.etServerIp.text.toString().trim()
            if (serverIp.isEmpty()){
                toast("ip不能为空")
                return@setOnUnFastClickListener
            }
            lifecycleScope.launch(Dispatchers.IO){
                tcpConnect(serverIp)
            }

        }
        viewBinding.btnContentSend.setOnUnFastClickListener {
            //发送消息
            val content = viewBinding.etContent.text.toString().trim()
            if (content.isEmpty()) {
                toast("请输入内容")
                return@setOnUnFastClickListener
            }
            lifecycleScope.launch(Dispatchers.IO){
                tcpSend(content)
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tcpDisConnect()
    }

    private fun tcpConnect(ip: String) {
        if (tcpSocket != null) tcpDisConnect()
        tcpSocket = Socket(ip, 8899)
        tcpSocket?.soTimeout = 1000
        tcpReceiveThread = thread {
            try {
                val flag = true
                val `in` = BufferedReader(InputStreamReader(tcpSocket?.getInputStream(), "UTF-8"))
                toast("TCP socket connection connected")
                var line: String? = null
                while (flag) {
                    try {
                        line = `in`.readLine()
                    } catch (e: SocketTimeoutException) {
                        //e.printStackTrace();
                        //flag = false;
                    }
                    if (line != null) {
                        println(line)
                        log("消息：$line")
                        line = null
                    }
                }
                `in`.close()
                tcpSocket?.close()
                tcpSocket = null
                toast("TCP socket connection closed")
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    private fun tcpDisConnect() {
        if (tcpReceiveThread != null) {
            tcpReceiveThread?.stop()
        }
    }

    fun tcpSend(msg: String) {
        log("发送：$msg")
        val printWriter =
            PrintWriter(BufferedWriter(OutputStreamWriter(tcpSocket?.getOutputStream())), true)
        printWriter.println(msg)
        printWriter.flush()
    }
}