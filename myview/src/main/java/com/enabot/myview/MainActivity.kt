package com.enabot.myview

import com.enabot.mylibrary.BaseActivity
import com.enabot.myview.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun initViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }
    override fun initViews() {

    }
}