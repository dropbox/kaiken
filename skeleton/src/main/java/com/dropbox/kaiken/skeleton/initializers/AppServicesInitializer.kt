package com.dropbox.kaiken.skeleton.initializers

import com.dropbox.common.inject.AppScope
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import com.squareup.anvil.annotations.ContributesMultibinding
import com.squareup.anvil.annotations.ContributesTo
import javax.inject.Inject

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

@ContributesTo(AppScope::class)
interface AppInitializerProvider {
    val appServicesInitializers: Set<AppServicesInitializer>
}

// Dagger expects at least one item in the Multibinding, so add this and we'll call it.
@ContributesMultibinding(AppScope::class)
@SingleIn(AppScope::class)
class NoOpAppServicesInitializer @Inject constructor() : AppServicesInitializer{
    override fun init() {
        // no op
    }
}