package com.dropbox.kaiken.skeleton.core

import com.dropbox.kaiken.scoping.AppServices
import com.dropbox.kaiken.skeleton.scoping.AppScope
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import com.dropbox.kaiken.skeleton.usermanagement.UserManager
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesTo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import javax.inject.Inject

/**
 * Holds the common dependencies for [AAppScope]
 * The classes defined in this interface will be available in all Skeleton apps components that merge with [AAppScope]
 */
@ContributesTo(AppScope::class)
interface KaikenAppServices : AppServices {
    fun userStore(): UserStore<out User>
}

@ContributesTo(AppScope::class)
interface CoroutineScopeBindings {
    fun coroutineScopes(): CoroutineScopes
}

@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)
class RealCoroutineScopes @Inject constructor() : CoroutineScopes {
    override val mainScope: CoroutineScope
        get() = MainScope()
    override val globalScope: CoroutineScope
        get() = GlobalScope
}

interface CoroutineScopes {
    val mainScope: CoroutineScope
    val globalScope: CoroutineScope
}
