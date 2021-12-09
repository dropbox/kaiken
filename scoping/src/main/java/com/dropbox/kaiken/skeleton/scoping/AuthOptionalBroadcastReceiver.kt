package com.dropbox.kaiken.skeleton.scoping

/**
 * An broadcast received that does not require a user to be logged in.
 */
interface AuthOptionalBroadcastReceiver : AuthAwareBroadcastReceiver {
    @JvmDefault
    override val authRequired: Boolean
        get() = false
}
