package com.dropbox.kaiken.sample.advancedsample.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.dropbox.kaiken.sample_with_di.app.R
import com.dropbox.kaiken.scoping.AuthRequiredActivity
import kotlinx.android.synthetic.main.authactivity.bottomNavigationView

class LoggedInActivity : AppCompatActivity(), AuthRequiredActivity {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (finishIfInvalidAuth()) return
        setContentView(R.layout.authactivity)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.mainNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Setup bottom navigation with navigation controller
        bottomNavigationView.setupWithNavController(navController)
    }
}
