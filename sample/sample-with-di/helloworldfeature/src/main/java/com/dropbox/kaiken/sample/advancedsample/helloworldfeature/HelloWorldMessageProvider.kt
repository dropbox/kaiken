package com.dropbox.kaiken.sample.advancedsample.helloworldfeature

interface HelloWorldMessageProvider {
    fun sayHello(): String
}

class RealWorldMessageProvider(private val message: String) : HelloWorldMessageProvider {
    override fun sayHello() = message
}
