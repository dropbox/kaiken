package com.dropbox.kaiken.sample.advancedsample.helloworldfeature

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dropbox.kaiken.Injector
import com.dropbox.kaiken.runtime.InjectorFactory
import com.dropbox.kaiken.runtime.InjectorHolder
import com.dropbox.kaiken.runtime.InjectorViewModel
import com.dropbox.kaiken.skeleton.scoping.AuthAwareInjectorHolder
import com.dropbox.kaiken.skeleton.scoping.AuthOptionalActivityComponent
import com.dropbox.kaiken.skeleton.scoping.AuthOptionalScreenScope
import com.dropbox.kaiken.skeleton.scoping.InjectorViewModelFactory
import com.dropbox.kaiken.skeleton.scoping.authOptionalInjectorFactory
import com.dropbox.kaiken.skeleton.scoping.authOptionalScreenComponent
import com.dropbox.kaiken.skeleton.scoping.authRequiredScreenComponent
import com.dropbox.kaiken.skeleton.scoping.cast
import com.dropbox.kaiken.skeleton.usermanagement.auth.UserInput
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesTo
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@ContributesTo(AuthOptionalScreenScope::class)
interface LoginScreenComponent : Injector {
    fun presenter(): LoginPresenter
    fun intentFactory(): @JvmSuppressWildcards (Context, String) -> Intent
}

class LoginFragment : AuthAwareInjectorHolder<AuthOptionalActivityComponent>() {
    override fun getInjectorFactory(): InjectorFactory<AuthOptionalActivityComponent> =
        authOptionalInjectorFactory()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "login") {
                    composable("login") { backstackEntry ->
                        AuthOptionalLocalProvider(backstackEntry) {
                            LoginScreen()
                        }
                    }
                }
            }
        }
    }

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
        LaunchedEffect(key1 = Unit) {
            startActivity(intentLauncher(requireActivity(), model.userId))
        }
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


sealed interface LoginEvent
object LoginLaunched : LoginEvent
data class Submit(val userInput: UserInput) : LoginEvent

sealed interface LoginModel
data class LoginSuccess(val userId: String) : LoginModel
object LoginNeeded : LoginModel


interface LoginPresenter : MoleculePresenter<LoginEvent, LoginModel>

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

interface MoleculePresenter<Event, Model> {
    @Composable
    fun present(): Model

    val events: MutableStateFlow<Event>
}


@Composable
inline fun <reified T : Injector> ViewModelStoreOwner.retain(
    injectorFactory: InjectorFactory<T>
): T {
    val viewModelProvider = ViewModelProvider(this, InjectorViewModelFactory(injectorFactory))
    return viewModelProvider.get(InjectorViewModel::class.java).injector as T
}

@Composable
inline fun <reified T : AuthOptionalActivityComponent> AuthAwareInjectorHolder<T>.AuthOptionalLocalProvider(
    viewModelStoreOwner: ViewModelStoreOwner,
    crossinline content: @Composable () -> Unit
) {
    val component = viewModelStoreOwner.retain { authOptionalScreenComponent() }
    CompositionLocalProvider(LocalComponent provides component) {
        content()
    }
}

@Composable
inline fun <reified T : Injector> InjectorHolder<T>.AuthRequiredLocalProvider(
    viewModelStoreOwner: ViewModelStoreOwner,
    crossinline content: @Composable () -> Unit
) {
    val retain = viewModelStoreOwner.retain { authRequiredScreenComponent() }
    CompositionLocalProvider(LocalComponent provides retain) {
        content()
    }
}

val LocalComponent = staticCompositionLocalOf<Injector> {
    error("CompositionLocal Component not present, " +
            "you need to wrap your composable in " +
            "AuthOptionalLocalProvider or AuthRequiredLocalProvider")
}
