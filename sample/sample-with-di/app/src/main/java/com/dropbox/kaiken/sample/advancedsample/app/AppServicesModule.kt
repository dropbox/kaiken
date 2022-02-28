package com.dropbox.kaiken.sample.advancedsample.app

import android.util.Log
import com.dropbox.common.inject.AppScope
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.HelloWorldMessageProvider
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.RealTimeMessageProvider
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.RealWorldMessageProvider
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.TimeMessageProvider
import com.dropbox.kaiken.skeleton.initializers.AppServicesInitializer
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import com.squareup.anvil.annotations.ContributesMultibinding
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
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
}

@ContributesMultibinding(AppScope::class)
class HelloWorldAppServicesInitializer @Inject constructor() : AppServicesInitializer {
    override fun init() {
        Log.d("TAG", "App Services initializing for $this")
    }
}
