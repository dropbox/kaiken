package com.dropbox.kaiken.skeleton.initializers

import com.dropbox.common.inject.AppScope
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.multibindings.ElementsIntoSet

/**
 * Interface to use when you have a component which you want to be called
 * immediately at app startup.  Be mindful anything done here will block the app from launching.
 *
 * @ContributesMultibinding(SdkScope::class)
 * @SingleIn(SdkScope::class)
 * class FooInitializer @Inject constructor(
 *     private val foo: Foo,
 *     private val bar: Bar,
 * ): AppInitializer {
 *    override fun init() {
 *       foo.setup(bar)
 *    }
 * }
 *
 */
interface AppServicesInitializer {
    /**
     * Called when the app is started
     */
    fun init()
}

@Component(modules = [AppServicesInitializerModule::class])
@SingleIn(AppScope::class)
interface AppInitializerProvider {
    val appServicesInitializers: Set<AppServicesInitializer>
}

@Module
class AppServicesInitializerModule {
    @Provides
    @ElementsIntoSet
    fun primeEmptyAppInits() = emptySet<AppServicesInitializer>()
}