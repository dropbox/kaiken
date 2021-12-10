package com.dropbox.kaiken.scoping

/**
 * An broadcast received that requires a user to be logged in.
 */
interface AuthRequiredBroadcastReceiver : AuthAwareBroadcastReceiver {
    @JvmDefault
    override val authRequired: Boolean
        get() = true
}
