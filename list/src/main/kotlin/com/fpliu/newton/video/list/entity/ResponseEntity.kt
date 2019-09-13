package com.fpliu.newton.video.list.entity

/**
 * @author 792793182@qq.com 2017-06-05.
 */
open class ResponseEntity<T> {
    val code: Int = -1
    val message: String? = null
    val data: T? = null

    override fun toString(): String {
        return "ResponseEntity(code=$code, message=$message, data=$data)"
    }

    /**
     * @return 是否需要继续处理, false表示不需要继续处理, true表示需要继续处理
     */
    fun filter() = code == 0

    /**
     * @return 是否需要继续处理, false表示不需要继续处理, true表示需要继续处理
     */
    fun filterIfFalseThenThrowAException() = if (code == 0) true else throw ResponseException(code, message, data)
}