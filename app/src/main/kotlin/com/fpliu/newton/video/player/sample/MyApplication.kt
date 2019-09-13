package com.fpliu.newton.video.player.sample

import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks2
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextPaint
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.fpliu.kotlin.util.jdk.touch
import com.fpliu.newton.crash.CrashHandler
import com.fpliu.newton.http.RetrofitRequest
import com.fpliu.newton.http.converter.StringConverterFactory
import com.fpliu.newton.http.cookie.MemoryCookieJar
import com.fpliu.newton.http.download.DownloadInfoPersistent2File
import com.fpliu.newton.http.download.Downloader
import com.fpliu.newton.http.interceptor.LogInterceptor
import com.fpliu.newton.log.Logger
import com.fpliu.newton.ui.base.BaseActivity
import com.fpliu.newton.ui.pullable.PullableViewContainer
import com.fpliu.newton.ui.stateview.StateView
import com.fpliu.newton.ui.statusbar.StatusBarUtil
import com.fpliu.newton.util.*
import com.fpliu.newton.video.list.HTTP_BASE_URL
import com.fpliu.newton.video.list.ui.PlayListActivity
import com.jakewharton.rxbinding3.view.clicks
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.constant.SpinnerStyle
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import com.squareup.leakcanary.LeakCanary
import com.uber.autodispose.autoDisposable
import com.umeng.analytics.MobclickAgent
import io.reactivex.plugins.RxJavaPlugins
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.internal.http.BridgeInterceptor
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

/**
 * 进程的入口
 * @author 792793182@qq.com 2018-03-24.
 */
class MyApplication : Application(), Application.ActivityLifecycleCallbacks {

    companion object {
        private val TAG = MyApplication::class.java.simpleName
    }

    override fun onCreate() {
        super.onCreate()

        //内存泄漏工具
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)

        val processName = getCurrentProcessName()
        Log.i(TAG, "VP_MyApplication: processName = $processName")

        val packageName = packageName

        if ("$packageName:remote" == processName) {
            return
        } else if ("$packageName:pushservice" == processName) {
            return
        }

        registerActivityLifecycleCallbacks(this)

        Logger.init(this, "VP_", true)

        CrashHandler.init(this, null)

        if (!BuildConfig.DEBUG) {
            //RxJava的全局异常处理，防止崩溃
            RxJavaPlugins.setErrorHandler { ErrorHandler.onError(it) }
        }

        //读取渠道信息
//        val channelNumber = WalleChannelReader.getChannel(this) ?: "0"

//        val channelName = AppStoreChannel.getChannelName(channelNumber)

        // https://developer.umeng.com/docs/66632/detail/66890
        // adb logcat | grep "UMLog"
//        UMConfigure.setLogEnabled(BuildConfig.DEBUG)
//        UMConfigure.init(this, Config.UMENG_APP_KEY, channelName, UMConfigure.DEVICE_TYPE_PHONE, "")

        // 通过代码配置，而不是在AndroidManifest.xml中配置
        // http://dev.umeng.com/analytics/android-doc/integration
//        MobclickAgent.startWithConfigure(MobclickAgent.UMAnalyticsConfig(this, Config.UMENG_APP_KEY, channelName))

        // Debug模式下不上报UncaughtException到友盟
        // http://dev.umeng.com/analytics/android-doc/integration#4
        if (BuildConfig.DEBUG) {
            MobclickAgent.setCatchUncaughtExceptions(false)
        }

        //友盟分享设置友盟的appKey，通过代码配置，而不是在AndroidManifest.xml中配置
//        UMShareAPI.init(this, Config.UMENG_APP_KEY)
//
//        //初始化友盟分享SDK
//        PlatformConfig.setWeixin(Config.WX_APPID, Config.WX_SECRET)
//        PlatformConfig.setSinaWeibo(Config.WEIBO_APP_KEY, Config.WEIBO_SECRET_KEY, Config.WEIBO_REDIRECT_URL)
//        PlatformConfig.setQQZone(Config.QQ_APPID, Config.QQ_SECRET)
//        UMShareAPI.get(this)
//
//        Logger.i(TAG, "umeng_socialize_version = ${com.umeng.socialize.umengqq.BuildConfig.UMENG_VERSION}")

//        ViewHolderAbs.setImageLoader(ViewHolderAbsImageLoader())
//        ItemViewHolder.setImageLoader(ItemViewHolderImageLoader())

        PullableViewContainer.startPageNumber = 1

