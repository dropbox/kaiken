package com.dropbox.kaiken.skeleton.scoping.fugazi

import com.dropbox.kaiken.skeleton.scoping.AuthRequiredActivityScope
import com.dropbox.kaiken.skeleton.scoping.UserTeardownHelper
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AuthRequiredActivityScope::class)
interface UserTeardownHelperProvider {
    fun userTeardownHelper(): UserTeardownHelper
//    fun appTeardownHelper():AppTeardownHelper
}