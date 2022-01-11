package com.dropbox.kaiken.scoping

/**
 * A fragment that does not require a user to be logged in.
 *
 * Avoid using this class. This should only be used when it's not possible for the parent activity
 * to be an [AuthAwareScopeOwnerActivity].
 */
interface AuthOptionalFragment : AuthAwareScopeOwnerFragment {
    override val authRequired: Boolean
        get() = false
}
