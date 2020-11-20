package com.dropbox.kaiken.sample.basicscoping.app

import com.dropbox.kaiken.sample.basicscoping.helloworldfeatue.HelloWorldDependencies
import com.dropbox.kaiken.sample.basicscoping.helloworldfeatue.HelloWorldMessageProvider
import com.dropbox.kaiken.sample.basicscoping.helloworldfeatue.RealTimeMessageProvider
import com.dropbox.kaiken.sample.basicscoping.helloworldfeatue.RealWorldMessageProvider
import com.dropbox.kaiken.sample.basicscoping.helloworldfeatue.TimeMessageProvider
import com.dropbox.kaiken.scoping.AppServices
import com.dropbox.kaiken.scoping.TeardownHelper
import com.dropbox.kaiken.scoping.UserServices

class BasicKaikenSampleAppServices : AppServices, HelloWorldDependencies {

    override val helloWorldMessageProvider: HelloWorldMessageProvider
        = RealWorldMessageProvider("Hello world!")

    override val timeMessageProvider: TimeMessageProvider
        = RealTimeMessageProvider()

    override fun getTeardownHelper(): TeardownHelper = NOOP_TEARDOWNHELPER
}

class BasicKaikenSampleUserServices(
    userProfile: UserProfile,
    appServices: BasicKaikenSampleAppServices
) : UserServices, HelloWorldDependencies {

    override val helloWorldMessageProvider: HelloWorldMessageProvider
        = RealWorldMessageProvider("Hello ${userProfile.name}!")

    override val timeMessageProvider: TimeMessageProvider
        = appServices.timeMessageProvider

    override fun getTeardownHelper(): TeardownHelper = NOOP_TEARDOWNHELPER
}


// You can implement your own teardown logic here.
private val NOOP_TEARDOWNHELPER = object: TeardownHelper {
    override fun teardown() {
        // No op
    }
}
