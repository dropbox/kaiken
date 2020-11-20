package com.dropbox.kaiken.sample.basicscoping.helloworldfeatue

interface HelloWorldMessageProvider {
    fun sayHello(): String
}

class RealWorldMessageProvider(private val message: String) : HelloWorldMessageProvider {
    override fun sayHello() = message
}
