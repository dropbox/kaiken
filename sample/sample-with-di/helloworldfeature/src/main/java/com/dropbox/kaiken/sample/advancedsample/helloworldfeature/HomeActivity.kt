package com.dropbox.kaiken.sample.advancedsample.helloworldfeature

import android.os.Bundle

class HomeActivity : AuthRequiredComposeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeRouter()
        }
    }
}
