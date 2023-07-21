package com.enabot.jetpackrelearn;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class ConditionVideoView extends VideoView {
    public ConditionVideoView(Context context) {
        super(context);
    }

    public ConditionVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ConditionVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //使视频全屏播放
        int width = getDefaultSize(0, widthMeasureSpec);
        int height = getDefaultSize(0, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }
}