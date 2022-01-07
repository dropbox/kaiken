package com.dropbox.kaiken.sample.advancedsample.helloworldfeature

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.dropbox.kaiken.skeleton.scoping.AppScope
import com.dropbox.kaiken.skeleton.scoping.AuthOptionalScreenScope
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ForgotEvent
class ForgotSubmit(val email: String) : ForgotEvent

sealed interface ForgotModel
object Initial : ForgotModel
object ForgotLoading : ForgotModel

class PasswordReset(val display: String) : ForgotModel


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


@Composable
fun ForgotPasswordScreen(
    model: ForgotModel,
    onForgotSubmit: (ForgotSubmit) -> Unit
) {
    MaterialTheme {
        Column {
            when (model) {
                is Initial -> {
                    var text by remember { mutableStateOf("1") }
                    TextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text("USERID to retrieve password") })
                    Button(onClick = { onForgotSubmit(ForgotSubmit(text)) }) {
                        Text("Forgot Password")
                    }
                }
                is ForgotLoading -> Text("Sending you new password to email")
                is PasswordReset -> Text("New Password Sent to User Email")
            }
        }
    }
}

interface PasswordApi {
    suspend fun callApi(email: String): Boolean
}

@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)
class RealPasswordApi @Inject constructor() : PasswordApi {
    override suspend fun callApi(email: String): Boolean {
        return true
    }
}