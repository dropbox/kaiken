package com.dropbox.kaiken.scoping

/**
 * An activity that requires a user to be logged in.
 */
interface AuthRequiredActivity : AuthAwareScopeOwnerActivity, DependencyProviderResolver {
    @JvmDefault
    override val authRequired: Boolean
        get() = true

    @JvmDefault
    fun requireViewingUserSelector(): ViewingUserSelector = checkNotNull(getViewingUserSelector())
}
