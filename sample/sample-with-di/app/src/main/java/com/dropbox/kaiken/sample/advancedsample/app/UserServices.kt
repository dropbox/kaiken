package com.dropbox.kaiken.sample.advancedsample.app

import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.HelloWorldDependencies
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.HelloWorldMessageProvider
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.HelloWorldMessageProviderUser
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.RealWorldMessageProvider
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.RealWorldMessageProviderUser
import com.dropbox.kaiken.sample.advancedsample.helloworldfeature.TimeMessageProvider
import com.dropbox.kaiken.scoping.AppScope
import com.dropbox.kaiken.scoping.SingleIn
import com.dropbox.kaiken.scoping.TeardownHelper
import com.dropbox.kaiken.scoping.UserScope
import com.dropbox.kaiken.scoping.UserServices
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.MergeComponent
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides

// App Services that are need to instance user services
@ContributesTo(AppScope::class)
interface CoreAppServices {
    val timeMessageProvider: TimeMessageProvider
}

@ContributesTo(UserScope::class)
@Module
object UserServicesModule {


    @Provides
    @SingleIn(UserScope::class)
    fun provideHelloWorldMessageProvider(userProfile: UserProfile): HelloWorldMessageProviderUser =
        RealWorldMessageProviderUser("Hello ${userProfile.name}!")
}

//// You can implement your own teardown logic here.
//@ContributesBinding(UserScope::class)
//class AnotherNoOpTeardownHelper : TeardownHelper {
//    override fun teardown() {
//        // No op
//    }
//}
