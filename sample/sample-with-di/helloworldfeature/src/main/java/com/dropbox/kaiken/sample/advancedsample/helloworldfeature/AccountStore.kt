package com.dropbox.kaiken.sample.advancedsample.helloworldfeature

import com.dropbox.common.inject.AppScope
import com.dropbox.kaiken.skeleton.scoping.SingleIn
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

interface AccountStore {
    suspend fun addUser(user: DiSampleUser)
    suspend fun removeUser(userId: String)
    fun getAllUsers(): Flow<Set<DiSampleUser>>
}

@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)
class RealAccountStore @Inject constructor() : AccountStore {

    private var persistence = setOf<DiSampleUser>()
    private val users = MutableStateFlow<Set<DiSampleUser>>(emptySet())

    override suspend fun addUser(user: DiSampleUser) {
        persistence = persistence.plus(user)
        users.emit(persistence)
    }

    override suspend fun removeUser(userId: String) {
        persistence.firstOrNull { it.userId == userId }?.let {
            persistence = persistence.minus(it)
        }
        users.emit(persistence)
    }

    override fun getAllUsers(): Flow<Set<DiSampleUser>> = users
}
