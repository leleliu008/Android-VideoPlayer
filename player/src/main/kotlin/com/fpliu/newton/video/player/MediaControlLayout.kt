package com.fpliu.newton.video.player

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import com.fpliu.newton.log.Logger
import com.pili.pldroid.player.widget.PLVideoTextureView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.video_player_media_control_layout.view.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

/**
 * 视频播放器 - 播放控制视图
 * @author 792793182@qq.com 2018-11-19.
 */
class MediaControlLayout @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr), View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    companion object {
        private val TAG = MediaControlLayout::class.java.simpleName
    }

    enum class State {
        IDLE,
        TO_PLAY_ING,
        PLAY_ING,
        TO_IDLE
    }

    private val state by lazy { AtomicReference<State>(State.IDLE) }

    private var playingDisposable: Disposable? = null

    var durationMs: Long = 0
        set(value) {
            if (durationMs == 0L) {
                field = value
                totalDurationTv.text = toTimeFormatStr(value)
                startUpdateSeekBar()
            }
        }

    var videoView: PLVideoTextureView? = null

    init {
        View.inflate(context, R.layout.video_player_media_control_layout, this)
        startTimeTv.text = "00:00"
        playOrPauseBtn.setOnClickListener(this)
        seekBar.setOnSeekBarChangeListener(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        playOrPauseBtn.setOnClickListener(null)
        seekBar.setOnSeekBarChangeListener(null)
        videoView?.stopPlayback()
        playingDisposable?.run {
            if (!isDisposed) {
                dispose()
            }
        }
    }

    override fun onClick(v: View) {
        when (v) {
            playOrPauseBtn -> {
                when (state.get()) {
                    State.IDLE -> startPlayVideoIfNeeded()
                    State.PLAY_ING -> pausePlayVideoIfNeeded()
                }
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        Logger.i(TAG, "onStartTrackingTouch()")
        playingDisposable?.run {
            if (!isDisposed) {
                dispose()
            }
        }
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        Logger.i(TAG, "onStopTrackingTouch()")
        val beginTime = (seekBar.progress / 100f * durationMs).toLong()
        videoView?.run {
            seekTo(beginTime)
            startUpdateSeekBar()
        }
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
//        Logger.i(TAG, "onProgressChanged() progress = $progress, fromUser = $fromUser")
    }

    fun startPlayVideoIfNeeded() {
        if (state.compareAndSet(State.IDLE, State.TO_PLAY_ING)) {
            playOrPauseBtn.setImageResource(R.mipmap.video_player_btn_pause)
            videoView?.run {
                start()
                startUpdateSeekBar()
            }
            state.set(State.PLAY_ING)
        }
    }

    private fun pausePlayVideoIfNeeded() {
        if (state.compareAndSet(State.PLAY_ING, State.TO_IDLE)) {
            playOrPauseBtn.setImageResource(R.mipmap.video_player_btn_play)
            videoView?.pause()
            playingDisposable?.run {
                if (!isDisposed) {
                    dispose()
                }
            }
            state.set(State.IDLE)
        }
    }

    fun onCompletion() {
        playOrPauseBtn.setImageResource(R.mipmap.video_player_btn_play)
        playingDisposable?.run {
            if (!isDisposed) {
                dispose()
            }
        }
        state.set(State.IDLE)
    }

    private fun startUpdateSeekBar() {
        playingDisposable = Observable
            .interval(100, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val current = videoView?.currentPosition ?: return@subscribe
                startTimeTv.text = toTimeFormatStr(current)
                seekBar.progress = ((current / durationMs.toFloat()) * 100).toInt()
            }
    }

    private fun toTimeFormatStr(value: Long): String {
        val totalSeconds = value / 1000
        val minute = totalSeconds / 60
        val seconds = totalSeconds % 60
        val formatStr = StringBuilder()
        if (minute < 10) {
            formatStr.append("0")
        }
        formatStr.append(minute)
        formatStr.append(":")
        if (seconds < 10) {
            formatStr.append("0")
        }
        formatStr.append(seconds)
        return formatStr.toString()
    }
}