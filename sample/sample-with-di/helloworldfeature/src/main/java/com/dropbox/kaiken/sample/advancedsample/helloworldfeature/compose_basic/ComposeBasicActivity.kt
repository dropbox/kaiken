package com.dropbox.kaiken.sample.advancedsample.helloworldfeature.compose_basic

import android.os.Bundle
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.AuthRequiredComposeActivity
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.setContent

class ComposeBasicActivity : AuthRequiredComposeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // BasicScreen()
            BasicRouter()
        }
    }
}