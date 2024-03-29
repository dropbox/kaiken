package com.dropbox.kaiken.sample.advancedsample.helloworldfeature

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.dropbox.common.inject.AuthOptionalScreenScope
import com.dropbox.kaiken.skeleton.core.SkeletonOauth2
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

class UserInput(val userId: String)
abstract class LoginPresenter :
    Presenter<LoginPresenter.LoginEvent, LoginPresenter.LoginModel, LoginPresenter.LoginEffect>(LoginNeeded) {
    sealed interface LoginEvent
    data class Submit(val userInput: UserInput) : LoginEvent

    sealed interface LoginModel
    object LoginNeeded : LoginModel

    sealed interface LoginEffect
    data class LoginSuccessful(val userId: String) : LoginEffect
}

@SingleIn(AuthOptionalScreenScope::class)
@ContributesMultibinding(
    AuthOptionalScreenScope::class,
    boundType = BasePresenter::class
)
class RealLoginPresenter @Inject constructor(
    val accountStore: AccountStore
) : LoginPresenter() {
    override suspend fun eventHandler(event: LoginEvent) {
        when (event) {
            is Submit -> {
                // in a real flow we would call api and get an access token
                accountStore.addUser(
                    DiSampleUser(
                        event.userInput.userId,
                        SkeletonOauth2("flebityfive"),
                        "Bart Always Bart"
                    )
                )
                emitEffect(LoginSuccessful(userId = event.userInput.userId))
            }
        }
    }
}

@Composable
fun LoginScreen(
    model: LoginPresenter.LoginModel,
    onSubmit: (LoginPresenter.Submit) -> Unit,
    onClickForgotPassword: () -> Unit
) {
    MaterialTheme {
        Column {
            when (model) {
                is LoginPresenter.LoginNeeded -> {
                    loginView(onSubmit, onClickForgotPassword)
                }
            }
        }
    }
}

@Composable
fun loginView(onSubmit: (LoginPresenter.Submit) -> Unit, onForgotPassword: () -> Unit) {
    // In Compose world
    Text("Enter User ID")
    var text by remember { mutableStateOf("1") }
    TextField(value = text, onValueChange = { text = it }, label = { Text("Label") })
    submitter { onSubmit(LoginPresenter.Submit(UserInput(text))) }
    Text("Forgot Password", modifier = Modifier.clickable { onForgotPassword() })
}

@Composable
fun submitter(onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text("Login")
    }
}
