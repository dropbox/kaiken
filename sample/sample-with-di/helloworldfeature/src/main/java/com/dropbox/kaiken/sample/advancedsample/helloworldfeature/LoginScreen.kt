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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.dropbox.kaiken.skeleton.usermanagement.auth.UserInput

@Composable
//Stateless to impress Ryan
fun LoginScreen(
    model: LoginModel,
    onSubmit: (Submit) -> Unit,
    intentFactory: @JvmSuppressWildcards (Context, String) -> Intent,
    onClickForgotPassword: () -> Unit
) {
    MaterialTheme {
        Column {
            when (model) {
                is LoginNeeded -> loginView(onSubmit, onClickForgotPassword)
                is LoginSuccess -> loggedInActivityLauncher(model, intentFactory)
            }
        }
    }
}

@Composable
private fun loggedInActivityLauncher(
    model: LoginSuccess,
    intentFactory: @JvmSuppressWildcards (Context, String) -> Intent
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        context.startActivity(intentFactory(context, model.userId))
    }
}


@Composable
fun loginView(onSubmit: (Submit) -> Unit, onForgotPassword: () -> Unit) {
    // In Compose world
    Text("Enter User ID")
    var text by remember { mutableStateOf("1") }
    TextField(value = text, onValueChange = { text = it }, label = { Text("Label") })
    submitter { onSubmit(Submit(UserInput(text, "Bart"))) }
    Text("Forgot Password", modifier = Modifier.clickable { onForgotPassword() })
}

@Composable
fun submitter(onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text("Login")
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
