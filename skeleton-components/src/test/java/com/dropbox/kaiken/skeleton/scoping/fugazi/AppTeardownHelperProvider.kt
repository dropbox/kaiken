package com.dropbox.kaiken.skeleton.scoping.fugazi

import com.dropbox.common.inject.AuthOptionalScope
import com.dropbox.kaiken.scoping.AppTeardownHelper
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AuthOptionalScope::class)
interface AppTeardownHelperProvider {
    fun appTeardownHelper(): AppTeardownHelper
}
