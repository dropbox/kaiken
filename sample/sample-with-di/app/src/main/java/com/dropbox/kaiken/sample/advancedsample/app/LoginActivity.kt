package com.dropbox.kaiken.sample.advancedsample.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.dropbox.common.inject.AuthOptionalScreenScope
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.AuthOptionalComposeActivity
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.HomeActivity
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.LoginRouter
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.setContent
import com.dropbox.kaiken.scoping.ViewingUserSelector
import com.dropbox.kaiken.scoping.putViewingUserSelector
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides

class LoginActivity : AuthOptionalComposeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginRouter()
        }
    }
}

fun getLaunchIntent(
    context: Context,
    userId: String,
): Intent = Intent(context, HomeActivity::class.java).apply {
    putViewingUserSelector(ViewingUserSelector.fromUserId(userId))
}

@ContributesTo(AuthOptionalScreenScope::class)
@Module
class IntentFactoryModule {
    @Provides
    @SingleIn(AuthOptionalScreenScope::class)
    fun intentFactory(): @JvmSuppressWildcards (Context, String) -> Intent =
        { context: Context, userId: String -> getLaunchIntent(context, userId) }
}
