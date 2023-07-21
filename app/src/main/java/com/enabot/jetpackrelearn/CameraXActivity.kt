package com.enabot.jetpackrelearn

import com.enabot.jetpackrelearn.databinding.ActivityCameraXBinding
import com.enabot.mylibrary.BaseActivity

/**
 * @author liu tao
 * @date 2023/7/21 14:38
 * @description
 */
class CameraXActivity:BaseActivity<ActivityCameraXBinding>() {
    override fun initViewBinding(): ActivityCameraXBinding {
        return ActivityCameraXBinding.inflate(layoutInflater)
    }

    override fun initViews() {

    }
}