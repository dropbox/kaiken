package com.dropbox.kaiken.sample.advancedsample.helloworldfeature

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
import androidx.compose.ui.platform.LocalContext
import com.dropbox.kaiken.skeleton.scoping.cast
import com.dropbox.kaiken.skeleton.usermanagement.auth.UserInput
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun LoginScreen() {
    val presenter = LocalComponent.current.cast<LoginScreenComponent>().presenter()
    val model = presenter.present()
    MaterialTheme {
        Column {
            when (model) {
                is LoginNeeded -> loginView(presenter.events)
                is LoginSuccess -> loggedInActivityLauncher(model)
            }
        }
    }
}

@Composable
private fun loggedInActivityLauncher(model: LoginSuccess) {
    val intentLauncher = LocalComponent.current.cast<LoginScreenComponent>().intentFactory()
   val context= LocalContext.current
    LaunchedEffect(key1 = Unit) {
      context.startActivity(intentLauncher(context, model.userId))
    }
}


@Composable
fun loginView(events: MutableStateFlow<LoginEvent>) {
    // In Compose world
    Text("Enter User ID")
    var text by remember { mutableStateOf("1") }
    TextField(value = text, onValueChange = { text = it }, label = { Text("Label") })
    submitter { events.tryEmit(Submit(UserInput(text, "Bart"))) }
}

@Composable
fun submitter(onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text("Login")
    }
}