package com.dropbox.kaiken.sample.advancedsample.helloworldfeature

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.fragment.app.Fragment
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.dropbox.kaiken.Injector
import com.dropbox.kaiken.scoping.AuthAwareFragment
import com.dropbox.kaiken.skeleton.scoping.AuthOptionalScreenScope
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import com.dropbox.kaiken.skeleton.scoping.cast
import com.dropbox.kaiken.skeleton.usermanagement.auth.UserInput
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@ContributesTo(AuthOptionalScreenScope::class)
interface LoginScreenComponent : Injector {
    fun intentFactory(): @JvmSuppressWildcards (Context, String) -> Intent
}

abstract class AuthAwareFragment2 : AuthAwareFragment, Fragment()

class LoginFragment : AuthAwareFragment2() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return setContent { LoginRouter() }
    }
}


@Composable
fun LoginRouter() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        authOptionalComposable("login") { backstackEntry, presenter: LoginPresenter ->
            LoginScreen(
                presenter.model.value,
                presenter.events,
                this.cast<LoginScreenComponent>().intentFactory()
            ) { navController.navigate("forgot_password") }
        }


        authOptionalComposable("forgot_password") { backstackEntry, presenter: ForgotPasswordPresenter ->
            ForgotPasswordScreen(
                presenter.model.value,
                presenter.events,
            )
        }
    }
}

sealed interface LoginEvent
data class Submit(val userInput: UserInput) : LoginEvent

sealed interface LoginModel
data class LoginSuccess(val userId: String) : LoginModel
object LoginNeeded : LoginModel

typealias LoginPresenter = Presenter<LoginEvent, LoginModel>

sealed interface ForgotEvent

class ForgotSubmit(val email: String) : ForgotEvent

sealed interface ForgotModel
object Initial : ForgotModel
object ForgotLoading : ForgotModel

class PasswordReset(val display: String) : ForgotModel
typealias ForgotPasswordPresenter = Presenter<ForgotEvent, ForgotModel>


@ContributesTo(AuthOptionalScreenScope::class)
@Module
class PresenterBindings {
    @IntoSet
    @Provides
    fun provideLoginPresenter(realLoginPresenter: RealLoginPresenter): Presenter<*, *> =
        realLoginPresenter

    @IntoSet
    @Provides
    fun provideForgotPresenter(forgotPasswordPresenter: RealForgotPresenter): Presenter<*, *> =
        forgotPasswordPresenter
}


@SingleIn(AuthOptionalScreenScope::class)
class RealLoginPresenter @Inject constructor(
    //user flow is a flow to a user service/dao
    val userFlow: MutableSharedFlow<UserInput>,
) : LoginPresenter(initialState = LoginNeeded) {
    override val actionHandler: suspend (value: LoginEvent) -> LoginModel = { event ->
        when (event) {
            is Submit -> {
                userFlow.emit(event.userInput) //api
                LoginSuccess(userId = event.userInput.userId)
            }
        }
    }
}

@ContributesBinding(AuthOptionalScreenScope::class)
class RealPasswordApi @Inject constructor() : PasswordApi {
    override suspend fun callApi(): Boolean {
        return true
    }
}

interface PasswordApi {
    suspend fun callApi(): Boolean
}


@SingleIn(AuthOptionalScreenScope::class)
class RealForgotPresenter @Inject constructor(
    val passwordApi: PasswordApi
) : ForgotPasswordPresenter(initialState = Initial) {
    override val actionHandler: suspend (value: ForgotEvent) -> ForgotModel =
        { event: ForgotEvent ->
            when (event) {
                is ForgotSubmit -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        val result = passwordApi.callApi()
                        delay(5000)
                        model.value = PasswordReset("check your password")
                    }
                    ForgotLoading
                }
            }
        }
}



