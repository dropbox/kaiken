package com.dropbox.kaiken.skeleton.scoping

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dropbox.kaiken.Injector
import com.dropbox.kaiken.runtime.InjectorFactory
import com.dropbox.kaiken.runtime.InjectorHolder
import com.dropbox.kaiken.runtime.InjectorViewModel
import com.dropbox.kaiken.scoping.AppServices
import com.dropbox.kaiken.scoping.AuthAwareFragment
import com.dropbox.kaiken.scoping.AuthOptionalFragment
import com.dropbox.kaiken.scoping.AuthRequiredFragment
import com.dropbox.kaiken.scoping.DependencyProviderResolver
import com.dropbox.kaiken.scoping.UserServices
import com.squareup.anvil.annotations.ContributesSubcomponent
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.ExperimentalAnvilApi
import dagger.BindsInstance
import javax.inject.Scope
import kotlin.reflect.KClass

@OptIn(ExperimentalAnvilApi::class)

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class SingleIn(val clazz: KClass<*>)

abstract class SkeletonScope private constructor()
abstract class AppScope private constructor()
abstract class UserScope private constructor()
abstract class AuthRequiredScope private constructor()
abstract class AuthOptionalScope private constructor()
abstract class AuthOptionalScreenScope private constructor()
abstract class AuthRequiredScreenScope private constructor()

inline fun <reified T> Any.cast(): T = this as T

@ContributesSubcomponent(
    scope = AppScope::class,
    parentScope = SkeletonScope::class
)
@SingleIn(AppScope::class)
@OptIn(ExperimentalAnvilApi::class)
interface AppComponent : AppServices {
    @OptIn(ExperimentalAnvilApi::class)
    @ContributesTo(SkeletonScope::class)
    interface AppParentComponent {
        fun appComponent(): AppComponent
    }
}

@OptIn(ExperimentalAnvilApi::class)
@ContributesSubcomponent(
    scope = UserScope::class,
    parentScope = AppScope::class
)
@SingleIn(UserScope::class)
interface UserComponent : UserServices {
    @OptIn(ExperimentalAnvilApi::class)
    @ContributesSubcomponent.Factory
    interface Factory {
        fun userComponent(
            @BindsInstance userId: Int
        ): UserComponent
    }
}

@ContributesTo(AppScope::class)
interface UserParentComponent {
    @OptIn(ExperimentalAnvilApi::class)
    fun createUserComponent(): UserComponent.Factory
}

@OptIn(ExperimentalAnvilApi::class)
@ContributesSubcomponent(
    scope = AuthRequiredScope::class,
    parentScope = UserScope::class
)
@SingleIn(AuthRequiredScope::class)
interface AuthRequiredComponent {

    @ContributesTo(UserScope::class)
    interface ParentComponent {
        @OptIn(ExperimentalAnvilApi::class)

        fun createAuthRequiredComponent(): AuthRequiredComponent
    }
}

@OptIn(ExperimentalAnvilApi::class)
@ContributesSubcomponent(
    scope = AuthOptionalScope::class,
    parentScope = AppScope::class
)
@SingleIn(AuthOptionalScope::class)
interface AuthOptionalComponent : Injector {
    @ContributesTo(AppScope::class)
    interface ParentComponent {
        fun createAuthOptionalComponent(): AuthOptionalComponent
    }
}


@OptIn(ExperimentalAnvilApi::class)
@ContributesSubcomponent(
    scope = AuthOptionalScreenScope::class,
    parentScope = AppScope::class
)
@SingleIn(AuthOptionalScreenScope::class)
interface AuthOptionalScreenComponent : Injector {
    @ContributesTo(AppScope::class)
    interface ScreenParentComponent:Injector {
        fun createAuthOptionalScreenComponent(): AuthOptionalScreenComponent
    }
}

@OptIn(ExperimentalAnvilApi::class)
@ContributesSubcomponent(
    scope =AuthRequiredScreenScope::class,
    parentScope = UserScope::class
)
@SingleIn(AuthRequiredScreenScope::class)
interface AuthRequiredScreenComponent : Injector {
    @ContributesTo(UserScope::class)
    interface ScreenParentComponent:Injector {
        fun createAuthRequiredScreenComponent(): AuthRequiredScreenComponent
    }
}


inline fun <reified T : Injector> DependencyProviderResolver.authOptionalInjectorFactory() =
    InjectorFactory { (resolveDependencyProvider() as AuthOptionalComponent.ParentComponent).createAuthOptionalComponent() as T }

inline fun <reified T : Injector> DependencyProviderResolver.authInjector() =
    InjectorFactory { (resolveDependencyProvider() as AuthRequiredComponent.ParentComponent).createAuthRequiredComponent() as T }

abstract class AuthAwareInjectorHolder<T : Injector> : Fragment(), AuthAwareFragment,
    InjectorHolder<T>

abstract class AuthOptionalInjectorHolder<T : Injector> : Fragment(), AuthOptionalFragment,
    InjectorHolder<T>

abstract class AuthRequiredInjectorHolder<T : Injector> : Fragment(), AuthRequiredFragment,
    InjectorHolder<T>




class InjectorViewModelFactory<InjectorType : Injector>(
    private val injectorFactory: InjectorFactory<InjectorType>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return InjectorViewModel(injectorFactory.createInjector()) as T
    }
}

