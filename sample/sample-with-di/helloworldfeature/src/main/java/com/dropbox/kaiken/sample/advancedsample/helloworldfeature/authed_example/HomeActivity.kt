package com.dropbox.kaiken.sample.advancedsample.helloworldfeature.authed_example

import android.os.Bundle
import androidx.compose.animation.ExperimentalAnimationApi
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.AuthRequiredComposeActivity
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.setContent

@OptIn(ExperimentalAnimationApi::class)
class HomeActivity : AuthRequiredComposeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeRouter()
        }
    }
}
