package com.dropbox.kaiken.sample.advancedsample.helloworldfeature

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.dropbox.kaiken.Injector
import com.dropbox.kaiken.skeleton.scoping.AuthOptionalScreenScope
import com.dropbox.kaiken.skeleton.scoping.cast
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AuthOptionalScreenScope::class)
interface LoginScreenComponent : Injector {
    fun intentFactory(): @JvmSuppressWildcards (Context, String) -> Intent
}

@Composable
fun LoginRouter() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        authAwareComposable("login") { _, presenter: LoginPresenter ->
            LoginScreen(
                presenter.model,
                { submit: LoginPresenter.Submit -> presenter.events.tryEmit(submit) },
                { userId: String ->
                    val intentFactory = this.cast<LoginScreenComponent>().intentFactory()
                    navController.context.startActivity(intentFactory(navController.context, userId))
                }
            ) { navController.navigate("forgot_password") }
        }
        authAwareComposable("forgot_password") { _, presenter: ForgotPasswordPresenter ->
            ForgotPasswordScreen(
                presenter.model
            ) { submit: ForgotPasswordPresenter.ForgotSubmit -> presenter.events.tryEmit(submit) }
        }
    }
}

@Preview
@Composable
fun previewLoginScreen() {
    LoginScreen(model = LoginPresenter.LoginNeeded, onSubmit = {}, onLoggedInSuccess = {}) {}
}

@Preview
@Composable
fun previewForgotPasswordScreen() {
    ForgotPasswordScreen(model = ForgotPasswordPresenter.Initial) {}
}
