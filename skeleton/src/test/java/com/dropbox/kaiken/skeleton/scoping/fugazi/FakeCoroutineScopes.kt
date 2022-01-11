package com.dropbox.kaiken.skeleton.scoping.fugazi

import com.dropbox.kaiken.skeleton.core.CoroutineScopes
import com.dropbox.kaiken.skeleton.core.RealCoroutineScopes
import com.dropbox.kaiken.skeleton.scoping.AppScope
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import javax.inject.Inject

@OptIn(DelicateCoroutinesApi::class)
@ContributesBinding(AppScope::class, replaces = [RealCoroutineScopes::class])
class FakeCoroutineScopes @Inject constructor() : CoroutineScopes {
    override val mainScope: CoroutineScope
        get() = GlobalScope
    override val globalScope: CoroutineScope
        get() = GlobalScope
}
