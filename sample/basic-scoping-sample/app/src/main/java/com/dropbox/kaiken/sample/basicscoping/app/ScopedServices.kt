package com.dropbox.kaiken.sample.basicscoping.app

import com.dropbox.kaiken.sample.basicscoping.helloworldfeatue.HelloWorldDependencies
import com.dropbox.kaiken.sample.basicscoping.helloworldfeatue.HelloWorldMessageProvider
import com.dropbox.kaiken.sample.basicscoping.helloworldfeatue.RealTimeMessageProvider
import com.dropbox.kaiken.sample.basicscoping.helloworldfeatue.RealWorldMessageProvider
import com.dropbox.kaiken.sample.basicscoping.helloworldfeatue.TimeMessageProvider
import com.dropbox.kaiken.scoping.AppServices
import com.dropbox.kaiken.scoping.UserServices

class BasicKaikenSampleAppServices : AppServices, HelloWorldDependencies {

    override val helloWorldMessageProvider: HelloWorldMessageProvider =
        RealWorldMessageProvider("Hello world!")

    override val timeMessageProvider: TimeMessageProvider =
        RealTimeMessageProvider()
}

class BasicKaikenSampleUserServices(
    userProfile: UserProfile,
    appServices: BasicKaikenSampleAppServices
) : UserServices, HelloWorldDependencies {

    override val helloWorldMessageProvider: HelloWorldMessageProvider =
        RealWorldMessageProvider("Hello ${userProfile.name}!")

    override val timeMessageProvider: TimeMessageProvider =
        appServices.timeMessageProvider
}