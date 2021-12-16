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
import androidx.compose.ui.platform.AndroidUiDispatcher.Companion.Main
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dropbox.kaiken.Injector
import com.dropbox.kaiken.runtime.InjectorFactory
import com.dropbox.kaiken.runtime.InjectorViewModel
import com.dropbox.kaiken.skeleton.scoping.AuthAwareInjectorHolder
import com.dropbox.kaiken.skeleton.scoping.AuthOptionalActivityScope
import com.dropbox.kaiken.skeleton.scoping.AuthOptionalScreenScope
import com.dropbox.kaiken.skeleton.scoping.InjectorViewModelFactory
import com.dropbox.kaiken.skeleton.scoping.authOptionalInjectorFactory
import com.dropbox.kaiken.skeleton.scoping.cast
import com.dropbox.kaiken.skeleton.scoping.authOptionalScreenComponent
import com.dropbox.kaiken.skeleton.usermanagement.auth.UserInput
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesTo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@ContributesTo(AuthOptionalActivityScope::class)
interface HellowWorldManualInjector : Injector {
    fun inject(fragment: LoginFragment)
}

@ContributesTo(AuthOptionalScreenScope::class)
interface PresenterProvider : Injector {
    fun presenter(): LoginPresenter
}

val LocalComponent = staticCompositionLocalOf<Injector> {
    noLocalProvidedFor("LocalSavedStateRegistryOwner")
}

private fun noLocalProvidedFor(name: String): Nothing {
    error("CompositionLocal $name not present")
}


class LoginFragment : AuthAwareInjectorHolder<HellowWorldManualInjector>() {
    override fun getInjectorFactory(): InjectorFactory<HellowWorldManualInjector> =
        authOptionalInjectorFactory()

    @Inject
    lateinit var intentFactory: @JvmSuppressWildcards (Context, String) -> Intent


    private val scope = CoroutineScope(Main)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (finishIfInvalidAuth()) return
        locateInjector().inject(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        scope.cancel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        //create a single stream for event callbacks between fragment and presenter
        return ComposeView(requireContext()).apply {
            LocalContext
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "login") {
                    composable("login") { backstackEntry ->
                        val injector = backstackEntry.retain { authOptionalScreenComponent() }
                        CompositionLocalProvider(LocalComponent provides injector) {
                            Screen()
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun Screen() {
        val events = MutableStateFlow<LoginEvent>(LoginLaunched)
        val model = LocalComponent.current.cast<PresenterProvider>().presenter().present(events)
        MaterialTheme {
            Column {
                when (model) {
                    is LoginNeeded -> {
                        // In Compose world
                        Text("Enter User ID")
                        var text by remember { mutableStateOf("1") }
                        TextField(
                            value = text,
                            onValueChange = { text = it },
                            label = { Text("Label") }
                        )
                        submitter { events.tryEmit(Submit(UserInput(text, "Bart"))) }
                    }
                    is LoginSuccess -> LaunchedEffect(key1 = Unit) {
                        startActivity(
                            intentFactory(
                                requireActivity(),
                                model.userId
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun submitter(onClick: () -> Unit) {
    Button(
        onClick = onClick,
    ) {
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
    @Composable
    override fun present(events: Flow<LoginEvent>): LoginModel {
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
    fun present(events: Flow<Event>): Model
}



@Composable
private inline fun <reified T : Injector> ViewModelStoreOwner.retain(
    injectorFactory: InjectorFactory<T>
): T {
    val viewModelProvider = ViewModelProvider(this, InjectorViewModelFactory(injectorFactory))
    return viewModelProvider.get(InjectorViewModel::class.java).injector as T
}