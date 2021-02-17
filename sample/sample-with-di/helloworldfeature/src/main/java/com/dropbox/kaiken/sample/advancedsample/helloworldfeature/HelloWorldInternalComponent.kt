package com.dropbox.kaiken.sample.advancedsample.helloworldfeature

import com.dropbox.kaiken.runtime.InjectorFactory
import com.dropbox.kaiken.scoping.DependencyProviderResolver
import dagger.Component

@Component(
    dependencies = [HelloWorldDependencies::class]
)
abstract class HelloWorldInternalComponent : HelloWorldFragmentInjector {
    @Component.Factory
    interface Factory {
        fun create(
            dependencies: HelloWorldDependencies
        ): HelloWorldInternalComponent
    }
}
