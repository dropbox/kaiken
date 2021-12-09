package com.dropbox.kaiken.skeleton.scoping.fugazi

import com.dropbox.kaiken.skeleton.scoping.AppScope
import com.dropbox.kaiken.skeleton.usermanagement.auth.ActiveUserManager
import com.dropbox.kaiken.skeleton.usermanagement.auth.RealActiveUserManager
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppScope::class, replaces = [RealActiveUserManager::class])
class FakeActiveUserManager @Inject constructor() : ActiveUserManager {
    override suspend fun clearActiveUser(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun setActiveUser(userId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getActiveUserId(): String? {
        TODO("Not yet implemented")
    }
}