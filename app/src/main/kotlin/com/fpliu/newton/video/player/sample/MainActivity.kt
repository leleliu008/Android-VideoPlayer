package com.fpliu.newton.video.player.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fpliu.newton.util.startActivity
import com.fpliu.newton.video.list.ui.PlayListActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(PlayListActivity::class)
        finish()
    }
}