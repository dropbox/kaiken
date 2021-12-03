package com.dropbox.kaiken.sample.advancedsample.helloworldfeature

import android.content.Context
import android.content.Intent
import com.dropbox.kaiken.scoping.AppScope
import com.dropbox.kaiken.scoping.UserScope
import com.dropbox.kaiken.skeleton.skeleton.usermanagement.UserManager
import com.squareup.anvil.annotations.ContributesTo
import kotlinx.coroutines.flow.MutableSharedFlow

@ContributesTo(AppScope::class)
interface HelloWorldDependencies {
    val helloWorldMessageProvider: HelloWorldMessageProvider
    val timeMessageProvider: TimeMessageProvider
    val injectorFactory:@JvmSuppressWildcards (Context, String) -> Intent
    val userManager:UserManager
    val userFlow: @JvmSuppressWildcards MutableSharedFlow<String>
}

@ContributesTo(UserScope::class)
interface HelloWorldUserDependencies {
    val helloWorldMessageProvider: HelloWorldMessageProviderUser
    val timeMessageProvider: TimeMessageProvider
    val injectorFactory:@JvmSuppressWildcards (Context, String) -> Intent}
