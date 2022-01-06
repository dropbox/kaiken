package com.dropbox.kaiken.sample.advancedsample.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.dropbox.kaiken.sample_with_di.app.R
import com.dropbox.kaiken.scoping.AuthOptionalActivity
import com.dropbox.kaiken.scoping.ViewingUserSelector
import com.dropbox.kaiken.scoping.putViewingUserSelector
import com.dropbox.kaiken.skeleton.scoping.AppScope
import com.dropbox.kaiken.skeleton.scoping.AuthOptionalScreenScope
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import kotlinx.android.synthetic.main.mainactivity.bottomNavigationView

class SplashActivity : AppCompatActivity(), AuthOptionalActivity {
    override fun onCreate(savedInstanceState: Bundle?) {
        setContent{

        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mainactivity)
        if (finishIfInvalidAuth()) return

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.mainNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Setup bottom navigation with navigation controller
        bottomNavigationView.setupWithNavController(navController)
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