        //空列表背景色
        StateView.bgColor = getColorInt(R.color.c_f4f4f4)

        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, _ -> ClassicsHeader(context) }
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, _ ->
            //指定为经典Footer，默认是 BallPulseFooter
            ClassicsFooter(context).setSpinnerStyle(SpinnerStyle.Translate)
        }

        RetrofitRequest.init(object : RetrofitRequest.AbstractConfig() {

            val cookieJar = MemoryCookieJar()

            override fun getBaseUrl() = HTTP_BASE_URL

            override fun createOkHttpClientBuilder() = super.createOkHttpClientBuilder().apply {
                //设置缓存目录和缓存大小：20M
                cache(Cache(cacheDir, 20 * 1024 * 1024))
                cookieJar(cookieJar)
            }

            override fun getInterceptors() = ArrayList<Interceptor>().apply {
                add(BridgeInterceptor(cookieJar))
//                add(MyRequestInterceptor(channelNumber))
                //用于打印日志（里面包含了GZip的解压）
                if (BuildConfig.DEBUG) {
                    add(LogInterceptor { Logger.i("HTTP", it) })
                }
//                //处理解密
//                add(ResponseDecryptInterceptor())
//                //用于GZip的解压
//                add(ResponseGzipInterceptor())
            }

            override fun getConverterFactories() = ArrayList<Converter.Factory>().apply {
                add(StringConverterFactory())
                add(GsonConverterFactory.create())
            }

            override fun getCallAdapterFactories() = ArrayList<CallAdapter.Factory>().apply {
                add(RxJava2CallAdapterFactory.create())
            }
        })
        //设置下载相关的元信息存放位置
        Downloader.downloadInfoPersistent = DownloadInfoPersistent2File(cacheDir.absolutePath)

        //创建.nomedia隐藏文件，这样图库等媒体库就不会扫描我们的目录了
        try {
            File("$myDir/.nomedia").touch()
        } catch (e: Exception) {
            ErrorHandler.onError(e)
        }

        //全局替换字体
        try {
            globalReplaceFont("Alibaba_PuHuiTi_Light.otf")
        } catch (e: Exception) {
            ErrorHandler.onError(e)
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        Logger.i(TAG, "onActivityCreated() activity = $activity, savedInstanceState = $savedInstanceState")

        val headHeight = dp2px(48)

        if (activity is BaseActivity) {
            val baseView = activity.contentView.apply {
                setBackgroundColor(Color.WHITE)
            }

            baseView.headBarLayout.apply {
                layoutParams.height = headHeight

                background = object : ColorDrawable(Color.WHITE) {
                    val paint = Paint().apply {
                        isAntiAlias = true
                        isDither = true
                        color = getColorInt(R.color.c_ebebeb)
                    }

                    override fun draw(canvas: Canvas) {
                        super.draw(canvas)
                        canvas.drawLine(0f, height.toFloat(), width.toFloat(), height.toFloat(), paint)
                    }
                }

                findViewById<TextView>(R.id.base_view_head_title).apply {
                    setTextColorRes(R.color.c_101010)
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, getDimension(R.dimen.sp750_30))
                    (paint as TextPaint).isFakeBoldText = true
                }

                ImageView(activity).apply {
                    setImageResource(R.mipmap.ic_back_black)
                    setPadding(30, 0, 70, 0)
                    clicks().autoDisposable(activity.disposeOnDestroy()).subscribe { activity.onLeftBtnClick() }
                }.let { setLeftView(it) }
            }

            if (activity is PlayListActivity) {
                baseView.statusBarPlaceHolder.apply {
                    setBackgroundColor(Color.WHITE)
                    visibility = View.VISIBLE
                }
            }

            activity.toastLayout.apply {
                setBackgroundColorRes(R.color.c_00ddbb)
                layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, headHeight).apply {
                    topMargin = getStatusBarHeight()
                }
            }
        }

        StatusBarUtil.setRootViewFitsSystemWindows(activity, true)
        //设置状态栏透明
        StatusBarUtil.setTranslucentStatus(activity)
        //一般的手机的状态栏文字和图标都是白色的, 可如果你的应用也是纯白色的, 或导致状态栏文字看不清
        //所以如果你是这种情况,请使用以下代码, 设置状态使用深色文字图标风格, 否则你可以选择性注释掉这个if内容
        if (!StatusBarUtil.setStatusBarDarkTheme(activity, true)) {
            //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
            //这样半透明+白=灰, 状态栏的文字能看得清
            StatusBarUtil.setStatusBarColor(activity, 0x55000000)
        }
    }

    override fun onActivityStarted(activity: Activity) {
        Logger.i(TAG, "onActivityStarted() activity = $activity")
    }

    override fun onActivityResumed(activity: Activity) {
        Logger.i(TAG, "onActivityResumed() activity = $activity")
        MobclickAgent.onResume(activity)
    }

    override fun onActivityPaused(activity: Activity) {
        Logger.i(TAG, "onActivityPaused() activity = $activity")
        MobclickAgent.onPause(activity)
    }

    override fun onActivityStopped(activity: Activity) {
        Logger.i(TAG, "onActivityStopped() activity = $activity")
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        Logger.i(TAG, "onActivitySaveInstanceState() activity = $activity, outState = $outState")
    }

    override fun onActivityDestroyed(activity: Activity) {
        Logger.i(TAG, "onActivityDestroyed() activity = $activity")
    }

    override fun onLowMemory() {
        Logger.i(TAG, "onLowMemory()")
        super.onLowMemory()
//        Glide.get(this).clearMemory()
    }

    override fun onTrimMemory(level: Int) {
        Logger.i(TAG, "onTrimMemory() level = $level")
        super.onTrimMemory(level)
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) { //进入后台的时候会是这个level
//            Glide.get(this).clearMemory()
        }
//        Glide.get(this).trimMemory(level)
    }
}