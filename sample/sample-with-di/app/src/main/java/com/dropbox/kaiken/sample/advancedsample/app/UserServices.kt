package com.dropbox.kaiken.sample.advancedsample.app

import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.HelloWorldDependencies
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.HelloWorldMessageProvider
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.RealWorldMessageProvider
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.TimeMessageProvider
import com.dropbox.kaiken.scoping.TeardownHelper
import com.dropbox.kaiken.scoping.UserServices
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides

annotation class UserScope

@Component(
    modules = [UserServicesModule::class],
    dependencies = [CoreAppServices::class]
)
@UserScope
abstract class AdvancedKaikenSampleUserServices : UserServices, HelloWorldDependencies {
    @Component.Factory
    interface Factory {
        fun create(
            appServices: CoreAppServices,
            @BindsInstance userProfile: UserProfile
        ): AdvancedKaikenSampleUserServices
    }

    override fun getTeardownHelper(): TeardownHelper = NOOP_TEARDOWNHELPER
}

// App Services that are need to instance user services
interface CoreAppServices {
    val timeMessageProvider: TimeMessageProvider
}

@Module
object UserServicesModule {
    @Provides
    @UserScope
    fun provideHelloWorldMessageProvider(userProfile: UserProfile): HelloWorldMessageProvider =
        RealWorldMessageProvider("Hello ${userProfile.name}!")
}

// You can implement your own teardown logic here.
private val NOOP_TEARDOWNHELPER = object : TeardownHelper {
    override fun teardown() {
        // No op
    }
}
