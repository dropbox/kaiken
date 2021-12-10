package com.dropbox.kaiken.scoping

import android.content.Context
import android.content.Intent
import com.dropbox.kaiken.scoping.internal.RealAuthHelper
import com.dropbox.kaiken.scoping.internal.locateScopedServicesProvider

interface AuthAwareBroadcastReceiver {

    val authRequired: Boolean

    /**
     * Resolves the dependency provider for this broadcast receiver.
     *
     * If the auth is invalid, this method will return `null`.
     *
     * Should be called from [BroadcastReceiver#onReceive].
     */
    @JvmDefault
    fun <T> onReceiveResolveDependencyProvider(context: Context, intent: Intent): T? {
        val scopedServicesProvider = this.locateScopedServicesProvider(context)
        val viewingUserSelector = intent.getViewingUserSelector()

        val authHelper = RealAuthHelper(scopedServicesProvider, viewingUserSelector, authRequired)

        return if (authHelper.validateAuth()) {
            authHelper.resolveDependencyProvider()
        } else {
            null
        }
    }
}
