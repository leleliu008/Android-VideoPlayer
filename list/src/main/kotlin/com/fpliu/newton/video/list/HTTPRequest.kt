package com.fpliu.newton.video.list

import com.fpliu.newton.http.RetrofitRequest

const val HTTP_BASE_URL = "http://blog.fpliu.com/"
//const val HTTP_BASE_URL = "http://192.168.0.115:3000/"

open class HTTPAPIProxy(private val httpApi: HTTPAPI) : HTTPAPI by httpApi

object HTTPRequest : HTTPAPIProxy(RetrofitRequest.getRetrofit(HTTP_BASE_URL).create(HTTPAPI::class.java))

