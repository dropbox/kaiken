package com.dropbox.kaiken.skeleton.scoping.fugazi

import com.dropbox.kaiken.skeleton.core.CoroutineScopes
import com.dropbox.kaiken.skeleton.core.CoroutinesBinding
import com.dropbox.kaiken.skeleton.scoping.AppScope
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import com.squareup.anvil.annotations.ContributesTo
import dagger.Binds
import dagger.Module
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import javax.inject.Inject

class FakeCoroutineScopes @Inject constructor() : CoroutineScopes {
    override val mainScope: CoroutineScope
        get() = GlobalScope
    override val globalScope: CoroutineScope
        get() = GlobalScope
}

@ContributesTo(AppScope::class, replaces = [CoroutinesBinding::class])
@Module
abstract class FakeCoroutinesBinding {
    @SingleIn(AppScope::class)
    @Binds
    abstract fun bindCoroutineBinding(real: FakeCoroutineScopes): CoroutineScopes
}
