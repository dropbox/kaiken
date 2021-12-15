package com.dropbox.kaiken.sample.advancedsample.helloworldfeature

import com.dropbox.kaiken.skeleton.scoping.AppScope
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

    private val persistence = mutableSetOf<DiSampleUser>()
    private val users = MutableStateFlow<Set<DiSampleUser>>(emptySet())

    override suspend fun addUser(user: DiSampleUser) {
        persistence.add(user)
        users.emit(persistence)
    }

    override suspend fun removeUser(userId: String) {
        persistence.forEach {
            if (it.userId == userId) {
                persistence.remove(it)
            }
        }
        users.emit(persistence)
    }

    override fun getAllUsers(): Flow<Set<DiSampleUser>> = users
}
