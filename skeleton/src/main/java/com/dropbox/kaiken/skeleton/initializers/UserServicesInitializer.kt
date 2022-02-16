package com.dropbox.kaiken.skeleton.initializers

import com.dropbox.common.inject.UserScope
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dagger.multibindings.ElementsIntoSet

/**
 * Interface to use when you have a component which you want to be called
 * when a user services is created.  Be mindful this will be run on the main thread.
 *
 * @ContributesMultibinding(SdkScope::class)
 * @SingleIn(SdkScope::class)
 * class FooInitializer @Inject constructor(
 *     private val foo: Foo,
 *     private val bar: Bar,
 * ): UserServicesInitializer {
 *    override fun init() {
 *       foo.setup(bar)
 *    }
 * }
 *
 */
interface UserServicesInitializer {
    /**
     * Called when the user is added into [UserServices]
     */
    fun initUser(userId: String)

    /**
     * Called when the user is removed from [UserServices]
     */
    fun userTeardown(userId: String)
}

@ContributesTo(UserScope::class)
interface UserServicesInitializerProvider {
    val userServicesInitializers: Set<UserServicesInitializer>
}

// Dagger expects at least one contributor, so we give it an empty set.
@Module
@ContributesTo(UserScope::class)
class UserServicesInitializerModule {
    @Provides
    @ElementsIntoSet
    fun primeEmptyUserInits() = emptySet<UserServicesInitializer>()
}
