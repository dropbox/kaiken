package com.dropbox.kaiken.sample.advancedsample.helloworldfeature

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dropbox.kaiken.Injector
import com.dropbox.kaiken.runtime.InjectorFactory
import com.dropbox.kaiken.skeleton.scoping.AuthAwareInjectorHolder
import com.dropbox.kaiken.skeleton.scoping.AuthOptionalActivityComponent
import com.dropbox.kaiken.skeleton.scoping.AuthOptionalScreenScope
import com.dropbox.kaiken.skeleton.scoping.authOptionalInjectorFactory
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AuthOptionalScreenScope::class)
interface LoginScreenComponent : Injector {
    fun presenter(): LoginPresenter
    fun intentFactory(): @JvmSuppressWildcards (Context, String) -> Intent
}

class LoginFragment : AuthAwareInjectorHolder<AuthOptionalActivityComponent>() {
    override fun getInjectorFactory(): InjectorFactory<AuthOptionalActivityComponent> =
        authOptionalInjectorFactory()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                LoginRouter()
            }
        }
    }

    @Composable
    private fun LoginRouter() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "login") {
            composable("login") { backstackEntry ->
                AuthOptionalLocalProvider(backstackEntry) {
                    LoginScreen()
                }
            }
        }
    }
}
