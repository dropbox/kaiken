package com.dropbox.kaiken.sample.advancedsample.helloworldfeature

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.dropbox.kaiken.Injector
import com.dropbox.kaiken.runtime.InjectorFactory
import com.dropbox.kaiken.runtime.InjectorHolder
import com.dropbox.kaiken.runtime.InjectorViewModel
import com.dropbox.kaiken.skeleton.scoping.AuthAwareInjectorHolder
import com.dropbox.kaiken.skeleton.scoping.AuthOptionalActivityComponent
import com.dropbox.kaiken.skeleton.scoping.InjectorViewModelFactory
import com.dropbox.kaiken.skeleton.scoping.authOptionalScreenComponent
import com.dropbox.kaiken.skeleton.scoping.authRequiredScreenComponent

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
