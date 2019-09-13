package com.fpliu.newton.video.player

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.video_player_loading_view.view.*

/**
 * 载入视频时候的视图
 * 792793182@qq.com 2018-08-24.
 */
class VideoLoadingView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attributeSet, defStyleAttr) {

    private var animation: AnimationDrawable

    init {
        View.inflate(context, R.layout.video_player_loading_view, this)
        animation = animationIv.drawable as AnimationDrawable
    }

    fun show(bgUrl: String? = null) {
        visibility = View.VISIBLE
        bgIv.visibility = View.VISIBLE
//        bgIv.displayImage(bgUrl, R.mipmap.live_bg_default)
        startAnimation()
    }

    fun hide() {
        visibility = View.GONE
        bgIv.visibility = View.GONE
        stopAnimation()
    }

    fun startAnimation() {
        if (!animation.isRunning) {
            animation.start()
        }
    }

    fun stopAnimation() {
        if (animation.isRunning) {
            animation.stop()
        }
    }
}