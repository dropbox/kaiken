package com.dropbox.kaiken.skeleton.scoping.fugazi

import com.dropbox.kaiken.scoping.AppTeardownHelper
import com.dropbox.kaiken.skeleton.scoping.AuthOptionalScope
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AuthOptionalScope::class)
interface AppTeardownHelperProvider {
    fun appTeardownHelper(): AppTeardownHelper
}
