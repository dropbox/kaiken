package com.dropbox.kaiken.sample.advancedsample.helloworldfeature

interface HelloWorldMessageProvider {
    fun sayHello(): String
}

class RealWorldMessageProvider(private val message: String) : HelloWorldMessageProvider {
    override fun sayHello() = message
}

interface HelloWorldMessageProviderUser {
    fun sayHello(): String
}

class RealWorldMessageProviderUser(private val message: String) : HelloWorldMessageProviderUser {
    override fun sayHello() = message
}
