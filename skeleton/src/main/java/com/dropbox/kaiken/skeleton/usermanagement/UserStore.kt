package com.dropbox.kaiken.skeleton.usermanagement

import com.dropbox.kaiken.skeleton.scoping.AppScope
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.scan
import javax.inject.Inject

/**
 * Manages the set of authenticated users for an app.
 */
interface UserStore {
    /**
     * Provides a flow of the [UsersEvent].  Observe this for changes related to users added, removed, and active users updated.
     *
     */
    fun getUserEvents(): Flow<UsersEvent<User>>

    suspend fun getUserById(userId: String): User?
}

@ExperimentalCoroutinesApi
@ContributesBinding(AppScope::class)
class RealUserStore @Inject constructor(
    private val userDataSource: UserDataSource<User>,
    private val activeUserDataSource: ActiveUserDataSource,
) : UserStore {

    override fun getUserEvents(): Flow<UsersEvent<User>> =
        activeUserDataSource.getActiveUser()
            .combine(userDataSource.getAllUsers()) { activeUserId, users ->
                Pair(activeUserId, users)
            }
            .scan(UsersEvent(emptySet())) { prev, next ->
                val users = next.second
                val usersRemoved = prev.users.minusById(users)
                val usersAdded = users.minusById(prev.users)

                UsersEvent(users, usersAdded, usersRemoved, next.first)
            }
            .drop(1)

    override suspend fun getUserById(userId: String): User? {
        return userDataSource.getAllUsers().first().firstOrNull { it.userId == userId }
    }
}

internal fun <T : User> Set<T>.minusById(elements: Set<T>): Set<T> {
    val result = toMutableSet()
    forEach { item ->
        if (elements.any { item.userId == it.userId }) {
            result.remove(item)
        }
    }
    return result
}
