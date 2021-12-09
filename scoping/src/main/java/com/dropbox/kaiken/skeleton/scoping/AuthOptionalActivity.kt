package com.dropbox.kaiken.skeleton.scoping

/**
 * An activity that does not require a user to be logged in.
 */
interface AuthOptionalActivity : AuthAwareScopeOwnerActivity, DependencyProviderResolver {
    @JvmDefault
    override val authRequired: Boolean
        get() = false
}
