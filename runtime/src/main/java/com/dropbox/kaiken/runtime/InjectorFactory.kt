package com.dropbox.kaiken.runtime

import com.dropbox.kaiken.Injector

/**
 * Factory for an injector.
 *
 * In the Dagger world this would be implemented as :
 *
 * ```
 * class MyInjectorFactory : InjectorFactory {
 *    override fun createInjector(): Injector {
 *       return DaggerMyComponents.builder().myDependencies(myDependencies).build()
 *    }
 * }
 *
 * ```
 */
fun interface InjectorFactory<InjectorType : Injector> {
    fun createInjector(): InjectorType
}

/**
 * Provides an `InjectorFactory`. This usually will be implemented by activities.
 */
fun interface InjectorFactoryProvider<InjectorType : Injector> {
    fun getInjectorFactory(): InjectorFactory<InjectorType>
}
