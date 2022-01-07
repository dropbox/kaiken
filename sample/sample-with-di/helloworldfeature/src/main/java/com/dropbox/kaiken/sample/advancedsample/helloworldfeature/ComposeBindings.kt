package com.dropbox.kaiken.sample.advancedsample.helloworldfeature

import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dropbox.kaiken.Injector
import com.dropbox.kaiken.runtime.InjectorFactory
import com.dropbox.kaiken.runtime.InjectorViewModel
import com.dropbox.kaiken.scoping.AuthAwareFragment
import com.dropbox.kaiken.scoping.AuthOptionalActivity
import com.dropbox.kaiken.scoping.AuthOptionalFragment
import com.dropbox.kaiken.scoping.AuthRequiredActivity
import com.dropbox.kaiken.scoping.AuthRequiredFragment
import com.dropbox.kaiken.scoping.DependencyProviderResolver
import com.dropbox.kaiken.skeleton.scoping.AuthOptionalScreenComponent
import com.dropbox.kaiken.skeleton.scoping.AuthOptionalScreenScope
import com.dropbox.kaiken.skeleton.scoping.AuthRequiredScreenComponent
import com.dropbox.kaiken.skeleton.scoping.InjectorViewModelFactory
import com.dropbox.kaiken.skeleton.scoping.cast
import com.squareup.anvil.annotations.ContributesTo
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect

@Composable
inline fun <reified T : Injector> ViewModelStoreOwner.retain(
    injectorFactory: InjectorFactory<T>
): T {
    val viewModelProvider = ViewModelProvider(this, InjectorViewModelFactory(injectorFactory))
    return viewModelProvider.get(InjectorViewModel::class.java).injector as T
}


fun AuthRequiredScreenComponent.ScreenParentComponent.authRequiredScreenComponent(): AuthRequiredScreenComponent =
    this.createAuthRequiredScreenComponent()

fun AuthOptionalScreenComponent.ScreenParentComponent.authOptionalScreenComponent(): AuthOptionalScreenComponent =
    this.createAuthOptionalScreenComponent()

val LocalComponent = staticCompositionLocalOf<Injector> {
    error(
        "CompositionLocal Component not present, " +
                "you need to wrap your composable in " +
                "AuthOptionalLocalProvider or AuthRequiredLocalProvider"
    )
}

val LocalDResolver = staticCompositionLocalOf<DependencyProviderResolver> {
    error(
        "CompositionLocal Component not present, " +
                "you need to call [AuthAwareFragment2.setContent] " +
                "or similar prior to creating the nav graph "
    )
}

/**
 * creates a new [AuthRequiredScreenComponent],
 * retains the component in a backstack entry so that callers get the same instance after rotation
 *
 * adds [content] Composable to NavGraph
 */
inline fun <reified T : BasePresenter> NavGraphBuilder.authRequiredComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    crossinline content: @Composable AuthRequiredScreenComponent.(NavBackStackEntry, T) -> Unit,
) {

    val kaikenAwareContent: @Composable (NavBackStackEntry) -> Unit = { entry ->
        val injector: AuthRequiredScreenComponent.ScreenParentComponent =
            LocalDResolver.current.resolveDependencyProvider()

        val retained = entry.retain { injector.authRequiredScreenComponent() }
        CompositionLocalProvider(LocalComponent provides retained) {
            val presenter: T =
                retained.cast<PresenterProvider>().presenters().filterIsInstance<T>()
                    .first()
            //if new entry after rotation, call run again
            LaunchedEffect(presenter,entry){
                presenter.cast<Presenter<*,*>>().start()
            }
            retained.content(entry, presenter)
        }
    }
    composable(route, arguments, deepLinks, kaikenAwareContent)
}

inline fun <reified T : BasePresenter> NavGraphBuilder.authOptionalComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    crossinline content: @Composable AuthOptionalScreenComponent.(NavBackStackEntry, T) -> Unit,
) {
    val kaikenAwareContent: @Composable (NavBackStackEntry) -> Unit = { entry ->
        val injector: AuthOptionalScreenComponent.ScreenParentComponent =
            LocalDResolver.current.resolveDependencyProvider()

        val retained = entry.retain { injector.authOptionalScreenComponent() }
        CompositionLocalProvider(LocalComponent provides retained) {
            val presenter: T =
                retained.cast<PresenterProvider>().presenters().filterIsInstance<T>()
                    .first()
            //if new entry after rotation, call run again
            LaunchedEffect(presenter,entry){
                presenter.cast<Presenter<*,*>>().start()
            }
            retained.content(entry, presenter)
        }
    }
    composable(route, arguments, deepLinks, kaikenAwareContent)
}

