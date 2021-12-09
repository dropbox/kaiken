package com.dropbox.kaiken.skeleton.usermanagement

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart

interface ActiveUserDataSource {
    suspend fun setActiveUser(userId: String)
    suspend fun getActiveUserId(): String?
    suspend fun clearActiveUser(): Boolean
    fun getActiveUser(): Flow<String?>
}

class RealActiveUserDataSource(context: Context) : ActiveUserDataSource {
    init {
        require(context == context.applicationContext) {
            "Must use application context when instantiating"
        }
    }

    private val sharedPrefs: SharedPreferences by lazy {
        context.getSharedPreferences(
            ACTIVE_USER_SHARED_PREFS,
            Context.MODE_PRIVATE
        )
    }

    private val activeUserState = MutableStateFlow<String?>(null)

    override suspend fun clearActiveUser(): Boolean =
        if (getActiveUserId() != null) {
            sharedPrefs.edit().remove(KEY_ACTIVE_USER_ID).apply()
            activeUserState.emit(null)
            true
        } else {
            false
        }

    override fun getActiveUser(): Flow<String?> = activeUserState.onStart { emit(getActiveUserId()) }

    override suspend fun setActiveUser(userId: String) {
        sharedPrefs.edit().putString(KEY_ACTIVE_USER_ID, userId).apply()
        activeUserState.emit(userId)
    }

    override suspend fun getActiveUserId(): String? =
        sharedPrefs.getString(KEY_ACTIVE_USER_ID, null)

    companion object {
        internal const val ACTIVE_USER_SHARED_PREFS = "active_user_shared_prefs"
        private const val KEY_ACTIVE_USER_ID = "key_active_user_id"
    }
}