package com.dropbox.kaiken.skeleton.initializers

import com.dropbox.common.inject.AppScope
import com.squareup.anvil.annotations.ContributesTo
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
     * Called when app services is initialized
     */
    fun init()

    /**
     * Called when the app is torn down
     */
    fun teardown()
}

@ContributesTo(AppScope::class)
interface AppInitializerProvider {
    val appServicesInitializers: Set<AppServicesInitializer>
}

// Dagger expects at least one contributor, so we give it an empty set.
@Module
@ContributesTo(AppScope::class)
class AppServicesInitializerModule {
    @Provides
    @ElementsIntoSet
    fun primeEmptyAppInits() = emptySet<AppServicesInitializer>()
}
