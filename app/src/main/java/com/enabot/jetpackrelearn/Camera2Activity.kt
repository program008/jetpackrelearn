package com.enabot.jetpackrelearn

import com.enabot.jetpackrelearn.databinding.ActivityCamera2Binding
import com.enabot.mylibrary.BaseActivity

/**
 * @author liu tao
 * @date 2023/7/21 14:40
 * @description
 */
class Camera2Activity : BaseActivity<ActivityCamera2Binding>() {
    override fun initViewBinding(): ActivityCamera2Binding {
        return ActivityCamera2Binding.inflate(layoutInflater)
    }

    override fun initViews() {

    }
}