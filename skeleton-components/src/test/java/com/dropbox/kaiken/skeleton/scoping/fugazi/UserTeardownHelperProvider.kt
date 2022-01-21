package com.dropbox.kaiken.skeleton.scoping.fugazi

import com.dropbox.common.inject.AuthRequiredScope
import com.dropbox.kaiken.scoping.UserTeardownHelper
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AuthRequiredScope::class)
interface UserTeardownHelperProvider {
    fun userTeardownHelper(): UserTeardownHelper
}
