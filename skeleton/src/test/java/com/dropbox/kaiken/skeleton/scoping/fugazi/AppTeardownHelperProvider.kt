package com.dropbox.kaiken.skeleton.scoping.fugazi

import com.dropbox.kaiken.scoping.AppTeardownHelper
import com.dropbox.kaiken.skeleton.scoping.AuthOptionalActivityScope
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AuthOptionalActivityScope::class)
interface AppTeardownHelperProvider {
    fun appTeardownHelper(): AppTeardownHelper
}
