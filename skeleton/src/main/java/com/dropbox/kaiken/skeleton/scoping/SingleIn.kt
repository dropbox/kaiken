package com.dropbox.kaiken.skeleton.scoping

import androidx.fragment.app.Fragment
import com.dropbox.kaiken.Injector
import com.dropbox.kaiken.runtime.InjectorFactory
import com.dropbox.kaiken.runtime.InjectorHolder
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
abstract class AuthRequiredActivityScope private constructor()
abstract class AuthOptionalActivityScope private constructor()

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
    scope = AuthRequiredActivityScope::class,
    parentScope = UserScope::class
)
@SingleIn(AuthRequiredActivityScope::class)
interface AuthRequiredActivityComponent {

    @ContributesTo(UserScope::class)
    interface ParentComponent {
        @OptIn(ExperimentalAnvilApi::class)

        fun createAuthRequiredComponent(): AuthRequiredActivityComponent
    }
}

@OptIn(ExperimentalAnvilApi::class)
@ContributesSubcomponent(
    scope = AuthOptionalActivityScope::class,
    parentScope = AppScope::class
)
@SingleIn(AuthOptionalActivityScope::class)
interface AuthOptionalActivityComponent : Injector {
    @ContributesTo(AppScope::class)
    interface ParentComponent {
        fun createAuthOptionalComponent(): AuthOptionalActivityComponent
    }
}

inline fun  <reified T:Injector> DependencyProviderResolver.authOptionalInjectorFactory() =
    InjectorFactory { (resolveDependencyProvider() as AuthOptionalActivityComponent.ParentComponent).createAuthOptionalComponent() as T }

inline fun  <reified T:Injector> DependencyProviderResolver.authInjector() =
    InjectorFactory { (resolveDependencyProvider() as AuthRequiredActivityComponent.ParentComponent).createAuthRequiredComponent() as T }

abstract class AuthAwareInjectorHolder<T:Injector>: Fragment(), AuthAwareFragment, InjectorHolder<T>

abstract class AuthOptionalInjectorHolder<T:Injector>: Fragment(), AuthOptionalFragment, InjectorHolder<T>

abstract class AuthRequiredInjectorHolder<T:Injector>: Fragment(), AuthRequiredFragment, InjectorHolder<T>
