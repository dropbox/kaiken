package com.dropbox.kaiken.sample.advancedsample.helloworldfeature

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dropbox.kaiken.Injector
import com.dropbox.kaiken.runtime.InjectorFactory
import com.dropbox.kaiken.runtime.InjectorHolder
import com.dropbox.kaiken.runtime.InjectorViewModel
import com.dropbox.kaiken.scoping.DependencyProviderResolver
import com.dropbox.kaiken.skeleton.scoping.AuthAwareInjectorHolder
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

@Composable
inline fun <reified T : AuthOptionalScreenComponent.ScreenParentComponent> AuthAwareInjectorHolder<T>.AuthOptionalLocalProvider(
    viewModelStoreOwner: ViewModelStoreOwner,
    crossinline content: @Composable () -> Unit
) {
    val component = viewModelStoreOwner.retain { authOptionalScreenComponent() }
    CompositionLocalProvider(LocalComponent provides component) {
        content()
    }
}

inline fun <reified T : AuthOptionalScreenComponent.ScreenParentComponent> AuthAwareInjectorHolder<T>.authOptionalScreenComponent(): AuthOptionalScreenComponent =
    locateInjector().cast<AuthOptionalScreenComponent.ScreenParentComponent>()
        .createAuthOptionalScreenComponent()

@Composable
inline fun <reified T : AuthRequiredScreenComponent.ScreenParentComponent> InjectorHolder<T>.AuthRequiredLocalProvider(
    viewModelStoreOwner: ViewModelStoreOwner,
    crossinline content: @Composable () -> Unit
) {
    val retain = viewModelStoreOwner.retain { authRequiredScreenComponent() }
    CompositionLocalProvider(LocalComponent provides retain) {
        content()
    }
}


inline fun <reified T : AuthRequiredScreenComponent.ScreenParentComponent> InjectorHolder<T>.authRequiredScreenComponent(): AuthRequiredScreenComponent =
    locateInjector().cast<AuthRequiredScreenComponent.ScreenParentComponent>()
        .authRequiredScreenComponentInner()


fun AuthRequiredScreenComponent.ScreenParentComponent.authRequiredScreenComponentInner(): AuthRequiredScreenComponent =
    this.createAuthRequiredScreenComponent()

fun AuthOptionalScreenComponent.ScreenParentComponent.authOptionalScreenComponentInner(): AuthOptionalScreenComponent =
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
                "you need to wrap your composable in " +
                "AuthOptionalLocalProvider or AuthRequiredLocalProvider"
    )
}


inline fun <reified T : Presenter<*, *>> NavGraphBuilder.authRequiredComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    crossinline content: @Composable AuthRequiredScreenComponent.(NavBackStackEntry, T) -> Unit,
) {

    val kaikenAwareContent: @Composable (NavBackStackEntry) -> Unit = { entry ->
        val injector: AuthRequiredScreenComponent.ScreenParentComponent =
            LocalDResolver.current.resolveDependencyProvider()

        val retained = entry.retain { injector.authRequiredScreenComponentInner() }
        CompositionLocalProvider(LocalComponent provides retained) {
            val presenter: T =
                retained.cast<Presenter.PresenterProvider>().presenters().filterIsInstance<T>()
                    .first()
            LaunchedEffect(presenter) { presenter.run() }
            retained.content(entry, presenter)
        }
    }
    composable(route, arguments, deepLinks, kaikenAwareContent)
}

inline fun <reified T : Presenter<*, *>> NavGraphBuilder.authOptionalComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    crossinline content: @Composable AuthOptionalScreenComponent.(NavBackStackEntry, T) -> Unit,
) {
    val kaikenAwareContent: @Composable (NavBackStackEntry) -> Unit = { entry ->
        val injector: AuthOptionalScreenComponent.ScreenParentComponent =
            LocalDResolver.current.resolveDependencyProvider()

        val retained = entry.retain { injector.authOptionalScreenComponentInner() }
        CompositionLocalProvider(LocalComponent provides retained) {
            val presenter: T =
                retained.cast<Presenter.PresenterProvider>().presenters().filterIsInstance<T>()
                    .first()
            LaunchedEffect(presenter) { presenter.run() }
            retained.content(entry, presenter)
        }
    }
    composable(route, arguments, deepLinks, kaikenAwareContent)
}

abstract class Presenter<Event, Model>(
    initialState: Model,
) {
    val model: MutableState<Model> = mutableStateOf(initialState)

   abstract val actionHandler: suspend (value: Event) -> Model

    val events: MutableSharedFlow<Event> =
        MutableSharedFlow(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    suspend fun run() {
        events.collect {
            model.value = actionHandler(it)
        }
    }

    @ContributesTo(AuthOptionalScreenScope::class)
    interface PresenterProvider : Injector {
        fun presenters(): Set<Presenter<*, *>>
    }
}

fun AuthAwareFragment2.setContent(content: @Composable () -> Unit): ComposeView {
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
