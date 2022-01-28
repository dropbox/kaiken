package com.dropbox.kaiken.sample.advancedsample.helloworldfeature.authed_example

import android.os.Bundle
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.AuthRequiredComposeActivity
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.setContent

class HomeActivity : AuthRequiredComposeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeRouter()
        }
    }
}
