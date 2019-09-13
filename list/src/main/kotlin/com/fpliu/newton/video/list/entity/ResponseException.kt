package com.fpliu.newton.video.list.entity

import java.lang.RuntimeException

class ResponseException(
    val code: Int,
    message: String? = null,
    val data: Any? = null,
    cause: Throwable? = null
) : RuntimeException(message, cause)