package com.dropbox.kaiken.sample.advancedsample.app

import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.HelloWorldDependencies
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.HelloWorldMessageProvider
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.RealTimeMessageProvider
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.RealWorldMessageProvider
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.TimeMessageProvider
import com.dropbox.kaiken.scoping.AppServices
import com.dropbox.kaiken.scoping.TeardownHelper
import dagger.Component
import dagger.Module
import dagger.Provides

annotation class AppScope

@Component(
    modules = [AppServicesModule::class]
)
@AppScope
abstract class AdvancedKaikenSampleAppServices : AppServices, HelloWorldDependencies, CoreAppServices {

    @Component.Factory
    interface Factory {
        fun create(
        ): AdvancedKaikenSampleAppServices
    }

    override fun getTeardownHelper(): TeardownHelper = NOOP_TEARDOWNHELPER
}

@Module
object AppServicesModule {
    @Provides
    @AppScope
    fun provideHelloWorldMessageProvider(): HelloWorldMessageProvider
        = RealWorldMessageProvider("Hello world!")

    @Provides
    @AppScope
    fun provideTimeMessageProvider(): TimeMessageProvider = RealTimeMessageProvider()
}

// You can implement your own teardown logic here.
private val NOOP_TEARDOWNHELPER = object: TeardownHelper {
    override fun teardown() {
        // No op
    }
}
