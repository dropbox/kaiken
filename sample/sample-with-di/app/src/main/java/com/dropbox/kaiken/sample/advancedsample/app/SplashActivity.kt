package com.dropbox.kaiken.sample.advancedsample.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.LocalDResolver
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.LoginRouter
import com.dropbox.kaiken.scoping.AuthOptionalActivity
import com.dropbox.kaiken.scoping.ViewingUserSelector
import com.dropbox.kaiken.scoping.putViewingUserSelector
import com.dropbox.kaiken.skeleton.scoping.AuthOptionalScreenScope
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides


abstract class AuthOptionalComposeActivity : AppCompatActivity(), AuthOptionalActivity

fun AuthOptionalComposeActivity.setAuthOptionalContent(content: @Composable () -> Unit) {
    setContent {
        CompositionLocalProvider(LocalDResolver provides this) {
            content()
        }
    }
}

class SplashActivity : AuthOptionalComposeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        if(finishIfInvalidAuth()) return
        setAuthOptionalContent {
            LoginRouter()
        }
    }
}

fun getLaunchIntent(
    context: Context,
    userId: String,
): Intent = Intent(context, LoggedInActivity::class.java).apply {
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

