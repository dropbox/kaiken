package com.dropbox.kaiken.sample.advancedsample.helloworldfeature

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.dropbox.kaiken.Injector
import com.dropbox.kaiken.scoping.ViewingUserSelector
import com.dropbox.kaiken.scoping.putViewingUserSelector
import com.dropbox.kaiken.skeleton.scoping.AuthOptionalScreenScope
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import com.dropbox.kaiken.skeleton.scoping.cast
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.launch

@ContributesTo(AuthOptionalScreenScope::class)
interface LoginScreenComponent : Injector {
    fun intentFactory(): @JvmSuppressWildcards (Context, String) -> Intent
}

@Composable
fun LoginRouter() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        authAwareComposable("login") { backstackEntry, presenter: LoginPresenter ->
            val screenScope = rememberCoroutineScope()
            LoginScreen(
                presenter.model.value,
                { submit: LoginPresenter.Submit -> screenScope.launch { presenter.onSubmit(submit) } },
                this.cast<LoginScreenComponent>().intentFactory()
            ) { navController.navigate("forgot_password") }
        }
        authAwareComposable("forgot_password") { backstackEntry, presenter: ForgotPasswordPresenter ->
            val screenScope = rememberCoroutineScope()
            ForgotPasswordScreen(
                presenter.model.value
            ) { submit: ForgotSubmit -> screenScope.launch { presenter.onSubmit(submit) } }
        }
    }
}

@Preview
@Composable
fun previewLoginScreen() {
    val intentFactory: @JvmSuppressWildcards (Context, String) -> Intent = { s, c -> Intent() }
    LoginScreen(model = LoginPresenter.LoginNeeded, onSubmit = {}, intentFactory = intentFactory) {}
}

@Preview
@Composable
fun previewForgotPasswordScreen() {
    ForgotPasswordScreen(model = Initial) {}
}


