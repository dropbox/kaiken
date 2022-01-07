package com.dropbox.kaiken.sample.advancedsample.helloworldfeature

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
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
import com.squareup.anvil.annotations.ContributesMultibinding
import com.squareup.anvil.annotations.ContributesTo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
            val screenScope = rememberCoroutineScope()
            LoginScreen(
                presenter.model.value,
                { submit: Submit -> screenScope.launch { presenter.onSubmit(submit) } },
                this.cast<LoginScreenComponent>().intentFactory()
            ) { navController.navigate("forgot_password") }
        }


        authOptionalComposable("forgot_password") { backstackEntry, presenter: ForgotPasswordPresenter ->
            val screenScope = rememberCoroutineScope()
            ForgotPasswordScreen(
                presenter.model.value
            ) { submit: ForgotSubmit -> screenScope.launch { presenter.onSubmit(submit) } }
        }
    }
}

@Preview
@Composable
fun previewLoginScreen(){
   val intentFactory: @JvmSuppressWildcards (Context, String) -> Intent = {s,c-> Intent() }
    LoginScreen(model = LoginNeeded, onSubmit = {}, intentFactory = intentFactory) {}
}
@Preview
@Composable
fun previewForgotPasswordScreen(){
    ForgotPasswordScreen(model = Initial) {}
}


sealed interface LoginEvent
data class Submit(val userInput: UserInput) : LoginEvent

sealed interface LoginModel
data class LoginSuccess(val userId: String) : LoginModel
object LoginNeeded : LoginModel


sealed interface ForgotEvent
class ForgotSubmit(val email: String) : ForgotEvent

sealed interface ForgotModel
object Initial : ForgotModel
object ForgotLoading : ForgotModel

class PasswordReset(val display: String) : ForgotModel



interface LoginPresenter : BasePresenter {
    suspend fun onSubmit(event: Submit)
    val model: MutableState<LoginModel>
}

@SingleIn(AuthOptionalScreenScope::class)
@ContributesMultibinding(AuthOptionalScreenScope::class, boundType = BasePresenter::class)
class RealLoginPresenter @Inject constructor(
    val userFlow: MutableSharedFlow<UserInput>, //user flow is a flow to a user service/dao
) : LoginPresenter {

    override val model: MutableState<LoginModel> = mutableStateOf(LoginNeeded)
    override suspend fun onSubmit(event: Submit) {
        userFlow.emit(event.userInput) //api
        model.value = LoginSuccess(userId = event.userInput.userId)
    }
}

interface ForgotPasswordPresenter : BasePresenter {
    suspend fun onSubmit(event: ForgotSubmit)
    val model: MutableState<ForgotModel>
}

@SingleIn(AuthOptionalScreenScope::class)
@ContributesMultibinding(AuthOptionalScreenScope::class, boundType = BasePresenter::class)
class RealForgotPresenter @Inject constructor(
    val passwordApi: PasswordApi,
) : ForgotPasswordPresenter {
    override val model: MutableState<ForgotModel> = mutableStateOf(Initial)
    override suspend fun onSubmit(event: ForgotSubmit) {
        CoroutineScope(Dispatchers.IO).launch {
            passwordApi.callApi(event.email)
            model.value = PasswordReset("check your password")
        }
        ForgotLoading
    }
}

interface PasswordApi {
    suspend fun callApi(email: String): Boolean
}

@ContributesBinding(AuthOptionalScreenScope::class)
class RealPasswordApi @Inject constructor() : PasswordApi {
    override suspend fun callApi(email: String): Boolean {
        return true
    }
}