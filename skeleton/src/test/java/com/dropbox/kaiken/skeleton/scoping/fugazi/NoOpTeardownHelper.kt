package com.dropbox.kaiken.skeleton.scoping.fugazi

import com.dropbox.common.inject.AppScope
import com.dropbox.kaiken.scoping.AppTeardownHelper
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

// You can implement your own teardown logic here.
@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)
class NoOpTeardownHelper @Inject constructor() : AppTeardownHelper {
    override fun teardown() {
        // No op
    }
}
