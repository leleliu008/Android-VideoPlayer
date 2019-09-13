package com.fpliu.newton.video.list

import com.fpliu.newton.video.list.entity.ListResponseEntity
import com.fpliu.newton.video.list.entity.PlayItem
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface HTTPAPI {

    @GET("/api/video/list/{page}")
    fun requestPlayList(@Path("page") page: Int) : Observable<ListResponseEntity<PlayItem>>
}