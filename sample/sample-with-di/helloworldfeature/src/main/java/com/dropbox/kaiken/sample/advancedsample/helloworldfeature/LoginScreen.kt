package com.dropbox.kaiken.sample.advancedsample.helloworldfeature

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.dropbox.kaiken.skeleton.scoping.AuthOptionalScreenScope
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import com.dropbox.kaiken.skeleton.usermanagement.auth.UserInput
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

interface LoginPresenter : BasePresenter {
    sealed interface LoginEvent
    data class Submit(val userInput: UserInput) : LoginEvent

    sealed interface LoginModel
    data class LoginSuccess(val userId: String) : LoginModel
    object LoginNeeded : LoginModel
    suspend fun onSubmit(event: Submit)
    val model: MutableState<LoginModel>
}

@SingleIn(AuthOptionalScreenScope::class)
@ContributesMultibinding(AuthOptionalScreenScope::class, boundType = BasePresenter::class)
class RealLoginPresenter @Inject constructor(
    val userFlow: MutableSharedFlow<UserInput>, //user flow is a flow to a user service/dao
) : LoginPresenter {

    override val model: MutableState<LoginPresenter.LoginModel> = mutableStateOf(LoginPresenter.LoginNeeded)
    override suspend fun onSubmit(event: LoginPresenter.Submit) {
        userFlow.emit(event.userInput) //api
        model.value = LoginPresenter.LoginSuccess(userId = event.userInput.userId)
    }
}


@Composable
//Stateless to impress Ryan
fun LoginScreen(
    model: LoginPresenter.LoginModel,
    onSubmit: (LoginPresenter.Submit) -> Unit,
    intentFactory: @JvmSuppressWildcards (Context, String) -> Intent,
    onClickForgotPassword: () -> Unit
) {
    MaterialTheme {
        Column {
            when (model) {
                is LoginPresenter.LoginNeeded -> loginView(onSubmit, onClickForgotPassword)
                is LoginPresenter.LoginSuccess -> loggedInActivityLauncher(model, intentFactory)
            }
        }
    }
}

@Composable
private fun loggedInActivityLauncher(
    model: LoginPresenter.LoginSuccess,
    intentFactory: @JvmSuppressWildcards (Context, String) -> Intent
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        context.startActivity(intentFactory(context, model.userId))
    }
}


@Composable
fun loginView(onSubmit: (LoginPresenter.Submit) -> Unit, onForgotPassword: () -> Unit) {
    // In Compose world
    Text("Enter User ID")
    var text by remember { mutableStateOf("1") }
    TextField(value = text, onValueChange = { text = it }, label = { Text("Label") })
    submitter { onSubmit(LoginPresenter.Submit(UserInput(text, "Bart"))) }
    Text("Forgot Password", modifier = Modifier.clickable { onForgotPassword() })
}

@Composable
fun submitter(onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text("Login")
    }
}

