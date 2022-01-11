package com.dropbox.kaiken.skeleton.scoping.fugazi

import com.dropbox.kaiken.scoping.UserTeardownHelper
import com.dropbox.kaiken.skeleton.scoping.AuthRequiredScope
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AuthRequiredScope::class)
interface UserTeardownHelperProvider {
    fun userTeardownHelper(): UserTeardownHelper
}
