package com.dropbox.kaiken.sample.advancedsample.helloworldfeature

interface HelloWorldDependencies {
    val helloWorldMessageProvider: HelloWorldMessageProvider
    val timeMessageProvider: TimeMessageProvider
}
