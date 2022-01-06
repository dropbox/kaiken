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
import kotlinx.coroutines.flow.MutableSharedFlow

@Composable
//Stateless to impress Ryan
fun LoginScreen(
    model: LoginModel,
    events: MutableSharedFlow<LoginEvent>,
    intentFactory: @JvmSuppressWildcards (Context, String) -> Intent,
    onClick: () -> Unit
) {
    MaterialTheme {
        Column {
            when (model) {
                is LoginNeeded -> loginView(events,onClick)
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
   val context= LocalContext.current
    LaunchedEffect(key1 = Unit) {
      context.startActivity(intentFactory(context, model.userId))
    }
}


@Composable
fun loginView(events: MutableSharedFlow<LoginEvent>, onClick: () -> Unit) {
    // In Compose world
    Text("Enter User ID")
    var text by remember { mutableStateOf("1") }
    TextField(value = text, onValueChange = { text = it }, label = { Text("Label") })
    submitter { events.tryEmit(Submit(UserInput(text, "Bart"))) }
    Text("Forgot Password", modifier = Modifier.clickable { onClick() } )
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
    events: MutableSharedFlow<ForgotEvent>) {
    MaterialTheme {
        Column {
            when (model) {
                is Initial -> {
                    var text by remember { mutableStateOf("1") }
                    TextField(value = text, onValueChange = { text = it }, label = { Text("Label") })
                    Button(onClick = { events.tryEmit(ForgotSubmit(text)) }) {
                        Text("Login")
                    }
                }
                is ForgotLoading -> Text("Sending you new password to email")
            }
        }
    }
}
