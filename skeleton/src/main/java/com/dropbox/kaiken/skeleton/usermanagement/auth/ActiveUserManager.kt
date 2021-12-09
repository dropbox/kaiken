package com.dropbox.kaiken.skeleton.usermanagement.auth

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.dropbox.kaiken.skeleton.scoping.AppScope
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

interface ActiveUserManager {

    /**
     * Clears the active users
     *
     * @return true if the active user was set and cleared
     */
    suspend fun clearActiveUser(): Boolean

    /**
     * Sets the active users and persists to disk
     *
     */
    suspend fun setActiveUser(userId: String)

    /**
     * Get the active user from disk
     *
     * @return the active user ID, or null if none has been set
     */
    suspend fun getActiveUserId(): String?
}
@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)
class RealActiveUserManager @Inject constructor(
    private val context: Application
) : ActiveUserManager {

    init {
        require(context == context.applicationContext) {
            "Must use application context when instantiating"
        }
    }

    private val sharedPrefs: SharedPreferences by lazy {
        context.getSharedPreferences(
            ACTIVE_USER_SHARED_PREFS,
            MODE_PRIVATE
        )
    }

    override suspend fun clearActiveUser(): Boolean =
        if (getActiveUserId() != null) {
            sharedPrefs.edit().remove(KEY_ACTIVE_USER_ID).apply()
            true
        } else {
            false
        }

    override suspend fun setActiveUser(userId: String) {
        sharedPrefs.edit().putString(KEY_ACTIVE_USER_ID, userId).apply()
    }

    override suspend fun getActiveUserId(): String? =
        sharedPrefs.getString(KEY_ACTIVE_USER_ID, null)

    companion object {
        internal const val ACTIVE_USER_SHARED_PREFS = "active_user_shared_prefs"
        private const val KEY_ACTIVE_USER_ID = "key_active_user_id"
    }
}
