package com.dropbox.kaiken.sample.advancedsample.helloworldfeature

import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppScope::class)
interface HelloWorldDependencies {
    val helloWorldMessageProvider: HelloWorldMessageProvider
    val timeMessageProvider: TimeMessageProvider
}
