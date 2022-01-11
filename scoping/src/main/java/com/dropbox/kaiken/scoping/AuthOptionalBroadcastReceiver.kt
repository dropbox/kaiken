package com.dropbox.kaiken.scoping

/**
 * An broadcast received that does not require a user to be logged in.
 */
interface AuthOptionalBroadcastReceiver : AuthAwareBroadcastReceiver {
    override val authRequired: Boolean
        get() = false
}
