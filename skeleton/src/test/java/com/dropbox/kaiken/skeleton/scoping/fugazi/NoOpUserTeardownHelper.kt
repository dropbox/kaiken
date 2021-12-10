package com.dropbox.kaiken.skeleton.scoping.fugazi

import com.dropbox.kaiken.scoping.UserTeardownHelper
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import com.dropbox.kaiken.skeleton.scoping.UserScope
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

// You can implement your own teardown logic here.
@ContributesBinding(UserScope::class)
@SingleIn(UserScope::class)
class NoOpUserTeardownHelper @Inject constructor() : UserTeardownHelper {
    override fun teardown() {
        // No op
    }
}
