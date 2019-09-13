package com.fpliu.newton.video.player

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.fpliu.newton.log.Logger
import com.fpliu.newton.ui.base.BaseActivity
import com.fpliu.newton.util.dp2px
import com.jakewharton.rxbinding3.view.clicks
import com.pili.pldroid.player.*
import com.pili.pldroid.player.widget.PLVideoView
import com.uber.autodispose.autoDisposable
import kotlinx.android.synthetic.main.video_player_activity.*
import java.util.concurrent.atomic.AtomicBoolean


/**
 * 视频播放器界面
 * 792793182@qq.com 2018-11-19.
 */
class VideoPlayerActivity : BaseActivity(),
    PLOnCompletionListener,
    PLOnErrorListener,
    PLOnInfoListener,
    PLOnVideoSizeChangedListener,
    View.OnClickListener {

    companion object {

        private val TAG = VideoPlayerActivity::class.java.simpleName

        private const val KEY_TITLE = "title"

        private const val KEY_VIDEO_PATH = "videoPath"

        fun start(activity: Activity, title: String, videoPath: String) {
            Intent(activity, VideoPlayerActivity::class.java).run {
                putExtra(KEY_TITLE, title)
                putExtra(KEY_VIDEO_PATH, videoPath)
                activity.startActivity(this)
            }
        }
    }

    private var isVideoLandscape = false

    //是否是加载的视频的第一桢
    private val isFirstReceiveOnInfoOfVideo = AtomicBoolean(true)

    private var title = ""
    private var videoPath = ""

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.run {
            putString(KEY_TITLE, title)
            putString(KEY_VIDEO_PATH, videoPath)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            intent?.run {
                title = getStringExtra(KEY_TITLE) ?: ""
                videoPath = getStringExtra(KEY_VIDEO_PATH) ?: ""
            }
        } else {
            title = savedInstanceState.getString(KEY_TITLE, "") ?: ""
            videoPath = savedInstanceState.getString(KEY_VIDEO_PATH, "") ?: ""
        }

        super.onCreate(savedInstanceState)

        if (!TextUtils.isEmpty(title)) {
            setTitle(title)
        }

        addContentView(R.layout.video_player_activity)

        val lp2 = rootView.layoutParams as CoordinatorLayout.LayoutParams
        lp2.behavior = null
        rootView.layoutParams = lp2

        contentView.removeView(contentView.appBarLayout)
        contentView.addView(contentView.appBarLayout, CoordinatorLayout.LayoutParams(MATCH_PARENT, dp2px(48)))
        contentView.appBarLayout.background = null
        contentView.headBarLayout.run {
            setBackgroundDrawable(ColorDrawable(Color.parseColor("#38101010")))
            setTitleTextColor(Color.WHITE)
            setLeftView(ImageView(context).apply {
                val padding = context.resources.getDimensionPixelSize(R.dimen.dp750_30)
                setPadding(padding, 0, padding, 0)
                setImageResource(R.mipmap.video_player_ic_back_white)
                background = null
                scaleType = ImageView.ScaleType.CENTER_INSIDE
                clicks().autoDisposable(disposeOnDestroy()).subscribe { onLeftBtnClick() }
            })
        }

        setupVideoView()
        registerVideoViewListeners()

        landscape2PortraitBtn.setOnClickListener(this)
        portrait2LandscapeBtn.setOnClickListener(this)

        videoLoadingView.show()

        videoView.setVideoPath(videoPath)
        mediaControlLayout.videoView = videoView
        mediaControlLayout.startPlayVideoIfNeeded()
    }

    private fun setupVideoView() {
        //缓冲控件
        videoView.apply {
            setBufferingIndicator(videoLoadingView)
            //全屏铺满
            displayAspectRatio = PLVideoView.ASPECT_RATIO_PAVED_PARENT

            val options = AVOptions().apply {
                //超时
                setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000)

                //硬解码
                setInteger(AVOptions.KEY_MEDIACODEC, AVOptions.MEDIA_CODEC_AUTO)

                // 默认的缓存大小，单位是 ms
                setInteger(AVOptions.KEY_CACHE_BUFFER_DURATION, 500)

                // 最大的缓存大小，单位是 ms
                setInteger(AVOptions.KEY_MAX_CACHE_BUFFER_DURATION, 4000)
            }
            setAVOptions(options)
        }
    }

    private fun registerVideoViewListeners() {
        videoView.apply {
            setOnCompletionListener(this@VideoPlayerActivity)
            setOnErrorListener(this@VideoPlayerActivity)
            setOnInfoListener(this@VideoPlayerActivity)
            setOnVideoSizeChangedListener(this@VideoPlayerActivity)
        }
    }

    override fun onVideoSizeChanged(width: Int, height: Int) {
        Logger.i(TAG, "onVideoSizeChanged() width = $width, height = $height")

        if (width > height) { //横屏录制的视频，但是，首次进入的时候，仍然以竖屏展示，用户想以横屏展示，可以点击按钮进行切换
            isVideoLandscape = true
            landscape2PortraitBtn.visibility = View.VISIBLE
            portrait2LandscapeBtn.visibility = View.GONE
            videoView.displayAspectRatio = PLVideoView.ASPECT_RATIO_16_9
        } else {
            isVideoLandscape = false
            landscape2PortraitBtn.visibility = View.GONE
            portrait2LandscapeBtn.visibility = View.GONE
            videoView.displayAspectRatio = PLVideoView.ASPECT_RATIO_PAVED_PARENT
        }
    }

    override fun onInfo(state: Int, extra: Int) {
//        Logger.i(TAG, "onInfo() state = $state, extra = $extra")
        when (state) {
            PLOnInfoListener.MEDIA_INFO_VIDEO_RENDERING_START -> { // 第一帧视频已成功渲染的回掉，注意：重连后也会调用
//                Logger.i(TAG, "onInfo() state = $state, MEDIA_INFO_VIDEO_RENDERING_START")
                if (isFirstReceiveOnInfoOfVideo.compareAndSet(true, false)) {
                    onReceiveFirstFrameVideo()
                }
                videoLoadingView.hide()
            }
            PLOnInfoListener.MEDIA_INFO_BUFFERING_START -> {
                Logger.i(TAG, "onInfo() state = $state, MEDIA_INFO_BUFFERING_START")
                videoLoadingView.startAnimation()
            }
            PLOnInfoListener.MEDIA_INFO_BUFFERING_END -> {
                Logger.i(TAG, "onInfo() state = $state, MEDIA_INFO_BUFFERING_END")
                videoLoadingView.stopAnimation()
            }
        }
    }

    override fun onError(errorCode: Int): Boolean {
        Logger.i(TAG, "onError() errorCode = $errorCode")
        mediaControlLayout.onCompletion()
        return true
    }

    override fun onCompletion() {
        Logger.i(TAG, "onCompletion()")
        mediaControlLayout.onCompletion()
    }

    //收到第一桢视频的回掉
    private fun onReceiveFirstFrameVideo() {
        Logger.i(TAG, "onReceiveFirstFrameVideo()")
        mediaControlLayout.durationMs = videoView.duration
    }

    override fun onClick(v: View) {
        when (v) {
            landscape2PortraitBtn -> { //切换到横屏
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                videoView.displayAspectRatio = PLVideoView.ASPECT_RATIO_PAVED_PARENT
                landscape2PortraitBtn.visibility = View.GONE
                portrait2LandscapeBtn.visibility = View.VISIBLE
            }
            portrait2LandscapeBtn -> { //切换到竖屏
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                videoView.displayAspectRatio = PLVideoView.ASPECT_RATIO_16_9
                landscape2PortraitBtn.visibility = View.VISIBLE
                portrait2LandscapeBtn.visibility = View.GONE
            }
        }
    }
}