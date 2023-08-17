package com.enabot.composesamples.ui.bloom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.constraintlayout.compose.ConstraintLayout

/**
 * @author liu tao
 * @date 2023/8/17 11:09
 * @description
 */

@Composable
fun FavoritesPage() {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFF2F3F5))
    ) {
        Text(text = "我的收藏")
    }
}