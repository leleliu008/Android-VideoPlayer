package com.fpliu.newton.video.list.entity

class ListResponseEntity<T> : ResponseEntity<MutableList<T>>() {

    val list: MutableList<T>?
        get() = super.data
}
