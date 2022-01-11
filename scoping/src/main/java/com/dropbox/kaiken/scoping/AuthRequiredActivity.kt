package com.dropbox.kaiken.scoping

/**
 * An activity that requires a user to be logged in.
 */
interface AuthRequiredActivity : AuthAwareScopeOwnerActivity, DependencyProviderResolver {
    override val authRequired: Boolean
        get() = true

    fun requireViewingUserSelector(): ViewingUserSelector = checkNotNull(getViewingUserSelector())
}
