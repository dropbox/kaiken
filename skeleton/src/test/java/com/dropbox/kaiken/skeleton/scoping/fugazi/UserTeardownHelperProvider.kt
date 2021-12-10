package com.dropbox.kaiken.skeleton.scoping.fugazi

import com.dropbox.kaiken.scoping.UserTeardownHelper
import com.dropbox.kaiken.skeleton.scoping.AuthRequiredActivityScope
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AuthRequiredActivityScope::class)
interface UserTeardownHelperProvider {
    fun userTeardownHelper(): UserTeardownHelper
}
