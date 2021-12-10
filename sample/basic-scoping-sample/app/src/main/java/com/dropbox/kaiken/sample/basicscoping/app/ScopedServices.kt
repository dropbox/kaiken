package com.dropbox.kaiken.sample.basicscoping.app

import com.dropbox.kaiken.sample.basicscoping.helloworldfeatue.HelloWorldDependencies
import com.dropbox.kaiken.sample.basicscoping.helloworldfeatue.HelloWorldMessageProvider
import com.dropbox.kaiken.sample.basicscoping.helloworldfeatue.RealTimeMessageProvider
import com.dropbox.kaiken.sample.basicscoping.helloworldfeatue.RealWorldMessageProvider
import com.dropbox.kaiken.sample.basicscoping.helloworldfeatue.TimeMessageProvider
import com.dropbox.kaiken.scoping.AppServices
import com.dropbox.kaiken.scoping.AppTeardownHelper
import com.dropbox.kaiken.scoping.UserServices
import com.dropbox.kaiken.scoping.UserTeardownHelper

class BasicKaikenSampleAppServices : AppServices, HelloWorldDependencies {

    override val helloWorldMessageProvider: HelloWorldMessageProvider =
        RealWorldMessageProvider("Hello world!")

    override val timeMessageProvider: TimeMessageProvider =
        RealTimeMessageProvider()

    override fun getTeardownHelper(): AppTeardownHelper = NOOP_TEARDOWNHELPER
}

class BasicKaikenSampleUserServices(
    userProfile: UserProfile,
    appServices: BasicKaikenSampleAppServices
) : UserServices, HelloWorldDependencies {

    override fun getUserTeardownHelper(): UserTeardownHelper {
        return object : UserTeardownHelper {
            override fun teardown() {
            }
        }
    }

    override val helloWorldMessageProvider: HelloWorldMessageProvider =
        RealWorldMessageProvider("Hello ${userProfile.name}!")

    override val timeMessageProvider: TimeMessageProvider =
        appServices.timeMessageProvider
}

// You can implement your own teardown logic here.
private val NOOP_TEARDOWNHELPER = object : AppTeardownHelper {
    override fun teardown() {
        // No op
    }
}
