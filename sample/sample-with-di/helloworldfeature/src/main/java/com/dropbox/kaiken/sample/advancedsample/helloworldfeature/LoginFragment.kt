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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.AndroidUiDispatcher.Companion.Main
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dropbox.kaiken.Injector
import com.dropbox.kaiken.runtime.InjectorFactory
import com.dropbox.kaiken.runtime.InjectorViewModel
import com.dropbox.kaiken.skeleton.scoping.AuthAwareInjectorHolder
import com.dropbox.kaiken.skeleton.scoping.AuthOptionalActivityScope
import com.dropbox.kaiken.skeleton.scoping.authOptionalInjectorFactory
import com.dropbox.kaiken.skeleton.usermanagement.UserManager
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
    fun presenter(): LoginPresenter
}


class LoginFragment : AuthAwareInjectorHolder<HellowWorldManualInjector>() {
    override fun getInjectorFactory(): InjectorFactory<HellowWorldManualInjector> =
        authOptionalInjectorFactory()


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
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        val injectorFactory: InjectorFactory<HellowWorldManualInjector> =
                            authOptionalInjectorFactory()

                        val viewModelProvider =
                            ViewModelProvider(it, InjectorViewModelFactory(injectorFactory))

                        val injector =
                            viewModelProvider.get(InjectorViewModel::class.java).injector as HellowWorldManualInjector
                        val presenter: LoginPresenter = injector.presenter()

                        val model: LoginModel = presenter.present(events = eventFlow)
                        Screen(model,eventFlow )
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

    @Composable
    fun Screen(model: LoginModel, events: MutableStateFlow<LoginEvent>) {
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

sealed interface LoginEvent
object LoginLaunched : LoginEvent
data class Submit(val userInput: UserInput) : LoginEvent

sealed interface LoginModel
data class LoginSuccess(val userId: String) : LoginModel
object LoginNeeded : LoginModel


interface LoginPresenter : MoleculePresenter<LoginEvent, LoginModel>

@ContributesBinding(AuthOptionalActivityScope::class)
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


internal class InjectorViewModelFactory<InjectorType : Injector>(
    private val injectorFactory: InjectorFactory<InjectorType>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return InjectorViewModel(injectorFactory.createInjector()) as T
    }
}
