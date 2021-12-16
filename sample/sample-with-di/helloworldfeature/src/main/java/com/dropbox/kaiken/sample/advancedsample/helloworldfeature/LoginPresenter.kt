package com.dropbox.kaiken.sample.advancedsample.helloworldfeature

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.dropbox.kaiken.skeleton.scoping.AuthOptionalScreenScope
import com.dropbox.kaiken.skeleton.usermanagement.auth.UserInput
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

sealed interface LoginEvent
object LoginLaunched : LoginEvent
data class Submit(val userInput: UserInput) : LoginEvent

sealed interface LoginModel
data class LoginSuccess(val userId: String) : LoginModel
object LoginNeeded : LoginModel

interface LoginPresenter : Presenter<LoginEvent, LoginModel>

@ContributesBinding(AuthOptionalScreenScope::class)
class RealLoginPresenter @Inject constructor(val userFlow: MutableSharedFlow<UserInput>) :
    LoginPresenter {

    override val events: MutableStateFlow<LoginEvent> = MutableStateFlow(LoginLaunched)

    @Composable
    override fun present(): LoginModel {
        var loginModel: LoginModel by remember { mutableStateOf(LoginNeeded) }
        LaunchedEffect(Unit) {
            events.collect { event ->
                loginModel = when (event) {
                    is Submit -> {
                        userFlow.emit(event.userInput)
                        LoginSuccess(userId = event.userInput.userId)
                    }
                    else -> LoginNeeded
                }
            }
        }
        return loginModel
    }
}

interface Presenter<Event, Model> {
    @Composable
    fun present(): Model

    val events: MutableStateFlow<Event>
}