inline fun <reified T : BasePresenter> NavGraphBuilder.authAwareComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    crossinline content: @Composable Injector.(NavBackStackEntry, T) -> Unit,
) {
    val kaikenAwareContent: @Composable (NavBackStackEntry) -> Unit = { entry ->

        if (LocalContext.current is AuthRequiredActivity) {
            val injector: AuthRequiredScreenComponent.ScreenParentComponent =
                LocalDResolver.current.resolveDependencyProvider()
            val retained = entry.retain { injector.authRequiredScreenComponent() }
            CompositionLocalProvider(LocalComponent provides retained) {
                val presenter: T =
                    retained.cast<PresenterProvider>().presenters().filterIsInstance<T>()
                        .first()
                //if new entry after rotation, call run again
                LaunchedEffect(presenter,entry){
                    presenter.cast<Presenter<*,*>>().start()
                }
                retained.content(entry, presenter)
            }
        } else if (LocalContext.current is AuthOptionalActivity) {
            val injector: AuthOptionalScreenComponent.ScreenParentComponent =
                LocalDResolver.current.resolveDependencyProvider()

            val retained = entry.retain { injector.authOptionalScreenComponent() }
            CompositionLocalProvider(LocalComponent provides retained) {
                val presenter: T =
                    retained.cast<PresenterProvider>().presenters().filterIsInstance<T>()
                        .first()
                //if new entry after rotation, call run again
                LaunchedEffect(presenter,entry){
                    presenter.cast<Presenter<*,*>>().start()
                }
                retained.content(entry, presenter)
            }
        }
    }
    composable(route, arguments, deepLinks, kaikenAwareContent)
}


@ContributesTo(AuthOptionalScreenScope::class)
interface PresenterProvider : Injector {
    fun presenters(): Set<BasePresenter>
}

interface BasePresenter

fun AuthAwareComposeFragment.setContent(content: @Composable () -> Unit): ComposeView {
    val myThis = this
    return ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            CompositionLocalProvider(LocalDResolver provides myThis) {
                content()
            }
        }
    }
}

fun AuthRequiredComposeFragment.setContent(content: @Composable () -> Unit): ComposeView {
    val myThis = this
    return ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            CompositionLocalProvider(LocalDResolver provides myThis) {
                content()
            }
        }
    }
}

fun AuthOptionalComposeFragment.setContent(content: @Composable () -> Unit): ComposeView {
    val myThis = this
    return ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            CompositionLocalProvider(LocalDResolver provides myThis) {
                content()
            }
        }
    }
}


fun AuthOptionalComposeActivity.setAuthOptionalContent(content: @Composable () -> Unit) {
    setContent {
        CompositionLocalProvider(LocalDResolver provides this) {
            content()
        }
    }
}

fun AuthRequiredComposeActivity.setAuthRequiredContent(content: @Composable () -> Unit) {
    setContent {
        CompositionLocalProvider(LocalDResolver provides this) {
            content()
        }
    }
}

abstract class AuthAwareComposeFragment : AuthAwareFragment, Fragment()
abstract class AuthRequiredComposeFragment : AuthRequiredFragment, Fragment()
abstract class AuthOptionalComposeFragment : AuthOptionalFragment, Fragment()
abstract class AuthOptionalComposeActivity : AppCompatActivity(), AuthOptionalActivity
abstract class AuthRequiredComposeActivity : AppCompatActivity(), AuthRequiredActivity


abstract class Presenter<Event, Model>(
    initialState: Model,
) : BasePresenter {
    val model: MutableState<Model> = mutableStateOf(initialState)

    val events: MutableSharedFlow<Event> =
        MutableSharedFlow(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    suspend fun start() {
        events.collect {
            eventHandler(it)
        }
    }

    abstract suspend fun eventHandler(event:Event)

    @ContributesTo(AuthOptionalScreenScope::class)
    interface PresenterProvider : Injector {
        fun presenters(): Set<BasePresenter>
    }
}