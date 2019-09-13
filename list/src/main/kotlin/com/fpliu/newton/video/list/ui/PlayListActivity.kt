package com.fpliu.newton.video.list.ui

import android.os.Bundle
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fpliu.newton.log.Logger
import com.fpliu.newton.ui.list.PullableRecyclerViewActivity
import com.fpliu.newton.ui.pullable.PullType
import com.fpliu.newton.ui.pullable.PullableViewContainer
import com.fpliu.newton.ui.recyclerview.decoration.ListDividerItemDecoration
import com.fpliu.newton.ui.recyclerview.holder.ItemViewHolder
import com.fpliu.newton.util.getColorInt
import com.fpliu.newton.util.getDimensionPixelSize
import com.fpliu.newton.video.list.HTTPRequest
import com.fpliu.newton.video.list.R
import com.fpliu.newton.video.list.entity.PlayItem
import com.fpliu.newton.video.player.VideoPlayerActivity
import com.uber.autodispose.autoDisposable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class PlayListActivity : PullableRecyclerViewActivity<PlayItem>() {

    companion object {
        private val TAG = PlayListActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = "播放列表"

        val color = getColorInt(R.color.c_ebebeb)
        val height = getDimensionPixelSize(R.dimen.dp750_1)
        val padding = getDimensionPixelSize(R.dimen.dp750_30)
        setItemDecoration(ListDividerItemDecoration().color(color).height(height).padding(padding))
    }

    override fun onRefreshOrLoadMore(pullableViewContainer: PullableViewContainer<RecyclerView>, pullType: PullType, pageNum: Int, pageSize: Int) {
        HTTPRequest
            .requestPlayList(pageNum)
            .filter { it.filterIfFalseThenThrowAException() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(disposeOnDestroy())
            .subscribe({
                finishRequestSuccessWithErrorImageAndMessageIfItemsEmpty(pullType, it.list, R.mipmap.buffer_anim_1, "暂无数据")
            }, {
                Logger.e(TAG, "onRefreshOrLoadMore()", it)
                finishRequestSuccessWithErrorImageAndMessageIfItemsEmpty(pullType, null, R.mipmap.buffer_anim_1, "网络异常")
            })
    }

    override fun onBindLayout(parent: ViewGroup, viewType: Int) = R.layout.play_list_item

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int, item: PlayItem) {
        holder.id(R.id.tv).text(item.name)
    }

    override fun onItemClick(holder: ItemViewHolder, position: Int, item: PlayItem) {
        VideoPlayerActivity.start(this, item.name, item.url)
    }
}
