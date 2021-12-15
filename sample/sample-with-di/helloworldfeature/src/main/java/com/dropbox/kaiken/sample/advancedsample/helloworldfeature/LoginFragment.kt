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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.AndroidUiDispatcher.Companion.Main
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import app.cash.molecule.launchMolecule
import com.dropbox.kaiken.Injector
import com.dropbox.kaiken.runtime.InjectorFactory
import com.dropbox.kaiken.skeleton.scoping.AuthAwareInjectorHolder
import com.dropbox.kaiken.skeleton.scoping.AuthOptionalActivityScope
import com.dropbox.kaiken.skeleton.scoping.authOptionalInjector
import com.dropbox.kaiken.skeleton.usermanagement.UserManager
import com.dropbox.kaiken.skeleton.usermanagement.auth.UserInput
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesTo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@ContributesTo(AuthOptionalActivityScope::class)
interface HellowWorldManualInjector : Injector {
    fun inject(fragment: LoginFragment)
}


class LoginFragment : AuthAwareInjectorHolder<HellowWorldManualInjector>() {
    override fun getInjectorFactory(): InjectorFactory<HellowWorldManualInjector> =
        authOptionalInjector()

    @Inject
    lateinit var loginPresenter: LoginPresenter

    @Inject
    lateinit var helloWorldMessageProvider: HelloWorldMessageProvider

    @Inject
    lateinit var timeMessageProvider: TimeMessageProvider

    @Inject
    lateinit var intentFactory: @JvmSuppressWildcards (Context, String) -> Intent

    @Inject
    lateinit var userManager: UserManager

    @Inject
    lateinit var userFlow: @JvmSuppressWildcards MutableSharedFlow<UserInput>
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
        val eventFlow = MutableStateFlow<LoginEvent>(LoginLaunched)
        //once we are started we want to launch our presenter.
        //the presenter present scope will be tied to the fragment's lifecycle
        val models = scope.launchMolecule {
            loginPresenter.present(events = eventFlow.asStateFlow())
        }
        //create a compose view
        return ComposeView(requireContext()).apply {
            // Dispose of the Composition when the view's LifecycleOwner
            // is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    Column {
                        val presenterState = models.collectAsState().value
                        when (presenterState) {
                            is LoginNeeded -> {
                                // In Compose world
                                Text("Enter User ID")
                                var text by remember { mutableStateOf("1") }
                                TextField(
                                    value = text,
                                    onValueChange = { text = it },
                                    label = { Text("Label") }
                                )
                                submitter { eventFlow.tryEmit(Submit(UserInput(text, "Bart"))) }
                            }
                            is LoginSuccess -> LaunchedEffect(key1 = Unit) {
                                startActivity(
                                    intentFactory(
                                        context,
                                        presenterState.userId
                                    )
                                )
                            }
                        }
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

@ContributesBinding(LoginPresenter::class)
class RealLoginPresenter(val userFlow: MutableSharedFlow<UserInput>) :
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