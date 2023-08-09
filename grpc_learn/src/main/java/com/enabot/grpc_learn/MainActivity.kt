package com.enabot.grpc_learn

import com.enabot.grpc_learn.databinding.ActivityMainBinding
import com.enabot.mylibrary.BaseActivity

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun initViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initViews() {

    }
}