package com.dropbox.kaiken.skeleton.initializers

import com.dropbox.common.inject.UserScope
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import com.squareup.anvil.annotations.ContributesMultibinding
import com.squareup.anvil.annotations.ContributesTo
import javax.inject.Inject

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

// Dagger expects at least one item in the Multibinding, so add this and we'll call it.
@ContributesMultibinding(UserScope::class)
@SingleIn(UserScope::class)
class NoOpUserServicesInitializer @Inject constructor() : UserServicesInitializer{
    override fun initUser(userId: String) {
        // no op
    }

    override fun userTeardown(userId: String) {
        //no op
    }
}