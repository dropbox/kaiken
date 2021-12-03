package com.dropbox.kaiken.sample.advancedsample.app

import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.HelloWorldMessageProvider
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.RealTimeMessageProvider
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.RealWorldMessageProvider
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.TimeMessageProvider
import com.dropbox.kaiken.scoping.AppScope
import com.dropbox.kaiken.scoping.SingleIn
import com.dropbox.kaiken.scoping.TeardownHelper
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject


@Module
@ContributesTo(AppScope::class)
object AppServicesModule {
    @Provides
    @SingleIn(AppScope::class)
    fun provideHelloWorldMessageProvider(): HelloWorldMessageProvider =
        RealWorldMessageProvider("Hello world!")

    @Provides
    @SingleIn(AppScope::class)
    fun provideTimeMessageProvider(): TimeMessageProvider = RealTimeMessageProvider()

    @Provides
    @SingleIn(AppScope::class)
    fun provideUserFlow(): @JvmSuppressWildcards MutableSharedFlow<String> =  MutableSharedFlow(replay = 1)
}

// You can implement your own teardown logic here.
@ContributesBinding(AppScope::class)
class NoOpTeardownHelper @Inject constructor(): TeardownHelper {
    override fun teardown() {
        // No op
    }
}
