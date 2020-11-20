package com.dropbox.kaiken.sample.basicscoping.helloworldfeatue

interface HelloWorldDependencies {
    val helloWorldMessageProvider: HelloWorldMessageProvider
    val timeMessageProvider: TimeMessageProvider
}
