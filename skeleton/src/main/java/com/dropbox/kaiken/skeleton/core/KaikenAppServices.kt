package com.dropbox.kaiken.skeleton.core

import com.dropbox.kaiken.skeleton.scoping.AppScope
import com.dropbox.kaiken.skeleton.scoping.AppServices
import com.dropbox.kaiken.skeleton.usermanagement.UserManager
import com.squareup.anvil.annotations.ContributesTo

/**
 * Holds the common dependencies for [AAppScope]
 * The classes defined in this interface will be available in all Skeleton apps components that merge with [AAppScope]
 */
@ContributesTo(AppScope::class)
interface KaikenAppServices : AppServices {
    fun userManager(): UserManager
}
